package com.mapd711_groupproject

data class ClinicalTestRequest(
    val patientId: String,
    val type: String,
    val value: String
)
