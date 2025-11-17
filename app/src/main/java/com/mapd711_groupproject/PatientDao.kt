package com.mapd711_groupproject

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

// Room DAO - Data Access Object Interface
// this interface declares database functions
// and does the mapping of SQL queries to functions
@Dao
interface PatientDao {

    //defining an insert method using @Insert Annotation
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStudent(patientModel: PatientModel)

    //defining a query method using @Query Annotation
    @Query("SELECT * FROM patient WHERE PatientName =:patientname")
    fun getPatients(patientname: String?) : LiveData<PatientModel>
}