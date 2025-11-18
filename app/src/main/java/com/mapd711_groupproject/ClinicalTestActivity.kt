package com.mapd711_groupproject

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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
        val patientName = intent.getStringExtra("patientName")
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
            val patientName = etPatientName.text.toString()
            val testType = spinnerTestType.selectedItem.toString()
            val testDate = selectDate.text.toString()
            val heartRate = etHeartRate.text.toString()
            val systolic = etSystolic.text.toString()
            val diastolic = etDiastolic.text.toString()
            val bpPulse = etBPpulse.text.toString()
            val respRate = etRespRate.text.toString()
            val notes = findViewById<EditText>(R.id.etNotes).text.toString()
            saveTestResult(
                patientName,
                testType,
                testDate,
                heartRate,
                systolic,
                diastolic,
                bpPulse,
                respRate,
                notes
            )

            //test result will be saved as shared preference
            val sharedPreferences = getSharedPreferences("test_results", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("patientName", patientName)
            editor.putString("testType", testType)
            editor.putString("testDate", testDate)
            editor.putString("heartRate", heartRate)
            editor.putString("systolic", systolic)
            editor.putString("diastolic", diastolic)
            editor.putString("bpPulse", bpPulse)
            editor.putString("respRate", respRate)
            editor.putString("notes", notes)
            editor.apply()

            //test result will be shown in text view
            val resultText = StringBuilder()
            resultText.append("Patient Name: $patientName\n")
            resultText.append("Test Type: $testType\n")
            resultText.append("Test Date: $testDate\n")

            when (testType) {
                "Heart Rate" -> resultText.append("Heart Rate: $heartRate\n")
                "Blood Pressure" -> resultText.append("Systolic: $systolic\nDiastolic: $diastolic\nPulse: $bpPulse\n")
                "Respiratory Rate" -> resultText.append("Resp Rate: $respRate\n")
            }

            if (notes.isNotEmpty()) resultText.append("Notes: $notes\n")
            tvSavedTests.text = resultText.toString()
        }


    }
}