package com.mapd711_groupproject

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.mapd711_groupproject.databinding.FragmentViewPatientsBinding

class ViewPatients : Fragment() {
    private var _binding: FragmentViewPatientsBinding? = null
    private val binding get() = _binding!!

    private var model: Patients? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViewPatientsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model = ViewModelProvider(this).get(Patients::class.java)

        // Set up the RecyclerView's basic properties
        setupRecyclerView()

        // Observe the list of patients from the ViewModel
        model?.patientList?.observe(viewLifecycleOwner) { patientList ->
            // When the list is updated, create a new adapter and set it on the RecyclerView
            if (patientList.isNotEmpty()) {
                binding.patientsRecyclerView.adapter = PatientAdapter(patientList)
            }
        }
    }

    private fun setupRecyclerView() {
        binding.patientsRecyclerView.apply {
            // Use a vertical LinearLayoutManager
            layoutManager = LinearLayoutManager(context)
            // Optional: Add a divider line between items for better visual separation
            addItemDecoration(DividerItemDecoration(context, (layoutManager as LinearLayoutManager).orientation))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clean up the binding reference to avoid memory leaks
        _binding = null
    }
}
