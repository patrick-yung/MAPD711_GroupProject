package com.mapd711_groupproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PatientAdapter(private val patientList: List<Patient>) :
    RecyclerView.Adapter<PatientAdapter.PatientViewHolder>() {

    // This class holds the views for a single list item (one row).
    class PatientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.textViewPatientName)
        val detailsTextView: TextView = itemView.findViewById(R.id.textViewPatientDetails)
    }

    // Creates a new row's layout.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.patients_list_display, parent, false)
        return PatientViewHolder(view)
    }

    // Populates a row with data from a specific patient.
    override fun onBindViewHolder(holder: PatientViewHolder, position: Int) {
        val patient = patientList[position]
        holder.nameTextView.text = patient.name
        holder.detailsTextView.text = "Age: ${patient.age}, Department: ${patient.department}"
    }

    // Returns the total number of items in the list.
    override fun getItemCount(): Int {
        return patientList.size
    }
}
