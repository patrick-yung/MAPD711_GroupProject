package com.mapd711_groupproject

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AddPatientActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_patient)

        // ✅ Setup navigation drawer
        setupDrawer(R.id.nav_home)

        // 🧩 Handle window insets for edge-to-edge UI
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 🧩 Setup UI components
        val nameInput = findViewById<EditText>(R.id.editTextName)
        val ageInput = findViewById<EditText>(R.id.editTextAge)
        val phoneInput = findViewById<EditText>(R.id.editTextPhone)
        val conditionInput = findViewById<EditText>(R.id.editTextCondition)
        val saveButton = findViewById<Button>(R.id.button2)

        val isEdit = intent.getBooleanExtra("isEdit", false)
        if (isEdit) {
            nameInput.setText(intent.getStringExtra("patientName"))
            ageInput.setText(intent.getStringExtra("patientAge"))
            phoneInput.setText(intent.getStringExtra("patientPhone"))
            conditionInput.setText(intent.getStringExtra("patientCondition"))
        }

        saveButton.setOnClickListener {
            val name = nameInput.text.toString()
            val age = ageInput.text.toString()
            val phone = phoneInput.text.toString()
            val condition = conditionInput.text.toString()

            val resultIntent = Intent().apply {
                putExtra("patientName", name)
                putExtra("patientAge", age)
                putExtra("patientPhone", phone)
                putExtra("patientCondition", condition)
            }

            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
}
