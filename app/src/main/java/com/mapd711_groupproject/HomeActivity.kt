package com.mapd711_groupproject

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton


class HomeActivity : BaseActivity() {

    private var currentFragment = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        // 🔹 Setup navigation drawer (hamburger)
        setupDrawer(R.id.nav_home)

        // 🔹 Handles top/bottom safe-area padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 🔹 Buttons
        val patientsBtn = findViewById<Button>(R.id.button5)
        val appointmentsBtn = findViewById<Button>(R.id.button7)
        val fabAddPatient = findViewById<Button>(R.id.fabAdd)
        val fabAddAppointment = findViewById<ExtendedFloatingActionButton>(R.id.fabAddAppointment)
        val fabAdd = findViewById<ExtendedFloatingActionButton>(R.id.fabAdd)
        val clinicTestBtn = findViewById<Button>(R.id.button6)
        val criticalBtn = findViewById<Button>(R.id.button4)






        // 🔹 Patient Activity launcher
        val addPatientLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val lastPatientName = data?.getStringExtra("patientName")
                val lastPatientAge = data?.getStringExtra("patientAge")
                val lastPatientPhone = data?.getStringExtra("patientPhone")
                val lastPatientCondition = data?.getStringExtra("patientCondition")
                val lastPatientGender = data?.getStringExtra("patientGender")


                if (lastPatientName != null && lastPatientAge != null && lastPatientPhone != null && lastPatientCondition != null && lastPatientGender != null) {
                    PatientService.uploadPatient(
                        context = this,
                        name = lastPatientName,
                        age = lastPatientAge,
                        phone = lastPatientPhone,
                        condition = lastPatientCondition,
                        gender = lastPatientGender,
                    )
                }
            }
        }

        // 🔹 FAB — Add Patient
        fabAddPatient.setOnClickListener {
            val intent = Intent(this, AddPatientActivity::class.java)
            addPatientLauncher.launch(intent)
        }

        // 🔹 NEW — Add Appointment Button
        fabAddAppointment.setOnClickListener {
            Log.d("HomeActivity", "Add Appointment FAB clicked")
            val intent = Intent(this, AppointmentActivity::class.java)
            startActivity(intent)
        }

        // 🔹 View Patients Button
        patientsBtn.setOnClickListener {
            if (currentFragment != "patients") {

                GlobalScope.launch {
                    withContext(Dispatchers.Main) {
                        val viewPatientsFragment = ViewPatients()

                        Log.d("HomeActivity", "Button clicked, showing ViewPatients fragment.")
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, viewPatientsFragment)
                            .addToBackStack(null)
                            .commit()

                        currentFragment = "patients"
                    }
                }
            }
        }

        // 🔹 View Appointments Button
        appointmentsBtn.setOnClickListener {
            if (currentFragment != "appointments") {
                GlobalScope.launch {
                    withContext(Dispatchers.Main) {
                        val viewAppointmentsFragment = ViewAppointments()

                        Log.d("HomeActivity", "Showing ViewAppointments fragment.")

                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, viewAppointmentsFragment)
                            .addToBackStack(null)
                            .commit()

                        currentFragment = "appointments"
                    }
                }
            }
        }

        // 🔹 View Clinical Tests Button
        clinicTestBtn.setOnClickListener {
            Log.d("HomeActivity", "Showing PatientSelectFragment.")
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PatientSelectFragment())
                .addToBackStack(null)
                .commit()

//            fabAdd.hide()
//            fabAddAppointment.hide()
        }

    // 🔹 View Critical Patients Button
    criticalBtn.setOnClickListener {
        val intent = Intent(this, CriticalPatientsActivity::class.java)
        startActivity(intent)
    }

    }
}
