package com.mapd711_groupproject

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class PatientDetailActivity : AppCompatActivity() {


    fun updatePatient(
        context: Context,
        patientId: String,
        name: String,
        age: Int,
        gender: String,
        contact: String,
        history: String
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val url = URL("https://mapd713-group-project.onrender.com/patients/$patientId")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "PUT"
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                connection.doOutput = true

                // Create a JSON object with all patient data
                val patientJson = JSONObject().apply {
                    put("name", name)
                    put("age", age)
                    put("gender", gender)
                    put("contact", contact)
                    put("history", history)
                }

                val patientLoad = patientJson.toString()

                OutputStreamWriter(connection.outputStream).use { writer ->
                    writer.write(patientLoad)
                    writer.flush()
                }

                val responseCode = connection.responseCode
                Log.d("Upload", "Response Code: $responseCode")

                // Switch to Main thread to show Toast
                withContext(Dispatchers.Main) {
                    if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                        Toast.makeText(context, "Patient updated successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Update failed with code: $responseCode", Toast.LENGTH_LONG).show()
                    }
                }

            } catch (e: Exception) {
                Log.e("UploadError", "Error updating patient", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_detail)
        val buttonCancel = findViewById<TextView>(R.id.buttonCancel)
        buttonCancel.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }


        // Get the patient data from intent
        val patient = intent.getSerializableExtra("PATIENT_DATA") as? PatientService.Patient
        val nameTextView: TextView = findViewById(R.id.textViewPatientName)
        val ageTextView: TextView = findViewById(R.id.editTextAge)
        val genderTextView: TextView = findViewById(R.id.editTextGender)
        val contactTextView: TextView = findViewById(R.id.editTextContact)
        val historyTextView: TextView = findViewById(R.id.editTextHistory)
        var patientID = ""
        patient?.let {
            // Set patient data
            nameTextView.text = it.name
            ageTextView.text = "${it.age}"
            genderTextView.text = "${it.gender}"
            contactTextView.text = "${it.contact}"
            historyTextView.text = "${it.history}"
            patientID= it._id.toString()

        }


        val saveButton: TextView = findViewById(R.id.buttonSave)
        saveButton.setOnClickListener {
            val name = nameTextView.text.toString()
            val age = ageTextView.text.toString().toIntOrNull() ?: 0
            val gender = genderTextView.text.toString()
            val contact = contactTextView.text.toString()
            val history = historyTextView.text.toString()

            updatePatient(this, patientID, name, age, gender, contact, history)        }
        }

}