package com.mapd711_groupproject

data class ClinicalTestResponse(
    val _id: String,
    val patientId: String,
    val type: String,
    val value: String,
    val flagged: Boolean,
    val measuredDateTime: String
)

data class ClinicalTestRequest(
    val patientId: String,
    val type: String,
    val value: String
)