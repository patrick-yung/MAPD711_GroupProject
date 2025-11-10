package com.mapd711_groupproject

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AddPatientActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_patient)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var name = findViewById<EditText>(R.id.editTextName)
        var age = findViewById<EditText>(R.id.editTextAge)
        var phone = findViewById<EditText>(R.id.editTextPhone)
        var condition = findViewById<EditText>(R.id.editTextCondition)
        var saveBtn = findViewById<Button>(R.id.button2)

        //save button will save the patient information and show a toast message
        saveBtn.setOnClickListener {
            //validation
            if (name.text.isEmpty() || age.text.isEmpty() || phone.text.isEmpty() || condition.text.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            //save patient information
            val patientName = name.text.toString()
            val patientAge = age.text.toString()
            val patientPhone = phone.text.toString()
            val patientCondition = condition.text.toString()
            //show toast message
            Toast.makeText(this, "Patient Added", Toast.LENGTH_SHORT).show()
            //clear the fields
            name.text.clear()
            age.text.clear()
            phone.text.clear()
            condition.text.clear()

            //show the data in home activity use intent
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("patientName", patientName)
            intent.putExtra("patientAge", patientAge)
            intent.putExtra("patientPhone", patientPhone)
            intent.putExtra("patientCondition", patientCondition)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }


    }
}