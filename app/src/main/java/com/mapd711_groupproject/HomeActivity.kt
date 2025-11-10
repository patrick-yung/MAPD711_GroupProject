package com.mapd711_groupproject

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
        var criticalBtn = findViewById<Button>(R.id.button4)
        var clinicTestBtn = findViewById<Button>(R.id.button6)
        var appointmentBtn = findViewById<Button>(R.id.button7)
        var patientsInfo = findViewById<TextView>(R.id.textView7)
        var fabAdd = findViewById<Button>(R.id.fabAdd)

        val addPatientLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val name = data?.getStringExtra("patientName")
                val age = data?.getStringExtra("patientAge")
                val phone = data?.getStringExtra("patientPhone")
                val condition = data?.getStringExtra("patientCondition")
                patientsInfo.text = """
                          Name: $name
                          Age: $age
                          Phone: $phone
                          Issues: $condition
                          """.trimIndent()

            }
        }

        fabAdd.setOnClickListener {
            val intent = Intent(this, AddPatientActivity::class.java)
            addPatientLauncher.launch(intent)
        }


        //patients button will show
        patientsBtn.setOnClickListener {
            addPatient()
            patientsInfo.text = "Patients: $patientCount"

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

    }
}