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
        val textPatientName: TextView = itemView.findViewById(R.id.textViewAppointmentPatientName)
        val textDetails: TextView = itemView.findViewById(R.id.textViewAppointmentDetails)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.appointment_list_item, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val item = items[position]

        holder.textPatientName.text = item.patientName

        val details = "${holder.itemView.context.getString(R.string.doctor_label)}: ${item.doctorName}\n" +
                "${holder.itemView.context.getString(R.string.date_label)}: ${item.appointmentDate}\n" +
                "${holder.itemView.context.getString(R.string.reason_label)}: ${item.reason}\n" +
                "${holder.itemView.context.getString(R.string.status_label)}: ${item.status}"

        holder.textDetails.text = details
    }

    override fun getItemCount(): Int = items.size
}
