package com.mapd711_groupproject

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AppointmentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_appointment)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        var patientName = findViewById<EditText>(R.id.etName)
        var date = findViewById<EditText>(R.id.etDate)
        var time = findViewById<EditText>(R.id.etTime)
        var reason = findViewById<EditText>(R.id.etReason)

        var btnSave = findViewById<Button>(R.id.btnSave)

        //information will be save as shared preference
        var sharedPreferences = getSharedPreferences("appointment_info", MODE_PRIVATE)
        var editor = sharedPreferences.edit()


        //save button will save the appointment information and show a toast message
        btnSave.setOnClickListener {
            //validation for patient name, date, time and reason cannot be empty
            if (patientName.text.toString().isEmpty() || date.text.toString().isEmpty()
                || time.text.toString().isEmpty() || reason.text.toString().isEmpty()
            ) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            editor.putString("patientName", patientName.text.toString())
            editor.putString("date", date.text.toString())
            editor.putString("time", time.text.toString())
            editor.putString("reason", reason.text.toString())
            editor.apply()
            Toast.makeText(this, "Appointment saved", Toast.LENGTH_SHORT).show()
            patientName.text.clear()
            date.text.clear()
            time.text.clear()
            reason.text.clear()

            var patientNameText = sharedPreferences.getString("patientName", "")
            var dateText = sharedPreferences.getString("date", "")
            var timeText = sharedPreferences.getString("time", "")
            var reasonText = sharedPreferences.getString("reason", "")

            // on click save button it will show the appointment information
            findViewById<TextView>(R.id.tvPatientName).text = "Patient Name: $patientNameText"
            findViewById<TextView>(R.id.tvDate).text = "Date: $dateText"
            findViewById<TextView>(R.id.tvTime).text = "Time: $timeText"
            findViewById<TextView>(R.id.tvReason).text = "Reason: $reasonText"

            //visibility will be visible
            findViewById<TextView>(R.id.tvPatientName).visibility = TextView.VISIBLE
            findViewById<TextView>(R.id.tvDate).visibility = TextView.VISIBLE
            findViewById<TextView>(R.id.tvTime).visibility = TextView.VISIBLE
            findViewById<TextView>(R.id.tvReason).visibility = TextView.VISIBLE
            //visibility will be gone
            findViewById<TextView>(R.id.tvSavedAppointment).visibility = TextView.GONE

        }

    }
}