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
import java.lang.System.console

private var currentFragment = ""

class HomeActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        // ✅ setup navigation drawer (hamburger)
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

        var patientCount = 0
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
        var fabAdd = findViewById<Button>(R.id.fabAdd)

        // initial state

        val addPatientLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                lastPatientName = data?.getStringExtra("patientName")
                lastPatientAge = data?.getStringExtra("patientAge")
                lastPatientPhone = data?.getStringExtra("patientPhone")
                lastPatientCondition = data?.getStringExtra("patientCondition")

            }
        }

        // fab button opens AddPatientActivity
        fabAdd.setOnClickListener {
            val intent = Intent(this, AddPatientActivity::class.java)
            addPatientLauncher.launch(intent)
        }

        // edit button reopens AddPatientActivity with existing info

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ///////////// button logic

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        patientsBtn.setOnClickListener {
            if (currentFragment != "patients") {
                currentFragment = "patients"
                val viewPatientsFragment = ViewPatients()
                fragmentTransaction.replace(R.id.fragment_container, viewPatientsFragment)
                fragmentTransaction.commit()
            }
        }



    }
}
