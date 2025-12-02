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

    fun deletePatient(
        context: Context,
        patientId: String
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val urlBase = Globals.BASE_URL+"/patients/$patientId"

                val url = URL(urlBase)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "DELETE" // Fixed: Changed from "DEL" to "DELETE"
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                connection.doOutput = false // DELETE requests typically don't have a body

                val responseCode = connection.responseCode
                Log.d("Delete", "Response Code: $responseCode")

                // Switch to Main thread to show Toast
                withContext(Dispatchers.Main) {
                    if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                        Toast.makeText(context, "Patient deleted successfully!", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(
                            context,
                            "Delete failed with code: $responseCode",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

            } catch (e: Exception) {
                Log.e("DeleteError", "Error deleting patient", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
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
                val urlBase = Globals.BASE_URL+"/patients/$patientId"

                val url = URL(urlBase)
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
        var patientID = ""

        val buttonCancel = findViewById<TextView>(R.id.buttonCancel)
        buttonCancel.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        val delateButton: TextView = findViewById(R.id.buttonDelete)
        delateButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            deletePatient(this, patientID)
            startActivity(intent)
        }


        // Get the patient data from intent
        val patient = intent.getSerializableExtra("PATIENT_DATA") as? PatientService.Patient
        val nameTextView: TextView = findViewById(R.id.textViewPatientName)
        val ageTextView: TextView = findViewById(R.id.editTextAge)
        val genderTextView: TextView = findViewById(R.id.editTextGender)
        val contactTextView: TextView = findViewById(R.id.editTextContact)
        val historyTextView: TextView = findViewById(R.id.editTextHistory)
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
            if (name.isEmpty()) {
                Toast.makeText(this,("Please enter patient name"),Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (ageTextView.text.toString().isEmpty()) {
                Toast.makeText(this,("Please enter patient age"),Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (age == null) {
                Toast.makeText(this,("Age must be a valid number"),Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (gender.isEmpty()) {
                Toast.makeText(this,("Please select patient gender"),Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (contact.isEmpty()) {
                Toast.makeText(this,("Please enter contact information"),Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            updatePatient(this, patientID, name, age, gender, contact, history)        }
        }



}