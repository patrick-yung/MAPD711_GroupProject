package com.mapd711_groupproject

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
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
        GlobalScope.launch(Dispatchers.IO) {
            try {
                Log.d("ViewAppointments", "Fetching from: $APPOINTMENTS_URL")

                val url = URL(APPOINTMENTS_URL)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                val responseCode = connection.responseCode
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    Log.e("ViewAppointments", "HTTP error: $responseCode")
                    showError(R.string.appointments_loading_error)
                    return@launch
                }

                val response = connection.inputStream.bufferedReader().use { it.readText() }
                Log.d("ViewAppointments", "Response => $response")

                val jsonArray = JSONArray(response)

                if (jsonArray.length() == 0) {
                    showError(R.string.appointments_empty)
                    return@launch
                }

                val appointments = mutableListOf<Appointment>()

                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)

                    val appt = Appointment(
                        id = obj.optString("_id", ""),
                        patientName = obj.optString("patientName", "Unknown"),
                        doctorName = obj.optString("doctorName", "Unknown"),
                        appointmentDate = obj.optString("appointmentDate", "Unknown date"),
                        reason = obj.optString("reason", "No reason"),
                        status = obj.optString("status", "Scheduled")
                    )

                    appointments.add(appt)
                }

                withContext(Dispatchers.Main) {
                    recyclerView.adapter = AppointmentAdapter(appointments)
                }

            } catch (e: Exception) {
                Log.e("ViewAppointments", "Exception: ${e.message}", e)
                showError(R.string.appointments_loading_error)
            }
        }
    }

    private suspend fun showError(stringResId: Int) {
        withContext(Dispatchers.Main) {
            val message = getString(stringResId)

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
