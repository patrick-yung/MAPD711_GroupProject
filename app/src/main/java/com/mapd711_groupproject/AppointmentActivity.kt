package com.mapd711_groupproject

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class AppointmentActivity : AppCompatActivity() {

    private val POST_URL = Globals.BASE_URL+"/appointments"




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment)

        val etName = findViewById<EditText>(R.id.etName)
        val etDoctor = findViewById<EditText>(R.id.etDoctor)
        val etDate = findViewById<EditText>(R.id.etDate)
        val etTime = findViewById<EditText>(R.id.etTime)
        val etReason = findViewById<EditText>(R.id.etReason)
        val btnSave = findViewById<Button>(R.id.btnSave)

        // DATE PICKER
        etDate.setOnClickListener {
            val picker = DatePickerDialog(this)
            picker.setOnDateSetListener { _, year, month, day ->
                etDate.setText("$day/${month + 1}/$year")
            }
            picker.show()
        }

        // TIME PICKER
        etTime.setOnClickListener {
            val picker = TimePickerDialog(
                this,
                { _, hour, minute ->
                    val amPm = if (hour >= 12) "PM" else "AM"
                    val hour12 = if (hour % 12 == 0) 12 else hour % 12
                    etTime.setText(String.format("%02d:%02d %s", hour12, minute, amPm))
                },
                9, 0, false
            )
            picker.show()
        }

        // SAVE BUTTON LOGIC
        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val doctor = etDoctor.text.toString().trim()
            val date = etDate.text.toString().trim()
            val time = etTime.text.toString().trim()
            val reason = etReason.text.toString().trim()

            if (name.isEmpty() || doctor.isEmpty() || date.isEmpty() || time.isEmpty() || reason.isEmpty()) {
                Toast.makeText(this, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            sendAppointmentToServer(name, doctor, date, time, reason)
        }
    }

    private fun sendAppointmentToServer(name: String, doctor: String, date: String, time: String, reason: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val url = URL(POST_URL)
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json")
                conn.doOutput = true

                val jsonBody = JSONObject().apply {
                    put("patientName", name)
                    put("doctorName", doctor)
                    put("appointmentDate", "$date $time")
                    put("reason", reason)
                    put("status", "Scheduled")
                }

                val writer = OutputStreamWriter(conn.outputStream)
                writer.write(jsonBody.toString())
                writer.flush()

                val code = conn.responseCode

                withContext(Dispatchers.Main) {
                    if (code in 200..299) {
                        Toast.makeText(this@AppointmentActivity, "Appointment saved!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@AppointmentActivity, "Error saving appointment.", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (e: Exception) {
                Log.e("APPOINTMENT_ERROR", e.message ?: "unknown error")
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AppointmentActivity, "Error saving appointment.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
