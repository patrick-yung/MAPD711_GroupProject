package com.mapd711_groupproject

import android.content.Context
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

object ClinicalService {

    private const val CLINICAL_URL = "https://mapd713-group-project-2.onrender.com/clinicaldata"

    // This function sends the data manually
    suspend fun uploadTest(context: Context, request: ClinicalTestRequest) {
        withContext(Dispatchers.IO) {
            try {
                Log.d("ClinicalService", "Uploading to: $CLINICAL_URL")

                // Setup the Connection to the Server
                val url = URL(CLINICAL_URL)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                connection.setRequestProperty("Accept", "application/json")
                connection.doOutput = true

                // Manually Build the JSON
                val jsonParam = JSONObject()
                jsonParam.put("patientId", request.patientId)
                jsonParam.put("type", request.type)
                jsonParam.put("value", request.value)

                Log.d("ClinicalService", "Sending Data: $jsonParam")

                //Send the Data
                val outputStream = OutputStreamWriter(connection.outputStream)
                outputStream.write(jsonParam.toString())
                outputStream.flush()
                outputStream.close()

                //Check the Result
                val responseCode = connection.responseCode
                if (responseCode == 201 || responseCode == 200) {
                    // Success!
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    Log.d("ClinicalService", "Response: $response")

                    // Check if critical (Backend returns "flagged": true/false)
                    val jsonResponse = JSONObject(response)
                    val isCritical = jsonResponse.optBoolean("flagged", false)

                    withContext(Dispatchers.Main) {
                        if (isCritical) {
                            Toast.makeText(context, "⚠️ CRITICAL ALERT SAVED!", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, "✅ Saved Successfully", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    // Failure
                    Log.e("ClinicalService", "Server Error: $responseCode")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Server Error: $responseCode", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (e: Exception) {
                Log.e("ClinicalService", "Exception: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Network Error. Check internet.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}