package com.mapd711_groupproject

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
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

        setupDrawer(R.id.nav_home)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val name = findViewById<EditText>(R.id.editTextName)
        val prefillName = intent.getStringExtra("fromAppointmentName")
        if (!prefillName.isNullOrEmpty()) {
            name.setText(prefillName)
        }

        val age = findViewById<EditText>(R.id.editTextAge)
        val phone = findViewById<EditText>(R.id.editTextPhone)
        val condition = findViewById<EditText>(R.id.editTextCondition)
        val selectGender = findViewById<RadioGroup>(R.id.radioGroupGender)
        val saveButton = findViewById<Button>(R.id.button2)
        var gender = ""

        val isEdit = intent.getBooleanExtra("isEdit", false)
        if (isEdit) {
            name.setText(intent.getStringExtra("patientName"))
            age.setText(intent.getStringExtra("patientAge"))
            phone.setText(intent.getStringExtra("patientPhone"))
            condition.setText(intent.getStringExtra("patientCondition"))
            // We’re not pre-selecting gender here because the old version also didn’t
        }

        saveButton.setOnClickListener {
            val nameText = name.text.toString().trim()
            val ageText = age.text.toString().trim()
            val phoneText = phone.text.toString().trim()
            val conditionText = condition.text.toString().trim()

            gender = when (selectGender.checkedRadioButtonId) {
                R.id.radioMale -> "Male"
                R.id.radioFemale -> "Female"
                else -> ""
            }

            if (gender.isEmpty()) {
                Toast.makeText(this, "Please fill in all Gender fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (nameText.isEmpty() ||
                ageText.isEmpty() ||
                phoneText.isEmpty() ||
                conditionText.isEmpty() ||
                gender.isEmpty()
            ) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (ageText.toIntOrNull() == null) {
                Toast.makeText(this, "Please enter a valid age", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 🔥 Mark matching appointments as Attended (Android-only logic)
            markAppointmentsAsAttended(nameText)

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
    // 🔥 NEW FUNCTION — Attended matching WITHOUT backend logic
    // --------------------------------------------------------------------
    private fun markAppointmentsAsAttended(patientName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL("https://mapd713-group-project-2.onrender.com/appointments")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"

                if (conn.responseCode !in 200..299) return@launch

                val text = conn.inputStream.bufferedReader().readText()
                val arr = JSONArray(text)

                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)

                    val nameA = obj.getString("patientName")
                    if (nameA.trim().lowercase() != patientName.trim().lowercase()) continue

                    val id = obj.getString("_id")

                    val updateUrl =
                        URL("https://mapd713-group-project-2.onrender.com/appointments/$id")
                    val updateConn = updateUrl.openConnection() as HttpURLConnection
                    updateConn.requestMethod = "PUT"
                    updateConn.setRequestProperty("Content-Type", "application/json")
                    updateConn.doOutput = true

                    val updatedObj = JSONObject().apply {
                        put("_id", id)
                        put("patientName", obj.getString("patientName"))
                        put("doctorName", obj.getString("doctorName"))
                        put("appointmentDate", obj.getString("appointmentDate"))
                        put("reason", obj.getString("reason"))
                        put("status", "Attended")
                        put("isEmergency", obj.optBoolean("isEmergency"))
                    }

                    updateConn.outputStream.write(updatedObj.toString().toByteArray())
                    updateConn.outputStream.close()

                    updateConn.responseCode
                }

            } catch (_: Exception) { }
        }
    }
}
