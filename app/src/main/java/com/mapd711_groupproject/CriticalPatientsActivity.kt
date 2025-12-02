package com.mapd711_groupproject
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CriticalPatientsActivity : AppCompatActivity() {

    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_critical_patients)

        listView = findViewById(R.id.listCriticalTests)
        loadCriticalData()
    }

    // Load data when returning to the screen
    override fun onResume() {
        super.onResume()
        loadCriticalData()
    }

    private fun loadCriticalData() {
        GlobalScope.launch {
            val patients = ClinicalPatientService.fetchPatientNamesAndIds()
            val patientMap = patients.associate { it._id to it.name }

            //Define the types
            val typesToCheck = listOf("blood pressure", "heart rate", "respiratory rate", "spo2")
            val criticalList = mutableListOf<String>()

            for (type in typesToCheck) {
                val tests = ClinicalService.fetchTestsByType(type)

                // Filter for Critical (flagged == true)
                val flaggedTests = tests.filter { it.flagged }

                for (test in flaggedTests) {
                    val pName = patientMap[test.patientId] ?: "Unknown Patient"

                    // Format the display string
                    val entry = """
                        Patient: $pName
                        Type: ${test.type.uppercase()}
                        Value: ${test.value}
                        Date: ${test.measuredDateTime}
                    """.trimIndent()

                    criticalList.add(entry)
                }
            }


            withContext(Dispatchers.Main) {
                if (criticalList.isNotEmpty()) {
                    val adapter = ArrayAdapter(this@CriticalPatientsActivity, android.R.layout.simple_list_item_1, criticalList)
                    listView.adapter = adapter
                } else {
                    val emptyList = listOf("Good News: No Critical Patients Found.")
                    listView.adapter = ArrayAdapter(this@CriticalPatientsActivity, android.R.layout.simple_list_item_1, emptyList)
                }
            }
        }
    }
}