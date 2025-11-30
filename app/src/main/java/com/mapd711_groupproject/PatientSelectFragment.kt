package com.mapd711_groupproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PatientSelectFragment : Fragment() {

    private var selectedPatient: ClinicalPatient? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_patient_select, container, false)

        val spinner = view.findViewById<Spinner>(R.id.spinnerPatient)
        val btnGo = view.findViewById<Button>(R.id.btnViewRecord)

        // Load Patients into Spinner
        GlobalScope.launch {
            val patients = ClinicalPatientService.fetchPatientNamesAndIds()

            withContext(Dispatchers.Main) {
                if (patients.isNotEmpty()) {
                    val names = patients.map { it.name }
                    val adapter =
                        ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, names)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner.adapter = adapter

                    spinner.onItemSelectedListener =
                        object : android.widget.AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                p0: android.widget.AdapterView<*>?,
                                p1: View?,
                                position: Int,
                                p3: Long
                            ) {
                                selectedPatient = patients[position]
                            }

                            override fun onNothingSelected(p0: android.widget.AdapterView<*>?) {}
                        }
                } else {
                    Toast.makeText(requireContext(), "No patients found", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Click Button Open History Activity
//        btnGo.setOnClickListener {
//            if (selectedPatient != null) {
//                // OPEN THE HISTORY ACTIVITY
//                val intent = Intent(requireContext(), PatientHistoryActivity::class.java)
//                intent.putExtra("PATIENT_ID", selectedPatient!!._id)
//                intent.putExtra("PATIENT_NAME", selectedPatient!!.name)
//                startActivity(intent)
//            } else {
//                Toast.makeText(requireContext(), "Please wait for patients to load", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        return view
//    }
//}
// Click Listener
        btnGo.setOnClickListener {
            Log.d("DEBUG_APP", "Button Clicked!") // <--- LOG

            if (selectedPatient != null) {
                Log.d(
                    "DEBUG_APP",
                    "Launching History Activity for: ${selectedPatient!!.name}"
                ) // <--- LOG

                try {
                    val intent = Intent(requireContext(), PatientHistoryActivity::class.java)
                    intent.putExtra("PATIENT_ID", selectedPatient!!._id)
                    intent.putExtra("PATIENT_NAME", selectedPatient!!.name)
                    startActivity(intent)
                } catch (e: Exception) {
                    Log.e("DEBUG_APP", "CRASH LAUNCHING ACTIVITY: ${e.message}") // <--- CATCH CRASH
                    e.printStackTrace()
                }
            } else {
                Log.d("DEBUG_APP", "Patient is NULL") // <--- LOG
                Toast.makeText(requireContext(), "Please select a patient", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        return view
    }
}