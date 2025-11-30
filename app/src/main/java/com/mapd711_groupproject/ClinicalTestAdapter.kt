package com.mapd711_groupproject

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ClinicalTestAdapter(private val testList: List<ClinicalTestResponse>) :
    RecyclerView.Adapter<ClinicalTestAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val type: TextView = view.findViewById(android.R.id.text1)
        val value: TextView = view.findViewById(android.R.id.text2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // We use a simple standard Android layout to save time
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val test = testList[position]

        holder.type.text = "${test.type.uppercase()} (Patient ID: ${test.patientId})"
        holder.value.text = "Result: ${test.value} | Date: ${test.measuredDateTime}"

        // Highlight critical items in RED
        if (test.flagged) {
            holder.value.setTextColor(Color.RED)
            holder.value.text = "${holder.value.text} ⚠️ CRITICAL"
        } else {
            holder.value.setTextColor(Color.DKGRAY)
        }
    }

    override fun getItemCount() = testList.size
}