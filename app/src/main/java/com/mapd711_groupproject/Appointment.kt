package com.mapd711_groupproject

// Model used for REST + UI
data class Appointment(
    val id: String,
    val patientName: String,
    val doctorName: String,
    val appointmentDate: String,
    val reason: String,
    val status: String = "Scheduled",
    val isEmergency: Boolean = false
)
