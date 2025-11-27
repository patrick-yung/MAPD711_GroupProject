package com.mapd711_groupproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AppointmentAdapter(
    private val items: List<Appointment>
) : RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>() {

    class AppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textPatientName: TextView =
            itemView.findViewById(R.id.textViewAppointmentPatientName)
        val textDetails: TextView =
            itemView.findViewById(R.id.textViewAppointmentDetails)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.appointment_list_item, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val item = items[position]
        val ctx = holder.itemView.context

        holder.textPatientName.text = item.patientName

        val detailsText =
            "Doctor: ${item.doctorName}\n" +
                    "Date: ${item.appointmentDate}\n" +
                    "Reason: ${item.reason}\n" +
                    "Status: ${item.status}"

        holder.textDetails.text = detailsText
    }

    override fun getItemCount(): Int = items.size
}
