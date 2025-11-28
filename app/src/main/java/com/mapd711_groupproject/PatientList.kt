package com.mapd711_groupproject

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class PatientAdapter(
    private val patientList: List<PatientService.Patient>,
    private val context: Context
) : RecyclerView.Adapter<PatientAdapter.PatientViewHolder>() {

    // This class holds the views for a single list item (one row).
    class PatientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.textViewPatientName)
        val detailsTextView: TextView = itemView.findViewById(R.id.textViewPatientDetails)
    }

    // Creates a new row's layout.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.patients_list_display, parent, false)
        return PatientViewHolder(view)
    }

    // Populates a row with data from a specific patient.
    override fun onBindViewHolder(holder: PatientViewHolder, position: Int) {
        val patient = patientList[position]
        holder.nameTextView.text = patient.name
        holder.detailsTextView.text = "Age: ${patient.age}, Gender: ${patient.gender}"

        // Add click listener to make items clickable
        holder.itemView.setOnClickListener {
            // Navigate to patient detail screen
            val intent = Intent(context, PatientDetailActivity::class.java).apply {
                putExtra("PATIENT_DATA", patient)
            }
            context.startActivity(intent)
        }
    }

    // Returns the total number of items in the list.
    override fun getItemCount(): Int {
        return patientList.size
    }
}

object PatientService {
    data class Patient(
        val name: String,
        val age: Int,
        val gender: String,
        val history: String,
        val contact: String,
        val _id: String
    ) : java.io.Serializable

    fun uploadPatient(
        context: Context,
        name: String,
        age: String,
        phone: String,
        gender: String,
        condition: String
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val url = URL("https://mapd713-group-project.onrender.com/patients")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                connection.doOutput = true

                // Create a valid JSON object
                val patientJson = JSONObject().apply {
                    put("name", name)
                    put("age", age.toIntOrNull() ?: 0)
                    put("gender", gender)
                    put("contact", phone)
                    put("history", condition)
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
                    if (responseCode == HttpURLConnection.HTTP_CREATED || responseCode == HttpURLConnection.HTTP_OK) {
                        Toast.makeText(context, "Patient added successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Upload failed with code: $responseCode", Toast.LENGTH_LONG).show()
                    }
                }

            } catch (e: Exception) {
                Log.e("UploadError", "Error uploading patient", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    suspend fun fetchPatients(): List<Patient>? {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("https://mapd713-group-project.onrender.com/patients")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")

                val responseCode = connection.responseCode
                Log.d("FetchPatients", "Response Code: $responseCode")

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Read the response
                    val inputStream = connection.inputStream
                    val responseText = inputStream.bufferedReader().use { it.readText() }

                    Log.d("FetchPatients", "Response: $responseText")

                    // Parse JSON response to List<Patient>
                    val jsonArray = org.json.JSONArray(responseText)
                    val patientsList = mutableListOf<Patient>()

                    for (i in 0 until jsonArray.length()) {
                        val patientObj = jsonArray.getJSONObject(i)
                        val patient = Patient(
                            name = patientObj.getString("name"),
                            age = patientObj.getInt("age"),
                            gender = patientObj.getString("gender"),
                            history = patientObj.getString("history"),
                            contact = patientObj.getString("contact"),
                            _id = patientObj.getString("_id")
                        )
                        patientsList.add(patient)
                    }

                    connection.disconnect()
                    patientsList
                } else {
                    Log.e("FetchPatients", "Failed to fetch patients. Code: $responseCode")
                    connection.disconnect()
                    null
                }

            } catch (e: Exception) {
                Log.e("FetchPatients", "Error fetching patients", e)
                null
            }
        }
    }



}