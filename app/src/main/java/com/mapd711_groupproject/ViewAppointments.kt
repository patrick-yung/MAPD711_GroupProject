package com.mapd711_grouppropproject

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

class ViewAppointments : Fragment() {

    private lateinit var recyclerView: RecyclerView

    private val APPOINTMENTS_URL =
        "https://mapd713-group-project-2.onrender.com/appointments"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_view_appointments, container, false)
        recyclerView = view.findViewById(R.id.appointmentsRecyclerView)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        loadAppointments()

        return view
    }

    private fun loadAppointments() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("Appointments", "Fetching from: $APPOINTMENTS_URL")

                val url = URL(APPOINTMENTS_URL)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                val responseCode = connection.responseCode
                if (responseCode != 200) {
                    Log.e("Appointments", "Error: HTTP $responseCode")
                    showError(getString(R.string.appointments_loading_error))
                    return@launch
                }

                val response = connection.inputStream.bufferedReader().readText()
                Log.d("Appointments", "Response => $response")

                val jsonArray = JSONArray(response)

                if (jsonArray.length() == 0) {
                    showError(getString(R.string.appointments_empty))
                    return@launch
                }

                val appointments = mutableListOf<Appointment>()

                for (i in 0 until jsonArray.length()) {
                    val item = jsonArray.getJSONObject(i)

                    val appt = Appointment(
                        id = item.optString("_id"),
                        patientName = item.optString("patientName"),
                        doctorName = item.optString("doctorName"),
                        appointmentDate = item.optString("appointmentDate"),
                        reason = item.optString("reason"),
                        status = item.optString("status")
                    )

                    appointments.add(appt)
                }

                withContext(Dispatchers.Main) {
                    recyclerView.adapter = AppointmentAdapter(appointments)
                }

            } catch (e: Exception) {
                Log.e("Appointments", "Exception: ${e.message}")
                showError(getString(R.string.appointments_loading_error))
            }
        }
    }

    private suspend fun showError(message: String) {
        withContext(Dispatchers.Main) {
            val fallbackList = listOf(
                Appointment(
                    id = "-1",
                    patientName = message,
                    doctorName = "",
                    appointmentDate = "",
                    reason = "",
                    status = ""
                )
            )

            recyclerView.adapter = AppointmentAdapter(fallbackList)
        }
    }
}
