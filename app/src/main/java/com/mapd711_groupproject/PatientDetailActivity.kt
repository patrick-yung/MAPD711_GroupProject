package com.mapd711_groupproject

import android.content.Intent
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
            val ageTextView: TextView = findViewById(R.id.textViewAge)
            val genderTextView: TextView = findViewById(R.id.textViewGender)
            val contactTextView: TextView = findViewById(R.id.textViewContact)
            val historyTextView: TextView = findViewById(R.id.textViewHistory)

            // Set patient data
            nameTextView.text = it.name
            ageTextView.text = "Age: ${it.age}"
            genderTextView.text = "Gender: ${it.gender}"
            contactTextView.text = "Contact: ${it.contact}"
            historyTextView.text = "Medical History: ${it.history}"
        }
    }
}