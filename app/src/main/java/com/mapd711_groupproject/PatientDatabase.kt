package com.mapd711_groupproject

import android.content.Context
import androidx.room.*
import androidx.room.RoomDatabase

//Room database
@Database(entities = arrayOf(PatientModel::class), version = 1, exportSchema = false)
abstract class PatientDatabase : RoomDatabase() {
    //instantiating Student DAO object
    abstract fun patientDao() : PatientDao

    //companion object means an object declaration inside a class
    companion object {

        //Volatile object or property is immediately made visible to other threads.
        @Volatile
        private var INSTANCE: PatientDatabase? = null

        //create a database name "PATIENTTDB"
        fun getDataseClient(context: Context) : PatientDatabase {

            if (INSTANCE != null) return INSTANCE!!

            synchronized(this) {

                INSTANCE = Room
                    .databaseBuilder(context, PatientDatabase::class.java, "PATIENTDB")
                    .fallbackToDestructiveMigration()
                    .build()

                return INSTANCE!!

            }
        }
    }
}