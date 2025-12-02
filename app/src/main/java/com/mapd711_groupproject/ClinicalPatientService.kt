package com.mapd711_groupproject

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

data class ClinicalPatient(
    val _id: String,
    val name: String
)

object ClinicalPatientService {
    private const val PATIENTS_URL = Globals.BASE_URL+"/patients"

    suspend fun fetchPatientNamesAndIds(): List<ClinicalPatient> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("ClinicalPatientService", "Fetching from: $PATIENTS_URL")

                val url = URL(PATIENTS_URL)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val responseText = connection.inputStream.bufferedReader().use { it.readText() }
                    Log.d("ClinicalPatientService", "Response: $responseText")

                    val jsonArray = JSONArray(responseText)
                    val list = mutableListOf<ClinicalPatient>()

                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        Log.d("ClinicalPatientService", "Object: $obj")

                        val id = obj.optString("_id", "")
                        val name = obj.optString("name", "Unknown")

                        if (id.isNotEmpty()) {
                            list.add(ClinicalPatient(id, name))
                        }
                    }
                    return@withContext list
                } else {
                    Log.e("ClinicalPatientService", "Error Code: ${connection.responseCode}")
                    return@withContext emptyList()
                }

            } catch (e: Exception) {
                Log.e("ClinicalPatientService", "Exception: ${e.message}")
                return@withContext emptyList()
            }
        }
    }
}