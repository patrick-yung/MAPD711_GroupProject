package com.mapd711_groupproject

data class Appointment(
    val id: String,
    val patientName: String,
    val doctorName: String,
    val appointmentDate: String,
    val reason: String,
    val status: String
)
