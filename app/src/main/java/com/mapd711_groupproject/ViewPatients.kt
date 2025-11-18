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

// This ViewModel handles fetching the data. The Fragment will observe it.
// This ViewModel handles fetching the data. The Fragment will observe it.
class PatientsViewModel : ViewModel() {

    // This LiveData will hold the list of patients from the PatientService.
    private val _patientList = MutableLiveData<List<PatientService.Patient>>()
    val patientList: LiveData<List<PatientService.Patient>> get() = _patientList

    init {
        // Fetch patients as soon as the ViewModel is created.
        fetchPatients()
    }

    private fun fetchPatients() {
        // Keeping GlobalScope as requested for data fetching.
        // We can launch on the Main dispatcher and let the suspend function handle the background thread.
        GlobalScope.launch {
            try {
                // FIX: Call the suspend function directly.
                // PatientService.fetchPatient() will handle switching to the IO thread itself.
                val patients = PatientService.fetchPatients()

                // Use .postValue() to safely update the LiveData from this coroutine.
                // If 'patients' is null, post an empty list to avoid errors.
                _patientList.postValue(patients ?: emptyList())

            } catch (e: Exception) {
                Log.e("PatientsViewModel", "Error fetching patients", e)
                // Post an empty list if an error occurs.
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

        // Set up the RecyclerView's basic properties.
        setupRecyclerView()

        // Observe the list of patients from the ViewModel.
        // This block will run on the main thread automatically whenever the data changes.
        viewModel.patientList.observe(viewLifecycleOwner) { patients ->
            // When the data changes, create a new adapter with the new list
            // and attach it to the RecyclerView.
            Log.d("ViewPatients", "Patient list updated with ${patients.size} items.")
            binding.patientsRecyclerView.adapter = PatientAdapter(patients)
        }
    }

    private fun setupRecyclerView() {
        binding.patientsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            // Add a divider line between items for better visuals.
            addItemDecoration(DividerItemDecoration(context, (layoutManager as LinearLayoutManager).orientation))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clean up the binding reference to avoid memory leaks when the view is destroyed.
        _binding = null
    }
}
