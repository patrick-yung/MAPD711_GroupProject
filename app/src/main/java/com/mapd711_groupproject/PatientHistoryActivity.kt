package com.mapd711_groupproject

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PatientHistoryActivity : AppCompatActivity() {

    private lateinit var patientId: String
    private lateinit var patientName: String
    private lateinit var listView: ListView

    //store the list
    private var allTestsList: List<ClinicalTestResponse> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_history)

        // Get Data from Intent
        patientId = intent.getStringExtra("PATIENT_ID") ?: ""
        patientName = intent.getStringExtra("PATIENT_NAME") ?: ""

        findViewById<TextView>(R.id.tvHistoryTitle).text = "$patientName's History"
        listView = findViewById(R.id.listHistory)
        val fab = findViewById<ExtendedFloatingActionButton>(R.id.fabAddTest)
        val spinnerFilter = findViewById<Spinner>(R.id.spinnerFilter)

        //setup filter spinner
        val filterOptions = listOf("All Tests", "Blood Pressure", "Heart Rate", "Respiratory Rate")
        val filterAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, filterOptions)
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFilter.adapter = filterAdapter

        spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedType = filterOptions[position]
                applyFilter(selectedType)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // open clinical test activity
        fab.setOnClickListener {
            val intent = Intent(this, ClinicalTestActivity::class.java)
            intent.putExtra("PATIENT_ID", patientId)
            intent.putExtra("PATIENT_NAME", patientName)
            startActivity(intent)
        }
    }

    // Refresh list when come back from adding a test
    override fun onResume() {
        super.onResume()
        loadHistory()
    }

    private fun loadHistory() {
        GlobalScope.launch {
            // Fetch ALL data for this patient
            val tests = ClinicalService.fetchTestsByPatientId(patientId)

            withContext(Dispatchers.Main) {
                // Save the full list for filtering
                allTestsList = tests

                // Show "All Tests" by default (trigger the filter to update UI)
                applyFilter("All Tests")
            }
        }
    }

    private fun applyFilter(filterType: String) {
        val filteredList = if (filterType == "All Tests") {
            allTestsList
        } else {
            allTestsList.filter { it.type.equals(filterType, ignoreCase = true) }
        }

        if (filteredList.isNotEmpty()) {
            val displayList = filteredList.map {
                val flag = if(it.flagged) "⚠️ CRITICAL" else "Normal"
                "${it.type.uppercase()}: ${it.value}\n$flag | ${it.measuredDateTime}"
            }
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, displayList)
            listView.adapter = adapter
        } else {
            val empty = listOf("No records found for $filterType.")
            listView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, empty)
        }
    }
}