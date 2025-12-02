package com.mapd711_groupproject

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ClinicalTestActivity : AppCompatActivity() {

    private fun saveTestResult(
        name: String,
        testType: String,
        testDate: String,
        heartRate: String,
        systolic: String,
        diastolic: String,
        bpPulse: String,
        respRate: String,
        notes: String
    ) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_clinical_test)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //take patient name from add patient activityjony
        val patientName = intent.getStringExtra("PATIENT_NAME")
        val patientId = intent.getStringExtra("PATIENT_ID") ?: ""

        val etPatientName = findViewById<EditText>(R.id.patientName)
        etPatientName.setText(patientName)

        //spinner for spinner test type
        val spinnerTestType = findViewById<Spinner>(R.id.spinnerTestType)
        val testTypes = resources.getStringArray(R.array.select_test_type)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, testTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTestType.adapter = adapter

        val selectDate = findViewById<EditText>(R.id.testDate)
        //date picker dialog
        val datePickerDialog = DatePickerDialog(this)
        datePickerDialog.setOnDateSetListener { _, year, month, dayOfMonth ->
            val selectedDate = "$dayOfMonth/${month + 1}/$year"
            selectDate.setText(selectedDate)
        }
        selectDate.setOnClickListener {
            datePickerDialog.show()
        }

        val groupHeart = findViewById<View>(R.id.groupHeart)
        val groupBP = findViewById<View>(R.id.groupBP)
        val groupResp = findViewById<View>(R.id.groupResp)
        val etHeartRate = findViewById<EditText>(R.id.etHeartRate)
        val etSystolic = findViewById<EditText>(R.id.etSystolic)
        val etDiastolic = findViewById<EditText>(R.id.etDiastolic)
        val etBPpulse = findViewById<EditText>(R.id.etBPpulse)
        val etRespRate = findViewById<EditText>(R.id.etRespRate)


        //select test type from spinner then take that type result
        spinnerTestType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                val type = parent.getItemAtPosition(pos).toString()
                groupHeart.visibility = if (type == "Heart Rate") View.VISIBLE else View.GONE
                groupBP.visibility = if (type == "Blood Pressure") View.VISIBLE else View.GONE
                groupResp.visibility = if (type == "Respiratory Rate") View.VISIBLE else View.GONE

                // Clear all fields not in the current group
                if (type != "Heart Rate") etHeartRate.setText("")
                if (type != "Blood Pressure") {
                    etSystolic.setText("")
                    etDiastolic.setText("")
                    etBPpulse.setText("")
                }
                if (type != "Respiratory Rate") etRespRate.setText("")
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        //save button
        val btnSaveTest = findViewById<View>(R.id.btnSaveTest)
        val tvSavedTests = findViewById<TextView>(R.id.tvSavedTests)
        tvSavedTests.visibility = View.VISIBLE
        tvSavedTests.text = "No tests saved yet"

        btnSaveTest.setOnClickListener {
            //Gather Data from Inputs
            val type = spinnerTestType.selectedItem.toString().lowercase()
            var value = ""

            // Formatting Logic
            if (type == "blood pressure") {
                if (etSystolic.text.isEmpty() || etDiastolic.text.isEmpty()) {
                    Toast.makeText(this, "Enter BP values", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                value = "${etSystolic.text}/${etDiastolic.text}"
            } else if (type == "heart rate") {
                value = etHeartRate.text.toString()
            } else if (type == "respiratory rate") {
                value = etRespRate.text.toString()
            }

            // Validation
            if (value.isEmpty()) {
                Toast.makeText(this, "Enter a test value", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            val request = ClinicalTestRequest(
                patientId = patientId,
                type = type,
                value = value
            )


            GlobalScope.launch {
                val response = ClinicalService.uploadTest(this@ClinicalTestActivity, request)

                withContext(Dispatchers.Main) {
                    if (response != null) {
                        Toast.makeText(
                            this@ClinicalTestActivity,
                            "Saved successfully",
                            Toast.LENGTH_SHORT
                        ).show()


                        finish()
                    } else {
                        Toast.makeText(
                            this@ClinicalTestActivity,
                            "Save failed. Try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}