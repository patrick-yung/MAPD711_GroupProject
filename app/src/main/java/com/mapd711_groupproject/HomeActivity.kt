package com.mapd711_groupproject

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class HomeActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        setupDrawer(R.id.nav_home)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var lastPatientName = intent.getStringExtra("patientName")
        var lastPatientAge = intent.getStringExtra("patientAge")
        var lastPatientPhone = intent.getStringExtra("patientPhone")
        var lastPatientCondition = intent.getStringExtra("patientCondition")

        var patientCount = 1258
        fun addPatient() { patientCount++ }
        var appointmentCount = 50
        fun addAppointment() { appointmentCount++ }

        var patientsBtn = findViewById<Button>(R.id.button5)
        var criticalBtn = findViewById<Button>(R.id.button4)
        var clinicTestBtn = findViewById<Button>(R.id.button6)
        var appointmentBtn = findViewById<Button>(R.id.button7)
        var patientsInfo = findViewById<TextView>(R.id.textView7)
        var fabAdd = findViewById<Button>(R.id.fabAdd)
        var buttonEdit = findViewById<ImageButton>(R.id.buttonEdit)

        buttonEdit.visibility = View.GONE
        patientsInfo.text = "Patients: $patientCount"

        val addPatientLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                lastPatientName = data?.getStringExtra("patientName")
                lastPatientAge = data?.getStringExtra("patientAge")
                lastPatientPhone = data?.getStringExtra("patientPhone")
                lastPatientCondition = data?.getStringExtra("patientCondition")
                if (lastPatientName != null) {
                    patientsInfo.text = """
                        Name: $lastPatientName
                        Age: $lastPatientAge
                        Phone: $lastPatientPhone
                        Issues: $lastPatientCondition
                    """.trimIndent()
                    buttonEdit.visibility = View.VISIBLE
                } else {
                    patientsInfo.text = "No patient data available"
                    buttonEdit.visibility = View.GONE
                }
            }
        }

        fabAdd.setOnClickListener {
            val intent = Intent(this, AddPatientActivity::class.java)
            addPatientLauncher.launch(intent)
        }

        buttonEdit.setOnClickListener {
            if (lastPatientName != null) {
                val intent = Intent(this, AddPatientActivity::class.java)
                intent.putExtra("isEdit", true)
                intent.putExtra("patientName", lastPatientName)
                intent.putExtra("patientAge", lastPatientAge)
                intent.putExtra("patientPhone", lastPatientPhone)
                intent.putExtra("patientCondition", lastPatientCondition)
                addPatientLauncher.launch(intent)
            }
        }

        patientsBtn.setOnClickListener {
            addPatient()
            patientsInfo.text = "Patients: $patientCount"
        }
        criticalBtn.setOnClickListener { patientsInfo.text = "Critical" }
        clinicTestBtn.setOnClickListener { patientsInfo.text = "Clinic Test" }
        appointmentBtn.setOnClickListener { patientsInfo.text = "Appointment" }
    }
}
