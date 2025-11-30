package com.mapd711_groupproject

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PatientHistoryActivity : AppCompatActivity() {

    private lateinit var patientId: String
    private lateinit var patientName: String
    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_history)

        // Get Data from Intent
        patientId = intent.getStringExtra("PATIENT_ID") ?: ""
        patientName = intent.getStringExtra("PATIENT_NAME") ?: ""

        findViewById<TextView>(R.id.tvHistoryTitle).text = "$patientName's History"
        listView = findViewById(R.id.listHistory)
        val fab = findViewById<FloatingActionButton>(R.id.fabAddTest)

        // FAB Click -> OPEN YOUR CLINICAL TEST ACTIVITY (The Form)
        fab.setOnClickListener {
            val intent = Intent(this, ClinicalTestActivity::class.java)
            intent.putExtra("PATIENT_ID", patientId)
            intent.putExtra("PATIENT_NAME", patientName)
            startActivity(intent)
        }
    }

    // Refresh list when you come back from adding a test
    override fun onResume() {
        super.onResume()
        loadHistory()
    }

    private fun loadHistory() {
        GlobalScope.launch {
            // Ensure fetchTestsByPatientId exists in ClinicalService
            val tests = ClinicalService.fetchTestsByPatientId(patientId)

            withContext(Dispatchers.Main) {
                if (tests.isNotEmpty()) {
                    val displayList = tests.map {
                        val flag = if(it.flagged) "⚠️ CRITICAL" else "Normal"
                        "${it.type.uppercase()}: ${it.value}\n$flag | ${it.measuredDateTime}"
                    }
                    val adapter = ArrayAdapter(this@PatientHistoryActivity, android.R.layout.simple_list_item_1, displayList)
                    listView.adapter = adapter
                } else {
                    val empty = listOf("No history found.")
                    listView.adapter = ArrayAdapter(this@PatientHistoryActivity, android.R.layout.simple_list_item_1, empty)
                }
            }
        }
    }
}