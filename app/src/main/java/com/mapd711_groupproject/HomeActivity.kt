package com.mapd711_groupproject

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : BaseActivity() {

    private var currentFragment = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        // 🔹 Setup drawer
        setupDrawer(R.id.nav_home)

        // 🔹 System bar padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val sys = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(sys.left, sys.top, sys.right, sys.bottom)
            insets
        }

        // 🔹 Dashboard Text Views
        val totalPatientsText = findViewById<TextView>(R.id.textView3)
        val totalAppointmentsText = findViewById<TextView>(R.id.textView5)

        refreshDashboardCounts(totalPatientsText, totalAppointmentsText)

        // 🔹 Listen for refresh signal from ViewAppointments
        supportFragmentManager.setFragmentResultListener("refresh_home", this) { _, _ ->
            refreshDashboardCounts(totalPatientsText, totalAppointmentsText)
        }

        // ============================
        // BUTTONS + FABs
        // ============================
        val patientsBtn = findViewById<Button>(R.id.button5)
        val appointmentsBtn = findViewById<Button>(R.id.button7)
        val clinicTestBtn = findViewById<Button>(R.id.button6)
        val criticalBtn = findViewById<Button>(R.id.button4)

        val fabAddPatient = findViewById<ExtendedFloatingActionButton>(R.id.fabAdd)
        val fabAddAppointment = findViewById<ExtendedFloatingActionButton>(R.id.fabAddAppointment)

        // ============================
        // ADD PATIENT LAUNCHER
        // ============================
        val addPatientLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val name = data?.getStringExtra("patientName")
                val age = data?.getStringExtra("patientAge")
                val phone = data?.getStringExtra("patientPhone")
                val condition = data?.getStringExtra("patientCondition")
                val gender = data?.getStringExtra("patientGender")

                refreshDashboardCounts(totalPatientsText, totalAppointmentsText)

                if (name != null && age != null && phone != null && condition != null && gender != null) {
                    PatientService.uploadPatient(
                        context = this,
                        name = name,
                        age = age,
                        phone = phone,
                        condition = condition,
                        gender = gender
                    )
                }
            }
        }

        // ============================
        // FAB — ADD PATIENT
        // ============================
        fabAddPatient.setOnClickListener {
            addPatientLauncher.launch(Intent(this, AddPatientActivity::class.java))
        }

        // ============================
        // FAB — ADD APPOINTMENT
        // ============================
        fabAddAppointment.setOnClickListener {
            startActivity(Intent(this, AppointmentActivity::class.java))
        }

        // ============================
        // VIEW PATIENTS
        // ============================
        patientsBtn.setOnClickListener {
            if (currentFragment != "patients") {
                GlobalScope.launch(Dispatchers.Main) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, ViewPatients())
                        .addToBackStack(null)
                        .commit()

                    currentFragment = "patients"
                }
            }
        }

        // ============================
        // VIEW APPOINTMENTS
        // ============================
        appointmentsBtn.setOnClickListener {
            if (currentFragment != "appointments") {
                GlobalScope.launch(Dispatchers.Main) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, ViewAppointments())
                        .addToBackStack(null)
                        .commit()

                    currentFragment = "appointments"
                }
            }
        }

        // ============================
        // VIEW CLINICAL TESTS
        // ============================
        clinicTestBtn.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PatientSelectFragment())
                .addToBackStack(null)
                .commit()
        }

        // ============================
        // VIEW CRITICAL PATIENTS
        // ============================
        criticalBtn.setOnClickListener {
            startActivity(Intent(this, CriticalPatientsActivity::class.java))
        }
    }

    // -----------------------------------------------------------
    // 🔥 When user returns to Home screen, refresh the dashboard
    // -----------------------------------------------------------
    override fun onResume() {
        super.onResume()

        val totalPatientsText = findViewById<TextView>(R.id.textView3)
        val totalAppointmentsText = findViewById<TextView>(R.id.textView5)

        refreshDashboardCounts(totalPatientsText, totalAppointmentsText)
    }

    // -----------------------------------------------------------
    // 🔥 Live dynamic dashboard numbers
    // -----------------------------------------------------------
    private fun refreshDashboardCounts(
        totalPatientsText: TextView,
        totalAppointmentsText: TextView
    ) {
        GlobalScope.launch(Dispatchers.IO) {

            val patientsCount = try {
                ClinicalPatientService.fetchPatientsCount()
            } catch (e: Exception) {
                0
            }

            val appointmentsCount = try {
                AppointmentService.fetchAppointmentsCount()
            } catch (e: Exception) {
                0
            }

            withContext(Dispatchers.Main) {
                totalPatientsText.text = "Total Patients: $patientsCount"
                totalAppointmentsText.text = "Appointments Made: $appointmentsCount"
            }
        }
    }
}
