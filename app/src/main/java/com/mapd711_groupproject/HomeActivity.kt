package com.mapd711_groupproject

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL




class HomeActivity : BaseActivity() {

    private var currentFragment = "" // Moved inside the class to be a property

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

        val patientsBtn = findViewById<Button>(R.id.button5)
        val fabAdd = findViewById<Button>(R.id.fabAdd)

        val addPatientLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result?.data
                val lastPatientName = data?.getStringExtra("patientName")
                val lastPatientAge = data?.getStringExtra("patientAge")
                val lastPatientPhone = data?.getStringExtra("patientPhone")
                val lastPatientCondition = data?.getStringExtra("patientCondition")

                if (lastPatientName != null && lastPatientAge != null && lastPatientPhone != null && lastPatientCondition != null) {
                    PatientService.uploadPatient(
                        context = this,
                        name = lastPatientName,
                        age = lastPatientAge,
                        phone = lastPatientPhone,
                        condition = lastPatientCondition
                    )
                }
            }
        }

        fabAdd.setOnClickListener {
            val intent = Intent(this, AddPatientActivity::class.java)
            addPatientLauncher.launch(intent)
        }

        patientsBtn.setOnClickListener {
            if (currentFragment != "patients") {

                // Keeping GlobalScope as you requested.
                // The work inside will be to switch to the Main thread to update the UI.
                GlobalScope.launch {
                    // FIX #2: Switch to the Main thread before performing any UI operations.
                    withContext(Dispatchers.Main) {
                        // FIX #1: Create an instance of the ViewPatients fragment.
                        val viewPatientsFragment = ViewPatients()

                        Log.d("HomeActivity", "Button clicked, showing ViewPatients fragment.")

                        // Now you can use the variable to show the fragment.
                        // The fragment's own ViewModel will handle fetching the data.
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, viewPatientsFragment)
                            .addToBackStack(null) // Allows the user to press 'back'
                            .commit()
                    }
                }
            }
        }

    }
}
