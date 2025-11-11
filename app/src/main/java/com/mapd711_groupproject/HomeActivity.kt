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
<<<<<<< HEAD
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class HomeActivity : BaseActivity() {
=======
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class HomeActivity : AppCompatActivity() {
>>>>>>> 9fbd727a9c42aef0b94fdce1dc2f347d6bfdb20b
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
<<<<<<< HEAD
        setupDrawer(R.id.nav_home)

=======
>>>>>>> 9fbd727a9c42aef0b94fdce1dc2f347d6bfdb20b
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
<<<<<<< HEAD

=======
>>>>>>> 9fbd727a9c42aef0b94fdce1dc2f347d6bfdb20b
        var lastPatientName = intent.getStringExtra("patientName")
        var lastPatientAge = intent.getStringExtra("patientAge")
        var lastPatientPhone = intent.getStringExtra("patientPhone")
        var lastPatientCondition = intent.getStringExtra("patientCondition")

<<<<<<< HEAD
        var patientCount = 1258
        fun addPatient() { patientCount++ }
        var appointmentCount = 50
        fun addAppointment() { appointmentCount++ }
=======

        var patientCount = 1258
        //patient count will add patient to patient count
        fun addPatient() {
            patientCount++
        }

        var appointmentCount = 50
        fun addAppointment() {
            appointmentCount++
        }
>>>>>>> 9fbd727a9c42aef0b94fdce1dc2f347d6bfdb20b

        var patientsBtn = findViewById<Button>(R.id.button5)
        var criticalBtn = findViewById<Button>(R.id.button4)
        var clinicTestBtn = findViewById<Button>(R.id.button6)
        var appointmentBtn = findViewById<Button>(R.id.button7)
        var patientsInfo = findViewById<TextView>(R.id.textView7)
        var fabAdd = findViewById<Button>(R.id.fabAdd)
<<<<<<< HEAD
        var buttonEdit = findViewById<ImageButton>(R.id.buttonEdit)

        buttonEdit.visibility = View.GONE
        patientsInfo.text = "Patients: $patientCount"

=======
        var buttonEdit = findViewById< ImageButton>(R.id.buttonEdit)

        //initial state
        buttonEdit.visibility = View.GONE
        patientsInfo.text = "Patients: $patientCount"


>>>>>>> 9fbd727a9c42aef0b94fdce1dc2f347d6bfdb20b
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
<<<<<<< HEAD
                        Name: $lastPatientName
                        Age: $lastPatientAge
                        Phone: $lastPatientPhone
                        Issues: $lastPatientCondition
                    """.trimIndent()
=======
                          Name: $lastPatientName
                          Age: $lastPatientAge
                          Phone: $lastPatientPhone
                          Issues: $lastPatientCondition
                          """.trimIndent()
>>>>>>> 9fbd727a9c42aef0b94fdce1dc2f347d6bfdb20b
                    buttonEdit.visibility = View.VISIBLE
                } else {
                    patientsInfo.text = "No patient data available"
                    buttonEdit.visibility = View.GONE
                }
            }
        }

<<<<<<< HEAD
=======
        //fab button will launch add patient activity
>>>>>>> 9fbd727a9c42aef0b94fdce1dc2f347d6bfdb20b
        fabAdd.setOnClickListener {
            val intent = Intent(this, AddPatientActivity::class.java)
            addPatientLauncher.launch(intent)
        }

<<<<<<< HEAD
=======
        //when click on the button edit it will keep this patient information in the add patient screen
>>>>>>> 9fbd727a9c42aef0b94fdce1dc2f347d6bfdb20b
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

<<<<<<< HEAD
        patientsBtn.setOnClickListener {
            addPatient()
            patientsInfo.text = "Patients: $patientCount"
        }
        criticalBtn.setOnClickListener { patientsInfo.text = "Critical" }
        clinicTestBtn.setOnClickListener { patientsInfo.text = "Clinic Test" }
        appointmentBtn.setOnClickListener { patientsInfo.text = "Appointment" }
    }
}
=======
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
>>>>>>> 9fbd727a9c42aef0b94fdce1dc2f347d6bfdb20b
