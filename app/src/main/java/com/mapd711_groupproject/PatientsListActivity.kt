package com.mapd711_groupproject

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.concurrent.thread

class PatientsListActivity : AppCompatActivity() {

    //database variable
    private lateinit var db: PatientDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_patients_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //setup the recycler view
        val recyclerView = findViewById<RecyclerView>(R.id.patientRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Load patients from DB on background thread
        thread {
            val db = PatientDatabase.getDataseClient(this)
            val allPatients = db.patientDao().getAllPatients()

            runOnUiThread {
                val adapter = PatientAdapter(allPatients)
                recyclerView.adapter = adapter
                if (allPatients.isEmpty()) {
                    // Optional: Add a TextView in XML for "No patients" and show it here
                } else {
                    val adapter = PatientAdapter(allPatients)
                    recyclerView.adapter = adapter
                }
            }
            }




    }
}