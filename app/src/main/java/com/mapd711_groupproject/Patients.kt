package com.mapd711_groupproject

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class Patient(
    val name: String,
    val age: Int,
    val department: String
)

class Patients : ViewModel() {

    private val _patientList = MutableLiveData<List<Patient>>()
    val patientList: LiveData<List<Patient>> get() = _patientList
    init {
        loadDefaultPatients()
    }

    private fun loadDefaultPatients() {
        val defaultData = listOf(
            Patient("John Smith", 45, "Cardiology"),
            Patient("Emily Jones", 28, "Neurology"),
            Patient("Michael Johnson", 62, "Oncology"),
            Patient("Sarah Williams", 33, "Pediatrics"),
            Patient("David Brown", 51, "Orthopedics"),
            Patient("Jessica Davis", 38, "Cardiology"),
            Patient("Chris Miller", 40, "General"),
            Patient("Amanda Wilson", 29, "Radiology")
        )

        _patientList.value = defaultData
    }
}
