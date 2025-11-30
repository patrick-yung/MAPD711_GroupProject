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

    // uploadtest function
    suspend fun uploadTest(context: Context, request: ClinicalTestRequest): ClinicalTestResponse? {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(CLINICAL_URL)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                connection.setRequestProperty("Accept", "application/json")
                connection.doOutput = true
                val jsonParam = JSONObject()
                jsonParam.put("patientId", request.patientId)
                jsonParam.put("type", request.type)
                jsonParam.put("value", request.value)

                val outputStream = OutputStreamWriter(connection.outputStream)
                outputStream.write(jsonParam.toString())
                outputStream.flush()
                outputStream.close()

                val responseCode = connection.responseCode
                if (responseCode == 201 || responseCode == 200) {
                    val responseText = connection.inputStream.bufferedReader().use { it.readText() }
                    val jsonResponse = JSONObject(responseText)

                    val responseObj = ClinicalTestResponse(
                        _id = jsonResponse.optString("_id"),
                        patientId = jsonResponse.optString("patientId"),
                        type = jsonResponse.optString("type"),
                        value = jsonResponse.optString("value"),
                        flagged = jsonResponse.optBoolean("flagged"),
                        measuredDateTime = jsonResponse.optString("measuredDateTime")
                    )
                    return@withContext responseObj
                } else {
                    Log.e("Upload", "Error: $responseCode")
                    return@withContext null
                }
            } catch (e: Exception) {
                Log.e("Upload", "Exception: ${e.message}")
                return@withContext null
            }
        }
    }


    // fetch test function
    suspend fun fetchTestsByPatientId(patientId: String): List<ClinicalTestResponse> {
        return withContext(Dispatchers.IO) {
            val list = mutableListOf<ClinicalTestResponse>()
            try {
                // 1. Build the URL for the specific patient
                val urlString = "https://mapd713-group-project.onrender.com/clinicaldata/patients/$patientId"
                Log.d("ClinicalService", "Fetching history: $urlString")

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
                Log.e("ClinicalService", "Error fetching history: ${e.message}")
            }
            return@withContext list
        }
    }

}