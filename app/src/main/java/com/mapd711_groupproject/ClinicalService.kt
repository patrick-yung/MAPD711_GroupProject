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

    private const val CLINICAL_URL = "https://mapd713-group-project.onrender.com/clinicalData"

    suspend fun uploadTest(context: Context, request: ClinicalTestRequest): ClinicalTestResponse? {
        return withContext(Dispatchers.IO) {
            try {
                // Setup
                val url = URL(CLINICAL_URL)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                connection.doOutput = true

                // JSON Body
                val jsonParam = JSONObject()
                jsonParam.put("patientId", request.patientId)
                jsonParam.put("type", request.type)
                jsonParam.put("value", request.value)

                // Send
                val outputStream = OutputStreamWriter(connection.outputStream)
                outputStream.write(jsonParam.toString())
                outputStream.flush()
                outputStream.close()

                // Response
                val responseCode = connection.responseCode
                if (responseCode == 201 || responseCode == 200) {
                    val responseText = connection.inputStream.bufferedReader().use { it.readText() }
                    val jsonResponse = JSONObject(responseText)
                    Log.d("ClinicalService", "Response: $jsonResponse")

                    val responseObj = ClinicalTestResponse(
                        _id = jsonResponse.optString("_id"),
                        patientId = jsonResponse.optString("patientId"),
                        type = jsonResponse.optString("type"),
                        value = jsonResponse.optString("value"),
                        flagged = jsonResponse.optBoolean("flagged"),
                        measuredDateTime = jsonResponse.optString("measuredDateTime")
                    )

                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "✅ Saved Successfully", Toast.LENGTH_SHORT).show()
                    }

                    return@withContext responseObj
                } else {
                    Log.e("ClinicalService", "Server Error: $responseCode")
                    return@withContext null
                }

            } catch (e: Exception) {
                Log.e("ClinicalService", "Exception: ${e.message}")
                return@withContext null
            }
        }
    }
}