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

        var lastPatientName = intent.getStringExtra("patientName")
        var lastPatientAge = intent.getStringExtra("patientAge")
        var lastPatientGender = intent.getStringExtra("patientGender")
        var lastPatientPhone = intent.getStringExtra("patientPhone")
        var lastPatientAddress = intent.getStringExtra("patientAddress")
        var lastPatientCondition = intent.getStringExtra("patientCondition")


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
        var buttonEdit = findViewById< ImageButton>(R.id.buttonEdit)

        //initial state
        buttonEdit.visibility = View.GONE
        patientsInfo.text = "Patients: $patientCount"


        val addPatientLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                lastPatientName = data?.getStringExtra("patientName")
                lastPatientAge = data?.getStringExtra("patientAge")
                lastPatientGender = data?.getStringExtra("patientGender")
                lastPatientPhone = data?.getStringExtra("patientPhone")
                lastPatientAddress = data?.getStringExtra("patientAddress")
                lastPatientCondition = data?.getStringExtra("patientCondition")
                if (lastPatientName != null) {
                    patientsInfo.text = """
                          Name: $lastPatientName
                          Age: $lastPatientAge
                          Gender: $lastPatientGender
                          Address: $lastPatientAddress
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

        //fab button will launch add patient activity
        fabAdd.setOnClickListener {
            val intent = Intent(this, AddPatientActivity::class.java)
            addPatientLauncher.launch(intent)
        }

        //when click on the button edit it will keep this patient information in the add patient screen
        buttonEdit.setOnClickListener {
            if (lastPatientName != null) {
                val intent = Intent(this, AddPatientActivity::class.java)
                intent.putExtra("isEdit", true)
                intent.putExtra("patientName", lastPatientName)
                intent.putExtra("patientAge", lastPatientAge)
                intent.putExtra("patientGender", lastPatientGender)
                intent.putExtra("patientPhone", lastPatientPhone)
                intent.putExtra("patientAddress", lastPatientAddress)
                intent.putExtra("patientCondition", lastPatientCondition)
                addPatientLauncher.launch(intent)
            }
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

        //clinic test button will go to clinical test activity
        clinicTestBtn.setOnClickListener {
            val intent = Intent(this, ClinicalTestActivity::class.java)
            startActivity(intent)
        }

       //click on appointment button it will go appointment activity
        appointmentBtn.setOnClickListener {
            val intent = Intent(this, AppointmentActivity::class.java)
            startActivity(intent)
        }
    }

}