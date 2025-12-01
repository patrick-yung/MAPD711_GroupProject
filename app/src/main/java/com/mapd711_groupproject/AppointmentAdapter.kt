package com.mapd711_groupproject

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class AppointmentAdapter(
    private val items: MutableList<Appointment>,
    private val context: Context,
    private val onUpdateRequested: (Appointment) -> Unit,
    private val onAttended: (Appointment) -> Unit
) : RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>() {

    inner class AppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val card: MaterialCardView = itemView.findViewById(R.id.cardAppointment)
        val tvPatientName: TextView = itemView.findViewById(R.id.tvPatientName)
        val tvDoctorName: TextView = itemView.findViewById(R.id.tvDoctorName)
        val tvDateTime: TextView = itemView.findViewById(R.id.tvDateTime)
        val tvReason: TextView = itemView.findViewById(R.id.tvReason)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val tvEmergencyBadge: TextView = itemView.findViewById(R.id.tvEmergencyBadge)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.appointment_list_item, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val appt = items[position]

        holder.tvPatientName.text = appt.patientName
        holder.tvDoctorName.text = "Doctor: ${appt.doctorName}"
        holder.tvDateTime.text = appt.appointmentDate
        holder.tvReason.text = "Reason: ${appt.reason}"

        // 🔥 Correct status always updates
        holder.tvStatus.text = "Status: ${appt.status}"

        // 🔥 EMERGENCY UI — Now fully working
        if (appt.isEmergency) {
            holder.tvEmergencyBadge.visibility = View.VISIBLE
            holder.tvEmergencyBadge.background =
                ContextCompat.getDrawable(context, R.drawable.emergency_badge_background)

            holder.card.strokeWidth = 4
            holder.card.strokeColor =
                ContextCompat.getColor(context, android.R.color.holo_red_dark)

        } else {
            holder.tvEmergencyBadge.visibility = View.GONE
            holder.card.strokeWidth = 0
            holder.card.strokeColor = ContextCompat.getColor(context, android.R.color.transparent)
        }

        // ✨ Entry animation
        holder.itemView.alpha = 0f
        holder.itemView.translationY = 40f
        holder.itemView.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(220)
            .setInterpolator(DecelerateInterpolator())
            .start()

        // 🟦 Tap → Update dialog
        holder.itemView.setOnClickListener {
            showUpdateDialog(holder.itemView.context, appt)
        }
    }

    private fun showUpdateDialog(context: Context, appointment: Appointment) {
        val dialogView = LayoutInflater.from(context)
            .inflate(R.layout.dialog_update_appointment, null)

        val tvDialogPatient = dialogView.findViewById<TextView>(R.id.tvDialogPatient)
        val spinnerStatus = dialogView.findViewById<Spinner>(R.id.spinnerStatus)
        val checkEmergency = dialogView.findViewById<CheckBox>(R.id.checkEmergency)

        tvDialogPatient.text = "Patient: ${appointment.patientName}"

        // Load status values
        ArrayAdapter.createFromResource(
            context,
            R.array.appointment_status_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerStatus.adapter = adapter
        }

        // Set current status
        val statusArray = context.resources.getStringArray(R.array.appointment_status_options)
        val currentIndex = statusArray.indexOfFirst {
            it.equals(appointment.status, ignoreCase = true)
        }.takeIf { it >= 0 } ?: 0

        spinnerStatus.setSelection(currentIndex)

        // Set current emergency flag
        checkEmergency.isChecked = appointment.isEmergency

        // Dialog
        AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.update_appointment))
            .setView(dialogView)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(android.R.string.ok) { _, _ ->

                val newStatus = spinnerStatus.selectedItem.toString()
                val newEmergency = checkEmergency.isChecked

                val updated = appointment.copy(
                    status = newStatus,
                    isEmergency = newEmergency
                )

                onUpdateRequested(updated)

                if (newStatus.equals("Attended", ignoreCase = true)) {
                    onAttended(updated)
                }
            }
            .show()
    }

    fun getItemAt(position: Int): Appointment = items[position]

    fun removeAt(position: Int): Appointment {
        val removed = items.removeAt(position)
        notifyItemRemoved(position)
        return removed
    }

    fun replaceAll(newItems: List<Appointment>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun updateItem(updated: Appointment) {
        val index = items.indexOfFirst { it.id == updated.id }
        if (index != -1) {
            items[index] = updated
            notifyItemChanged(index)
        }
    }
}
