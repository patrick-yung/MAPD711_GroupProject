package com.mapd711_groupproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        var patientCount = 1258
        //patient count will add patient to patient count
        fun addPatient() {
            patientCount++
        }

        var appointmentCount = 50
        fun addAppointment() {
            appointmentCount++
        }

        var patientsBtn = findViewById<Button>(R.id.button5)
        var criticalBtn = findViewById<Button>(R.id.button6)
        var clinicTestBtn = findViewById<Button>(R.id.button4)
        var appointmentBtn = findViewById<Button>(R.id.button7)
        var patientsInfo = findViewById<TextView>(R.id.textView7)
        var fabAdd = findViewById<Button>(R.id.fabAdd)


        //patients button will show text in patientsInfo
        patientsBtn.setOnClickListener {
            patientsInfo.text = "Patients"

        }

        //critical button will display text in patientsInfo
        criticalBtn.setOnClickListener {
            patientsInfo.text = "Critical"
        }

        //clinic test button will display text in patientsInfo
        clinicTestBtn.setOnClickListener {
            patientsInfo.text = "Clinic Test"
        }

       //appointment button will show text in patientsInfo
        appointmentBtn.setOnClickListener {
            patientsInfo.text = "Appointment"

        }

        //fab button will open add patient activity
        fabAdd.setOnClickListener {
            val intent = Intent(this, AddPatientActivity::class.java)
            startActivity(intent)
        }
    }
}