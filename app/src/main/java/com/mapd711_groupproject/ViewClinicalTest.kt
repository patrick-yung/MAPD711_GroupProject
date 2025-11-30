package com.mapd711_groupproject

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewClinicalTest : Fragment() {

    // Variables to hold selected data
    private var selectedPatient: ClinicalPatient? = null
    private lateinit var spinnerTestType: Spinner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_view_clinical_test, container, false)

        val containerForm = view.findViewById<View>(R.id.containerForm)
        val containerResult = view.findViewById<View>(R.id.containerResult)

//        val patientNameDisplay = view.findViewById<TextView>(R.id.textViewPatientNameDisplay)
        val spinnerPatientSelect = view.findViewById<Spinner>(R.id.spinnerPatientSelect)
        val btnSaveTest = view.findViewById<Button>(R.id.btnSaveTest)
        val testDateEditText = view.findViewById<EditText>(R.id.testDate)
        val etNotes = view.findViewById<EditText>(R.id.etNotes)
        val tvReceiptDetails = view.findViewById<TextView>(R.id.tvReceiptDetails)
        val btnClose = view.findViewById<Button>(R.id.btnClose)

        // Initialize the class property
        spinnerTestType = view.findViewById(R.id.spinnerTestType)

        //FETCH PATIENTS
        GlobalScope.launch {
            val patients = ClinicalPatientService.fetchPatientNamesAndIds()

            withContext(Dispatchers.Main) {
                if (patients.isNotEmpty()) {
                    val names = mutableListOf("Select Patient")
                    // Map the names for the spinner
                    names.addAll(patients.map { it.name })

                    val adapter =
                        ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, names)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerPatientSelect.adapter = adapter

                    spinnerPatientSelect.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>,
                                v: View?,
                                pos: Int,
                                id: Long
                            ) {
                                if (pos > 0) {
                                    selectedPatient = patients[pos - 1]
                                    // patientNameDisplay.text = "Selected: ${selectedPatient!!.name}"
                                } else {
                                    selectedPatient = null
                                    // patientNameDisplay.text = "Patient: Not Selected"
                                }
                            }

                            override fun onNothingSelected(parent: AdapterView<*>) {}
                        }
                }
//                } else {
//                    patientNameDisplay.text = "No patients found"
//                }
            }
        }
        //Setup Test Type Spinner
        val testTypes = resources.getStringArray(R.array.select_test_type)
        val typeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, testTypes)
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTestType.adapter = typeAdapter

        //Date Picker Logic
        val datePickerDialog = DatePickerDialog(requireContext())
        datePickerDialog.setOnDateSetListener { _, year, month, dayOfMonth ->
            val dateStr = "$dayOfMonth/${month + 1}/$year"
            testDateEditText.setText(dateStr)
        }
        testDateEditText.setOnClickListener { datePickerDialog.show() }

        //Setup Visibility Groups
        val groupHeart = view.findViewById<View>(R.id.groupHeart)
        val groupBP = view.findViewById<View>(R.id.groupBP)
        val groupResp = view.findViewById<View>(R.id.groupResp)
        val etHeartRate = view.findViewById<EditText>(R.id.etHeartRate)
        val etSystolic = view.findViewById<EditText>(R.id.etSystolic)
        val etDiastolic = view.findViewById<EditText>(R.id.etDiastolic)
        val etBPpulse = view.findViewById<EditText>(R.id.etBPpulse)
        val etRespRate = view.findViewById<EditText>(R.id.etRespRate)

        spinnerTestType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                val type = parent.getItemAtPosition(pos).toString()

                // Show/Hide logic
                groupHeart.visibility = if (type == "Heart Rate") View.VISIBLE else View.GONE
                groupBP.visibility = if (type == "Blood Pressure") View.VISIBLE else View.GONE
                groupResp.visibility = if (type == "Respiratory Rate") View.VISIBLE else View.GONE

                // Reset fields logic...
                if (type != "Heart Rate") etHeartRate.setText("")
                if (type != "Blood Pressure") { etSystolic.setText(""); etDiastolic.setText(""); etBPpulse.setText("") }
                if (type != "Respiratory Rate") etRespRate.setText("")
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // SAVE BUTTON
        btnSaveTest.setOnClickListener {
            if (selectedPatient == null) {
                Toast.makeText(requireContext(), "Please select a patient", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            // Prepare Data
            val type =
                spinnerTestType.selectedItem.toString().lowercase() // Backend wants lowercase
            var value = ""

            if (type == "blood pressure") {
                if (etSystolic.text.isEmpty() || etDiastolic.text.isEmpty()) {
                    Toast.makeText(requireContext(), "Enter BP values", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                value = "${etSystolic.text}/${etDiastolic.text}"
            } else if (type == "heart rate") {
                value = etHeartRate.text.toString()
            } else if (type == "respiratory rate") {
                value = etRespRate.text.toString()
            }

            if (value.isEmpty()) {
                Toast.makeText(requireContext(), "Enter a test value", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create Request Object
            val request = ClinicalTestRequest(
                patientId = selectedPatient!!._id,
                type = type,
                value = value
            )

            // Send to Server AND Navigate Back
            GlobalScope.launch {
                val response = ClinicalService.uploadTest(requireContext(), request)

                withContext(Dispatchers.Main) {
                    if (response != null) {
                        containerForm.visibility = View.GONE
                        containerResult.visibility = View.VISIBLE

                        // 3. Fill the Receipt Text
                        val status = if (response.flagged) "CRITICAL ⚠️" else "Normal ✅"
                        tvReceiptDetails.text = """
                            Patient: ${selectedPatient!!.name}
                            Type: ${response.type.uppercase()}
                            Result: ${response.value}
                            Status: $status
                            Date: ${response.measuredDateTime}
                        """.trimIndent()

                    } else {
                        Toast.makeText(requireContext(), "Save failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // 4. CLOSE BUTTON
        btnClose.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return view
    }
}

