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
import android.content.Context
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider


class AddPatientActivity : AppCompatActivity() {

    lateinit var patientViewModel: PatientViewModel
    lateinit var context: Context
    lateinit var patientName: String
    lateinit var patientAge: String
    lateinit var patientGender: String
    lateinit var patientPhone: String
    lateinit var patientAddress: String
    lateinit var patientCondition: String





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_patient)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        context = this@AddPatientActivity
        patientViewModel = ViewModelProvider(this).get(PatientViewModel::class.java)


        var name = findViewById<EditText>(R.id.editTextName)
        var age = findViewById<EditText>(R.id.editTextAge)
        var gender = findViewById<EditText>(R.id.editTextGender)
        var phone = findViewById<EditText>(R.id.editTextPhone)
        var address = findViewById<EditText>(R.id.editTextAddress)
        var condition = findViewById<EditText>(R.id.editTextCondition)
        var saveBtn = findViewById<Button>(R.id.button2)

        val isEdit = intent.getBooleanExtra("isEdit", false)
        if (isEdit) {
            name.setText(intent.getStringExtra("patientName"))
            age.setText(intent.getStringExtra("patientAge"))
            gender.setText(intent.getStringExtra("patientGender"))
            address.setText(intent.getStringExtra("patientAddress"))
            phone.setText(intent.getStringExtra("patientPhone"))
            condition.setText(intent.getStringExtra("patientCondition"))
        }

        //save button will save the patient information and show a toast message
        saveBtn.setOnClickListener {

            patientName = name.text.toString().trim()
            patientAge = age.text.toString().trim()
            patientGender = gender.text.toString().trim()
            patientPhone = phone.text.toString().trim()
            patientAddress = address.text.toString().trim()
            patientCondition = condition.text.toString().trim()

            patientViewModel.insertPatient(context, patientName, patientAge, patientGender, patientPhone, patientAddress, patientCondition)

            //validation for patient name, patient gender, patient address and patient condition cannot be empty
            if (patientName.isEmpty() || patientGender.isEmpty() || patientAddress.isEmpty() || patientCondition.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //validation for patient age and phone cannot be empty and patient age and phone only take only number
            if (patientAge.isEmpty() || patientPhone.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (patientAge.toIntOrNull() == null) {
                Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //observer and observe() used to work with live data objects
            patientViewModel.getPatients(context, patientName)?.observe(this, Observer {
                if (it == null) {
                    Toast.makeText(this, "Patient not found", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Patient found", Toast.LENGTH_SHORT).show()
                }
            })

            //show the patient name in home activity
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("patientName", patientName)
            intent.putExtra("patientAge", patientAge)
            intent.putExtra("patientGender", patientGender)
            intent.putExtra("patientPhone", patientPhone)
            intent.putExtra("patientAddress", patientAddress)
            intent.putExtra("patientCondition", patientCondition)
            setResult(Activity.RESULT_OK, intent)
            finish()

            //show toast message
            Toast.makeText(this, "Patient Added", Toast.LENGTH_SHORT).show()
            //clear the fields
            name.text.clear()
            age.text.clear()
            gender.text.clear()
            address.text.clear()
            phone.text.clear()
            condition.text.clear()

        }
    }
}