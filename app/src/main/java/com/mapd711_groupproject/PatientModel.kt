package com.mapd711_groupproject

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//Room entity data class - model
//This class describes a database table
@Entity(tableName = "patient")
data class PatientModel(
    //defining a column PatientName
    @ColumnInfo(name = "patientname")
    var PatientName: String,
    //defining a column PatientAge
    @ColumnInfo(name = "patientage")
    var PatientAge: String,
    //defining a column PatientGender
    @ColumnInfo(name = "patientgender")
    var PatientGender: String,
    //defining a column PatientPhone
    @ColumnInfo(name = "patientphone")
    var PatientPhone: String,
    //defining a column PatientAddress
    @ColumnInfo(name = "patientaddress")
    var PatientAddress: String,
    //defining a column PatientCondition
    @ColumnInfo(name = "patientcondition")
    var PatientCondition: String,


    )
{
    //defining a primary key field Id
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var Id: Int? = null
}

