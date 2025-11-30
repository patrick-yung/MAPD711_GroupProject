package com.mapd711_groupproject

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

class ClinicalListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // We can reuse your partner's 'fragment_view_patients.xml' if it has a recycler view,
        // OR we create a simple layout dynamically to avoid XML errors.
        // For safety, let's use the layout I provide below.
        val view = inflater.inflate(R.layout.fragment_clinical_list, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerClinical)
        val fab = view.findViewById<FloatingActionButton>(R.id.fabAddClinical)
        val tvEmpty = view.findViewById<TextView>(R.id.tvEmpty)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 1. FETCH DATA (Manual/OkHttp Style)
        GlobalScope.launch {
            val list = fetchAllTests()
            withContext(Dispatchers.Main) {
                if (list.isNotEmpty()) {
                    recyclerView.adapter = ClinicalTestAdapter(list)
                    tvEmpty.visibility = View.GONE
                } else {
                    tvEmpty.visibility = View.VISIBLE
                    tvEmpty.text = "No clinical tests found."
                }
            }
        }

        // 2. FAB CLICK -> Go to your 'ViewClinicalTest' (The Add Form)
        fab.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ViewClinicalTest())
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    // Manual Fetch Logic (Matches your partner's style)
    private fun fetchAllTests(): List<ClinicalTestResponse> {
        val resultList = mutableListOf<ClinicalTestResponse>()
        try {
            // Use the endpoint we added in Step 1
            val url = URL("https://mapd713-group-project-2.onrender.com/clinicaldata")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"

            if (conn.responseCode == 200) {
                val response = conn.inputStream.bufferedReader().use { it.readText() }
                val jsonArray = JSONArray(response)

                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    resultList.add(
                        ClinicalTestResponse(
                            _id = obj.optString("_id"),
                            patientId = obj.optString("patientId"),
                            type = obj.optString("type"),
                            value = obj.optString("value"),
                            flagged = obj.optBoolean("flagged"),
                            measuredDateTime = obj.optString("measuredDateTime")
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("ClinicalList", "Error: ${e.message}")
        }
        return resultList
    }
}