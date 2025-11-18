package com.mapd711_groupproject

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AddPatientActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_patient)

        // ✅ Sets up the navigation drawer (hamburger menu)
        setupDrawer(R.id.nav_home)

        // ✅ Handles the edge-to-edge display insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 🧩 Gets references to the input fields
        val name = findViewById<EditText>(R.id.editTextName)
        val age = findViewById<EditText>(R.id.editTextAge)
        val phone = findViewById<EditText>(R.id.editTextPhone)
        val condition = findViewById<EditText>(R.id.editTextCondition)
        val saveButton = findViewById<Button>(R.id.button2) // Make sure R.id.button2 is your Save button

        // Handles if the activity was started in "edit mode"
        val isEdit = intent.getBooleanExtra("isEdit", false)
        if (isEdit) {
            // Pre-fill fields with existing data
            name.setText(intent.getStringExtra("patientName"))
            age.setText(intent.getStringExtra("patientAge"))
            phone.setText(intent.getStringExtra("patientPhone"))
            condition.setText(intent.getStringExtra("patientCondition"))
        }

        // ✅ Sets up validation and save logic for the button click
        saveButton.setOnClickListener {
            val nameText = name.text.toString().trim()
            val ageText = age.text.toString().trim()
            val phoneText = phone.text.toString().trim()
            val conditionText = condition.text.toString().trim()

            // --- Validation ---
            if (nameText.isEmpty() || ageText.isEmpty() || phoneText.isEmpty() || conditionText.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // Stop if any field is empty
            }

            if (ageText.toIntOrNull() == null) {
                Toast.makeText(this, "Please enter a valid age", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // Stop if age is not a valid number
            }

            // A phone number can be very long, so validating it as an Int is risky.
            // A simple check for emptiness is sufficient for now.

            // --- Send Data Back ---
            val resultIntent = Intent().apply {
                putExtra("patientName", nameText)
                putExtra("patientAge", ageText)
                putExtra("patientPhone", phoneText)
                putExtra("patientCondition", conditionText)
            }

            // Set the result to OK and attach the data
            setResult(Activity.RESULT_OK, resultIntent)

            // Show a success message
            Toast.makeText(this, "Patient Data Saved", Toast.LENGTH_SHORT).show()

            // Close this activity and return to the previous one
            finish()
        }
    }
}
