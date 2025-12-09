package com.mapd711_groupproject

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class AddPatientActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_patient)

        // Drawer / toolbar
        setupDrawer(R.id.nav_home)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val name = findViewById<EditText>(R.id.editTextName)
        val age = findViewById<EditText>(R.id.editTextAge)
        val phone = findViewById<EditText>(R.id.editTextPhone)
        val condition = findViewById<EditText>(R.id.editTextCondition)
        val selectGender = findViewById<RadioGroup>(R.id.radioGroupGender)
        val saveButton = findViewById<Button>(R.id.button2)

        // If we came from an Appointment → prefill the patient name
        val prefillName = intent.getStringExtra("fromAppointmentName")
        if (!prefillName.isNullOrEmpty()) {
            name.setText(prefillName)
        }

        // Edit mode support (keep your existing behavior)
        val isEdit = intent.getBooleanExtra("isEdit", false)
        if (isEdit) {
            name.setText(intent.getStringExtra("patientName"))
            age.setText(intent.getStringExtra("patientAge"))
            phone.setText(intent.getStringExtra("patientPhone"))
            condition.setText(intent.getStringExtra("patientCondition"))

            // If you later want to pre-select gender in edit mode, you can add it here
        }

        saveButton.setOnClickListener {
            val nameText = name.text.toString().trim()
            val ageText = age.text.toString().trim()
            val phoneText = phone.text.toString().trim()
            val conditionText = condition.text.toString().trim()

            val gender = when (selectGender.checkedRadioButtonId) {
                R.id.radioMale -> "Male"
                R.id.radioFemale -> "Female"
                else -> ""
            }

            if (gender.isEmpty()) {
                Toast.makeText(this, "Please select a gender", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (nameText.isEmpty() ||
                ageText.isEmpty() ||
                phoneText.isEmpty() ||
                conditionText.isEmpty()
            ) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (ageText.toIntOrNull() == null) {
                Toast.makeText(this, "Please enter a valid age", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 🔥 ANDROID-ONLY LINK: mark matching appointments as Attended
            markAppointmentsAsAttended(nameText)

            // Send data back to HomeActivity
            val resultIntent = Intent().apply {
                putExtra("patientName", nameText)
                putExtra("patientAge", ageText)
                putExtra("patientPhone", phoneText)
                putExtra("patientGender", gender)
                putExtra("patientCondition", conditionText)
            }

            setResult(Activity.RESULT_OK, resultIntent)

            Toast.makeText(this, "Patient Data Saved", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    // --------------------------------------------------------------------
    // 🔥 NEW FUNCTION — Mark any appointments with this name as "Attended"
    //    No backend changes needed; all done from Android.
    // --------------------------------------------------------------------
    private fun markAppointmentsAsAttended(patientName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val baseUrl = "https://mapd713-group-project-2.onrender.com/appointments"

                // 1) GET all appointments
                val url = URL(baseUrl)
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"

                if (conn.responseCode !in 200..299) {
                    conn.disconnect()
                    return@launch
                }

                val text = conn.inputStream.bufferedReader().use { it.readText() }
                conn.disconnect()

                val arr = JSONArray(text)

                fun normalizeName(str: String?): String =
                    (str ?: "")
                        .trim()
                        .lowercase()

                val target = normalizeName(patientName)

                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    val apptName = normalizeName(obj.optString("patientName", ""))

                    if (apptName != target) continue

                    val id = obj.optString("_id", "")
                    if (id.isEmpty()) continue

                    // 2) Build updated appointment JSON (keep all fields, just force status)
                    val updatedObj = JSONObject().apply {
                        put("_id", id)
                        put("patientName", obj.optString("patientName", ""))
                        put("doctorName", obj.optString("doctorName", ""))
                        put("appointmentDate", obj.optString("appointmentDate", ""))
                        put("reason", obj.optString("reason", ""))
                        put("status", "Attended")     // 👈 FORCE ATTENDED
                        put("isEmergency", obj.optBoolean("isEmergency", false))
                    }

                    // 3) PUT the updated appointment back
                    val putUrl = URL("$baseUrl/$id")
                    val putConn = putUrl.openConnection() as HttpURLConnection
                    putConn.requestMethod = "PUT"
                    putConn.setRequestProperty("Content-Type", "application/json")
                    putConn.doOutput = true

                    putConn.outputStream.bufferedWriter().use { writer ->
                        writer.write(updatedObj.toString())
                    }

                    // fire the request (we don't care much about the response body here)
                    putConn.responseCode
                    putConn.disconnect()
                }

            } catch (_: Exception) {
                // Silently ignore – we don't want to break patient creation if this fails
            }
        }
    }
}
