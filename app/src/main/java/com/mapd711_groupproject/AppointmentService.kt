package com.mapd711_groupproject

import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

object AppointmentService {

    // ✅ Use ONLY base API, not /appointments
    private const val BASE_URL = "https://mapd713-group-project-2.onrender.com"

    // ✅ Create the OkHttp client
    private val client = OkHttpClient()

    // ---------------------------------------------------------
    // FETCH APPOINTMENTS COUNT
    // ---------------------------------------------------------
    suspend fun fetchAppointmentsCount(): Int {
        return try {
            val url = "$BASE_URL/appointments/count"   // correct endpoint

            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            val response = client.newCall(request).execute()

            if (!response.isSuccessful) return 0

            val body = response.body?.string() ?: return 0

            val json = JSONObject(body)
            json.optInt("count", 0)

        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }
}
