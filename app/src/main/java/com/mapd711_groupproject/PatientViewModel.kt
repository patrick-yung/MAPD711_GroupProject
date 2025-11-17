package com.mapd711_groupproject

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

//provides data to the UI and acts as a communication center
// between the Repository and the UI.
class PatientViewModel : ViewModel() {

    // calling repository tasks and
    // sending the results to the Activity
    var liveDataPatient: LiveData<PatientModel>? = null

    //
    fun insertPatient(context: Context, patientname: String, patientage: String, patientgender: String, patientphone: String, patientaddress: String, patientcondition: String) {
        PatientRepository.insertPatient(context, patientname, patientage, patientgender, patientphone, patientaddress, patientcondition)
    }

    fun getPatients(context: Context, patientname: String) : LiveData<PatientModel>? {
        liveDataPatient = PatientRepository.getPatients(context, patientname)
        return liveDataPatient
    }
}

