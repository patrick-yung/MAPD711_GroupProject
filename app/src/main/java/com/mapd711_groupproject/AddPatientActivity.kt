package com.mapd711_groupproject

<<<<<<< HEAD
import android.os.Bundle

class AddPatientActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_patient)
        setupDrawer(R.id.nav_home)
    }
}
=======
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

        val isEdit = intent.getBooleanExtra("isEdit", false)
        if (isEdit) {
            name.setText(intent.getStringExtra("patientName"))
            age.setText(intent.getStringExtra("patientAge"))
            phone.setText(intent.getStringExtra("patientPhone"))
            condition.setText(intent.getStringExtra("patientCondition"))
        }

        //save button will save the patient information and show a toast message
        saveBtn.setOnClickListener {

            //validation and for age and phone only take number
            if (age.text.toString().toIntOrNull() == null) {
                Toast.makeText(this, "Please enter a valid age", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (phone.text.toString().toIntOrNull() == null) {
                Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //validation for name and condition cannot be empty
            if (name.text.isEmpty()  || condition.text.isEmpty()) {
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
>>>>>>> 9fbd727a9c42aef0b94fdce1dc2f347d6bfdb20b
