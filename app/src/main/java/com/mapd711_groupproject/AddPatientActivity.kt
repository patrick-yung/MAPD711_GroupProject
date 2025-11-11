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

        // ✅ navigation drawer (hamburger)
        setupDrawer(R.id.nav_home)

        // ✅ edge-to-edge layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 🧩 input fields
        val name = findViewById<EditText>(R.id.editTextName)
        val age = findViewById<EditText>(R.id.editTextAge)
        val phone = findViewById<EditText>(R.id.editTextPhone)
        val condition = findViewById<EditText>(R.id.editTextCondition)
        val saveButton = findViewById<Button>(R.id.button2)

        val isEdit = intent.getBooleanExtra("isEdit", false)
        if (isEdit) {
            name.setText(intent.getStringExtra("patientName"))
            age.setText(intent.getStringExtra("patientAge"))
            phone.setText(intent.getStringExtra("patientPhone"))
            condition.setText(intent.getStringExtra("patientCondition"))
        }

        // ✅ validation and save logic
        saveButton.setOnClickListener {
            val nameText = name.text.toString().trim()
            val ageText = age.text.toString().trim()
            val phoneText = phone.text.toString().trim()
            val conditionText = condition.text.toString().trim()

            if (nameText.isEmpty() || conditionText.isEmpty() ||
                ageText.isEmpty() || phoneText.isEmpty()
            ) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (ageText.toIntOrNull() == null) {
                Toast.makeText(this, "Please enter a valid age", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (phoneText.toIntOrNull() == null) {
                Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(this, "Patient Added", Toast.LENGTH_SHORT).show()

            // ✅ send data back
            val resultIntent = Intent().apply {
                putExtra("patientName", nameText)
                putExtra("patientAge", ageText)
                putExtra("patientPhone", phoneText)
                putExtra("patientCondition", conditionText)
            }

            setResult(Activity.RESULT_OK, resultIntent)

            // clear inputs
            name.text.clear()
            age.text.clear()
            phone.text.clear()
            condition.text.clear()

            finish()
        }
    }
}
