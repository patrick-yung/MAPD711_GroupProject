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

    private const val CLINICAL_URL = "https://mapd713-group-project.onrender.com/clinicaldata"

    // Fetch tests ONLY for a specific patient ID
    suspend fun fetchTestsByPatientId(patientId: String): List<ClinicalTestResponse> {
        return withContext(Dispatchers.IO) {
            val list = mutableListOf<ClinicalTestResponse>()
            try {
                // Your backend endpoint: /clinicaldata/patients/:patientId
                val urlString =
                    "https://mapd713-group-project.onrender.com/clinicaldata/patients/$patientId"
                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                if (connection.responseCode == 200) {
                    val responseText = connection.inputStream.bufferedReader().use { it.readText() }
                    val jsonArray = org.json.JSONArray(responseText)

                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        list.add(
                            ClinicalTestResponse(
                                _id = obj.optString("_id"),
                                patientId = obj.optString("patientId"),
                                type = obj.optString("type"),
                                value = obj.optString("value"),
                                flagged = obj.optBoolean("flagged"),
                                measuredDateTime = obj.optString("measuredDateTime")
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("ClinicalService", "Error: ${e.message}")
            }
            return@withContext list
        }
    }
}