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
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONArray
import org.json.JSONObject

class HomeActivity : BaseActivity() {

    private var currentFragment: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        // Drawer / toolbar
        setupDrawer(R.id.nav_home)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val patientsBtn = findViewById<Button>(R.id.button5)
        val appointmentsBtn = findViewById<Button>(R.id.button7)
        val clinicTestBtn = findViewById<Button>(R.id.button6)
        val criticalBtn = findViewById<Button>(R.id.button4)

        val fabAddPatient = findViewById<Button>(R.id.fabAdd)
        val fabAddAppointment = findViewById<ExtendedFloatingActionButton>(R.id.fabAddAppointment)
        val fabAdd = findViewById<ExtendedFloatingActionButton>(R.id.fabAdd)

        // Dashboard text views
        val totalPatientsText = findViewById<TextView>(R.id.textView3)
        val totalAppointmentsText = findViewById<TextView>(R.id.textView5)

        // Listen for "refresh_home" from ViewAppointments fragment
        supportFragmentManager.setFragmentResultListener("refresh_home", this) { _, _ ->
            refreshDashboardCounts(totalPatientsText, totalAppointmentsText)
        }

        // ActivityResult launcher for AddPatientActivity
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

                if (lastPatientName != null &&
                    lastPatientAge != null &&
                    lastPatientPhone != null &&
                    lastPatientCondition != null &&
                    lastPatientGender != null
                ) {
                    PatientService.uploadPatient(
                        context = this,
                        name = lastPatientName,
                        age = lastPatientAge,
                        phone = lastPatientPhone,
                        condition = lastPatientCondition,
                        gender = lastPatientGender
                    )
                }

                // After adding a patient, refresh dashboard
                refreshDashboardCounts(totalPatientsText, totalAppointmentsText)
            }
        }

        // FAB — Add Patient
        fabAddPatient.setOnClickListener {
            val intent = Intent(this, AddPatientActivity::class.java)
            addPatientLauncher.launch(intent)
        }

        // FAB — Add Appointment
        fabAddAppointment.setOnClickListener {
            Log.d("HomeActivity", "Add Appointment FAB clicked")
            val intent = Intent(this, AppointmentActivity::class.java)
            startActivity(intent)
        }

        // View Patients
        patientsBtn.setOnClickListener {
            if (currentFragment != "patients") {
                GlobalScope.launch {
                    withContext(Dispatchers.Main) {
                        val viewPatientsFragment = ViewPatients()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, viewPatientsFragment)
                            .addToBackStack(null)
                            .commit()
                        currentFragment = "patients"
                    }
                }
            }
        }

        // View Appointments
        appointmentsBtn.setOnClickListener {
            if (currentFragment != "appointments") {
                GlobalScope.launch {
                    withContext(Dispatchers.Main) {
                        val viewAppointmentsFragment = ViewAppointments()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, viewAppointmentsFragment)
                            .addToBackStack(null)
                            .commit()
                        currentFragment = "appointments"
                    }
                }
            }
        }

        // View Clinical Tests
        clinicTestBtn.setOnClickListener {
            Log.d("HomeActivity", "Showing PatientSelectFragment.")
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PatientSelectFragment())
                .addToBackStack(null)
                .commit()

            // Optional: hide FABs while viewing tests
            fabAdd.hide()
            fabAddAppointment.hide()
        }

        // View Critical Patients
        criticalBtn.setOnClickListener {
            val intent = Intent(this, CriticalPatientsActivity::class.java)
            startActivity(intent)
        }

        // Initial dashboard load
        refreshDashboardCounts(totalPatientsText, totalAppointmentsText)
    }

    // Refresh every time HomeActivity comes back to foreground
    override fun onResume() {
        super.onResume()

        val totalPatientsText = findViewById<TextView>(R.id.textView3)
        val totalAppointmentsText = findViewById<TextView>(R.id.textView5)

        refreshDashboardCounts(totalPatientsText, totalAppointmentsText)
    }


    // -----------------------------------------------------------
    // Helper: Load dynamic Patients + Appointments counts
    // -----------------------------------------------------------
    private fun refreshDashboardCounts(
        totalPatientsText: TextView,
        totalAppointmentsText: TextView
    ) {
        GlobalScope.launch(Dispatchers.IO) {

            // Fetch Patients Count
            val patientsCount = try {
                ClinicalPatientService.fetchPatientsCount()
            } catch (e: Exception) {
                e.printStackTrace()
                0
            }

            // Fetch Appointments Count
            val appointmentsCount = try {
                AppointmentService.fetchAppointmentsCount()
            } catch (e: Exception) {
                e.printStackTrace()
                0
            }

            // Update UI on main thread
            withContext(Dispatchers.Main) {
                totalPatientsText.text = "Total Patients: $patientsCount"
                totalAppointmentsText.text = "Appointments Made: $appointmentsCount"
            }
        }
    }


    // -----------------------------------------------------------
    // Direct call to appointments backend to get count
    // Works even if AppointmentService was changed.
    // -----------------------------------------------------------
    private fun fetchAppointmentsCountDirect(): Int {
        return try {
            val baseUrl = "https://mapd713-group-project-2.onrender.com/appointments/count"

            // Prefer /appointments/count if available
            var url = URL("$baseUrl/count")
            var conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"

            if (conn.responseCode in 200..299) {
                val text = conn.inputStream.bufferedReader().use { it.readText() }
                conn.disconnect()

                // Handle either { "count": X } or a raw array
                return try {
                    val obj = JSONObject(text)
                    obj.optInt("count", 0)
                } catch (_: Exception) {
                    val arr = JSONArray(text)
                    arr.length()
                }
            } else {
                conn.disconnect()

                // Fallback: GET /appointments and count items
                url = URL(baseUrl)
                conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"

                if (conn.responseCode in 200..299) {
                    val text = conn.inputStream.bufferedReader().use { it.readText() }
                    conn.disconnect()
                    val arr = JSONArray(text)
                    arr.length()
                } else {
                    conn.disconnect()
                    0
                }
            }
        } catch (e: Exception) {
            Log.e("HomeActivity", "fetchAppointmentsCountDirect error: ${e.message}", e)
            0
        }
    }
}
