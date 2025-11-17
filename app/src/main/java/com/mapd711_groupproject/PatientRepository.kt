package com.mapd711_groupproject

import android.content.Context
import androidx.lifecycle.LiveData
import kotlin.coroutines.CoroutineContext
import com.example.roommvvmapp.StudentModel
import com.example.roommvvmapp.StudentDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//a class for managing multiple data sources
class PatientRepository {

    //defining database and live data object as companion objects
    companion object {
        var patientDatabase: PatientDatabase? = null
        var patientModel: LiveData<PatientModel>? = null

        //initialize database
        fun initializeDB(context: Context) : PatientDatabase {
            return PatientDatabase.getDataseClient(context)
        }

        //Initialize insertPatient()
        fun insertPatient(context: Context, patientname: String, patientage: String, patientgender: String, patientphone: String, patientaddress: String, patientcondition: String) {
            patientDatabase = initializeDB(context)

            CoroutineScope(IO).launch {
                val patientDetails = PatientModel(patientname, patientage, patientgender, patientphone, patientaddress, patientcondition)
                patientDatabase!!.patientDao().insertPatient(patientDetails)
            }

        }

        //Initialize getPatients()
        fun getPatients(context: Context, patientname: String) : LiveData<PatientModel>? {

            patientDatabase = initializeDB(context)
            patientModel = patientDatabase!!.patientDao().getPatients(patientname)
            return patientModel
        }

    }
}