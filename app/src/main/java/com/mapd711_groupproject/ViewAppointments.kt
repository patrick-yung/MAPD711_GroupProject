package com.mapd711_groupproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import com.mapd711_groupproject.Globals

class ViewAppointments : Fragment() {

    private val TAG = "ViewAppointments"

    private val APPOINTMENTS_URL ="https://mapd713-group-project-2.onrender.com/appointments"

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AppointmentAdapter
    private val appointments = mutableListOf<Appointment>()

    private val uiScope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_view_appointments, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.appointmentsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = AppointmentAdapter(
            appointments,
            requireContext(),
            onUpdateRequested = { updated ->
                updateAppointmentOnServer(updated)
            },
            // 🔹 Called when status is set to "Cancelled" in the dialog
            onDeleteRequested = { appt, pos ->
                animateAndDelete(appt, pos)
            },
            onAttended = { appt ->
                openAddPatientWithName(appt.patientName)
            }
        )
        recyclerView.adapter = adapter

        attachSwipeToDelete()
        fetchAppointments()
    }

    // --------------------------------------------------------------------
    // FETCH ALL APPOINTMENTS
    // --------------------------------------------------------------------
    private fun fetchAppointments() {
        uiScope.launch(Dispatchers.IO) {
            try {
                val url = URL(APPOINTMENTS_URL)
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"

                val code = conn.responseCode
                Log.d(TAG, "GET /appointments -> $code")

                if (code in 200..299) {
                    val text = conn.inputStream.bufferedReader().use { it.readText() }
                    val jsonArray = JSONArray(text)

                    val list = mutableListOf<Appointment>()
                    for (i in 0 until jsonArray.length()) {
                        list.add(parseAppointment(jsonArray.getJSONObject(i)))
                    }

                    withContext(Dispatchers.Main) {
                        adapter.replaceAll(list)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            requireContext(),
                            "Failed to load appointments",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error fetching appointments: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Error loading appointments",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    // --------------------------------------------------------------------
    // PARSE JSON OBJECT -> Appointment model
    // Handles both id & _id safely
    // --------------------------------------------------------------------
    private fun parseAppointment(obj: JSONObject): Appointment {
        val id = obj.optString("_id", obj.optString("id", ""))

        return Appointment(
            id = id,
            patientName = obj.optString("patientName", ""),
            doctorName = obj.optString("doctorName", ""),
            appointmentDate = obj.optString("appointmentDate", ""),
            reason = obj.optString("reason", ""),
            status = obj.optString("status", "Scheduled"),
            isEmergency = obj.optBoolean("isEmergency", false)
        )
    }

    // --------------------------------------------------------------------
    // UPDATE APPOINTMENT (for non-cancelled statuses)
    // --------------------------------------------------------------------
    private fun updateAppointmentOnServer(updated: Appointment) {
        uiScope.launch(Dispatchers.IO) {
            try {
                if (updated.id.isBlank() || updated.id == "-1") {
                    Log.w(TAG, "Skipping update for placeholder appointment")
                    return@launch
                }

                val url = URL("$APPOINTMENTS_URL/${updated.id}")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "PUT"
                conn.setRequestProperty("Content-Type", "application/json")
                conn.doOutput = true

                // MUST send full JSON so backend updates correctly
                val body = JSONObject().apply {
                    put("_id", updated.id)  // REQUIRED FOR your backend
                    put("patientName", updated.patientName)
                    put("doctorName", updated.doctorName)
                    put("appointmentDate", updated.appointmentDate)
                    put("reason", updated.reason)
                    put("status", updated.status)
                    put("isEmergency", updated.isEmergency)
                }

                conn.outputStream.bufferedWriter().use { it.write(body.toString()) }

                val responseCode = conn.responseCode
                Log.d(TAG, "PUT /appointments/${updated.id} => $responseCode")

                withContext(Dispatchers.Main) {
                    if (responseCode == 200 || responseCode == 201 || responseCode == 202 || responseCode == 204) {
                        adapter.updateItem(updated)
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.appointment_updated),
                            Toast.LENGTH_SHORT
                        ).show()

                        // Tell HomeActivity to refresh dashboard
                        parentFragmentManager.setFragmentResult("refresh_home", Bundle())
                    } else {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.appointment_update_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error updating appointment: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.appointment_update_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    // --------------------------------------------------------------------
    // ANIMATE + DELETE when status was set to "Cancelled"
    // --------------------------------------------------------------------
    private fun animateAndDelete(appt: Appointment, pos: Int) {
        val holder = recyclerView.findViewHolderForAdapterPosition(pos)
        if (holder != null) {
            holder.itemView.animate()
                .translationX(holder.itemView.width.toFloat())
                .setDuration(300)
                .withEndAction {
                    deleteAppointmentOnServer(appt, pos)
                }
        } else {
            // Fallback if no holder (off-screen)
            deleteAppointmentOnServer(appt, pos)
        }
    }

    // --------------------------------------------------------------------
    // DELETE APPOINTMENT (used by swipe + cancelled flow)
    // --------------------------------------------------------------------
    private fun deleteAppointmentOnServer(appt: Appointment, pos: Int) {
        uiScope.launch(Dispatchers.IO) {
            try {
                val url = URL("$APPOINTMENTS_URL/${appt.id}")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "DELETE"

                val code = conn.responseCode
                Log.d(TAG, "DELETE /appointments/${appt.id} -> $code")

                withContext(Dispatchers.Main) {
                    if (code in 200..299) {
                        adapter.removeAt(pos)
                        Toast.makeText(requireContext(), "Deleted", Toast.LENGTH_SHORT).show()

                        // Tell HomeActivity to refresh dashboard
                        parentFragmentManager.setFragmentResult("refresh_home", Bundle())
                    } else {
                        adapter.notifyItemChanged(pos)
                        Toast.makeText(
                            requireContext(),
                            "Delete failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            } catch (e: Exception) {
                Log.e(TAG, "Delete error: ${e.message}")
                withContext(Dispatchers.Main) {
                    adapter.notifyItemChanged(pos)
                    Toast.makeText(
                        requireContext(),
                        "Delete failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    // --------------------------------------------------------------------
    // SWIPE TO DELETE
    // --------------------------------------------------------------------
    private fun attachSwipeToDelete() {
        val cb = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                rv: RecyclerView,
                vh: RecyclerView.ViewHolder,
                t: RecyclerView.ViewHolder
            ) = false

            override fun onSwiped(vh: RecyclerView.ViewHolder, dir: Int) {
                val pos = vh.bindingAdapterPosition
                val appt = adapter.getItemAt(pos)

                if (appt.id.isBlank() || appt.id == "-1") {
                    adapter.notifyItemChanged(pos)
                    return
                }

                deleteAppointmentOnServer(appt, pos)
            }
        }

        ItemTouchHelper(cb).attachToRecyclerView(recyclerView)
    }

    // --------------------------------------------------------------------
    // IF STATUS == ATTENDED → pre-fill Add Patient
    // --------------------------------------------------------------------
    private fun openAddPatientWithName(name: String) {
        val intent = Intent(requireContext(), AddPatientActivity::class.java)
        intent.putExtra("fromAppointmentName", name)
        startActivity(intent)
    }
}
