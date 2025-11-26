package com.mapd711_groupproject

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.mapd711_groupproject.databinding.FragmentViewPatientsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PatientsViewModel : ViewModel() {
    private val _patientList = MutableLiveData<List<PatientService.Patient>>()
    val patientList: LiveData<List<PatientService.Patient>> get() = _patientList

    init {
        fetchPatients()
    }

    private fun fetchPatients() {
        GlobalScope.launch {
            try {
                val patients = PatientService.fetchPatients()
                _patientList.postValue(patients ?: emptyList())

            } catch (e: Exception) {
                Log.e("PatientsViewModel", "Error fetching patients", e)
                _patientList.postValue(emptyList())
            }
        }
    }
}


// This is the Fragment that displays the UI.
class ViewPatients : Fragment() {

    private var _binding: FragmentViewPatientsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: PatientsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using ViewBinding.
        _binding = FragmentViewPatientsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the ViewModel.
        viewModel = ViewModelProvider(this).get(PatientsViewModel::class.java)
        setupRecyclerView()

        viewModel.patientList.observe(viewLifecycleOwner) { patients ->
            Log.d("ViewPatients", "Patient list updated with ${patients.size} items.")
            binding.patientsRecyclerView.adapter = PatientAdapter(patients)
        }
    }

    private fun setupRecyclerView() {
        binding.patientsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, (layoutManager as LinearLayoutManager).orientation))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
