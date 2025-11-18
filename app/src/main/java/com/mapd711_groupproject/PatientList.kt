package com.mapd711_groupproject

import android.content.Context
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

object PatientService {

    fun uploadPatient(
        context: Context,
        name: String,
        age: String,
        phone: String,
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
                    put("gender", "Male") // Hardcoded for now
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
}