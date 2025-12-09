package com.mapd711_groupproject

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class AppointmentAdapter(
    private val items: MutableList<Appointment>,
    private val context: Context,
    private val onUpdateRequested: (Appointment) -> Unit,
    private val onDeleteRequested: (Appointment, Int) -> Unit,
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
        val tvAttendedBadge: TextView = itemView.findViewById(R.id.tvAttendedBadge)
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

        holder.tvStatus.text = "Status: ${appt.status}"

        // 🔥 NEW: Attended — green border + badge
        if (appt.status.equals("Attended", ignoreCase = true)) {
            holder.tvAttendedBadge.visibility = View.VISIBLE

            holder.card.strokeWidth = 4
            holder.card.strokeColor =
                ContextCompat.getColor(context, android.R.color.holo_green_dark)

        } else if (appt.isEmergency) {
            // Emergency red border
            holder.tvEmergencyBadge.visibility = View.VISIBLE
            holder.tvAttendedBadge.visibility = View.GONE

            holder.card.strokeWidth = 4
            holder.card.strokeColor =
                ContextCompat.getColor(context, android.R.color.holo_red_dark)

        } else {
            // No border
            holder.tvAttendedBadge.visibility = View.GONE
            holder.tvEmergencyBadge.visibility = View.GONE
            holder.card.strokeWidth = 0
        }

        // Animation
        holder.itemView.alpha = 0f
        holder.itemView.translationY = 40f
        holder.itemView.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(220)
            .setInterpolator(DecelerateInterpolator())
            .start()

        holder.itemView.setOnClickListener {
            showUpdateDialog(holder.itemView.context, appt, position)
        }
    }

    private fun showUpdateDialog(context: Context, appointment: Appointment, position: Int) {
        val dialogView = LayoutInflater.from(context)
            .inflate(R.layout.dialog_update_appointment, null)

        val tvDialogPatient = dialogView.findViewById<TextView>(R.id.tvDialogPatient)
        val spinnerStatus = dialogView.findViewById<Spinner>(R.id.spinnerStatus)
        val checkEmergency = dialogView.findViewById<CheckBox>(R.id.checkEmergency)

        tvDialogPatient.text = "Patient: ${appointment.patientName}"

        val statusOptions = arrayOf("Scheduled", "Cancelled")

        spinnerStatus.adapter = ArrayAdapter(
            context,
            android.R.layout.simple_spinner_dropdown_item,
            statusOptions
        )

        val currentIndex = statusOptions.indexOfFirst {
            it.equals(appointment.status, ignoreCase = true)
        }.coerceAtLeast(0)

        spinnerStatus.setSelection(currentIndex)
        checkEmergency.isChecked = appointment.isEmergency

        AlertDialog.Builder(context)
            .setTitle("Update Appointment")
            .setView(dialogView)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(android.R.string.ok) { _, _ ->

                val newStatus = spinnerStatus.selectedItem.toString()
                val newEmergency = checkEmergency.isChecked

                if (newStatus == "Cancelled") {

                    Toast.makeText(
                        context,
                        "Deleting appointment ${appointment.patientName}",
                        Toast.LENGTH_SHORT
                    ).show()

                    Handler(Looper.getMainLooper()).postDelayed({
                        onDeleteRequested(appointment, position)
                    }, 4000)

                    return@setPositiveButton
                }

                val updated = appointment.copy(
                    status = newStatus,
                    isEmergency = newEmergency
                )

                onUpdateRequested(updated)

            }
            .show()
    }

    fun removeAt(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    fun updateItem(updated: Appointment) {
        val index = items.indexOfFirst { it.id == updated.id }
        if (index != -1) {
            items[index] = updated
            notifyItemChanged(index)
        }
    }

    fun getItemAt(position: Int): Appointment {
        return items[position]
    }

    fun replaceAll(newItems: List<Appointment>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
