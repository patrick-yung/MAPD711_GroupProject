package com.mapd711_groupproject

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AddPatientActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_patient)

        setupDrawer(R.id.nav_home)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val name = findViewById<EditText>(R.id.editTextName)
        val age = findViewById<EditText>(R.id.editTextAge)
        val phone = findViewById<EditText>(R.id.editTextPhone)
        val condition = findViewById<EditText>(R.id.editTextCondition)
        val selectGender = findViewById<RadioGroup>(R.id.radioGroupGender)

        val saveButton = findViewById<Button>(R.id.button2)
        var gender = ""

        val isEdit = intent.getBooleanExtra("isEdit", false)
        if (isEdit) {
            name.setText(intent.getStringExtra("patientName"))
            age.setText(intent.getStringExtra("patientAge"))
            phone.setText(intent.getStringExtra("patientPhone"))
            condition.setText(intent.getStringExtra("patientCondition"))
        }

        saveButton.setOnClickListener {
            val nameText = name.text.toString().trim()
            val ageText = age.text.toString().trim()
            val phoneText = phone.text.toString().trim()
            val conditionText = condition.text.toString().trim()

            gender = when (selectGender.checkedRadioButtonId) {
                R.id.radioMale -> "Male"
                R.id.radioFemale -> "Female"
                else -> ""
            }
            if (gender.isEmpty()) {
                Toast.makeText(this, "Please fill in all Gender fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // Stop if any field is empty
            }

            if (nameText.isEmpty() || ageText.isEmpty() || phoneText.isEmpty() || conditionText.isEmpty() || gender.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // Stop if any field is empty
            }

            if (ageText.toIntOrNull() == null) {
                Toast.makeText(this, "Please enter a valid age", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // Stop if age is not a valid number
            }

            val resultIntent = Intent().apply {
                putExtra("patientName", nameText)
                putExtra("patientAge", ageText)
                putExtra("patientPhone", phoneText)
                putExtra("patientGender", gender)
                putExtra("patientCondition", conditionText)
            }

            setResult(Activity.RESULT_OK, resultIntent)

            Toast.makeText(this, "Patient Data Saved", Toast.LENGTH_SHORT).show()

            finish()
        }
    }
}
