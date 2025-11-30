package com.mapd711_groupproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class PatientDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_detail)

        // Get the patient data from intent
        val patient = intent.getSerializableExtra("PATIENT_DATA") as? PatientService.Patient

        patient?.let {
            // Initialize views
            val nameTextView: TextView = findViewById(R.id.textViewPatientName)
            val ageTextView: TextView = findViewById(R.id.editTextAge)
            val genderTextView: TextView = findViewById(R.id.editTextGender)
            val contactTextView: TextView = findViewById(R.id.editTextContact)
            val historyTextView: TextView = findViewById(R.id.editTextHistory)

            // Set patient data
            nameTextView.text = it.name
            ageTextView.text = "${it.age}"
            genderTextView.text = "${it.gender}"
            contactTextView.text = "${it.contact}"
            historyTextView.text = "${it.history}"
        }
    }
}