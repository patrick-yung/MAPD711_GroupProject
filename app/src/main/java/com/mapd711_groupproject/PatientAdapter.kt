package com.mapd711_groupproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PatientAdapter(private val patientsList: List<PatientModel>) :
    RecyclerView.Adapter<PatientAdapter.PatientViewHolder>() {
    // Adapter class for the RecyclerView
    class PatientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val patientNameText: TextView = itemView.findViewById(R.id.patientNameText)
        val patientAgeText: TextView = itemView.findViewById(R.id.patientAgeText)
        val patientGenderText: TextView = itemView.findViewById(R.id.patientGenderText)
        val patientPhoneText: TextView = itemView.findViewById(R.id.patientPhoneText)
        val patientAddressText: TextView = itemView.findViewById(R.id.patientAddressText)
        val patientConditionText: TextView = itemView.findViewById(R.id.patientConditionText)
    }

    // ViewHolder class for the RecyclerView items
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientViewHolder
    {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_patient, parent, false)
        return PatientViewHolder(itemView)
    }
     //binds the data to the views
    override fun onBindViewHolder(holder: PatientViewHolder, position: Int)
    {
        val currentPatient = patientsList[position]
        holder.patientNameText.text = "Name: ${currentPatient.PatientName}"
        holder.patientAgeText.text = "Age: ${currentPatient.PatientAge}"
        holder.patientGenderText.text = "Gender: ${currentPatient.PatientGender}"
        holder.patientPhoneText.text = "Phone: ${currentPatient.PatientPhone}"
        holder.patientAddressText.text = "Address: ${currentPatient.PatientAddress}"
        holder.patientConditionText.text = "Condition: ${currentPatient.PatientCondition}"
    }

    //returns the number of items in the list
    override fun getItemCount(): Int {
        return patientsList.size
    }


}