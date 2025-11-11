package com.mapd711_groupproject

import android.os.Bundle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.mapd711_groupproject.databinding.ActivityMapsBinding

class MapsActivity : BaseActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    data class Clinic(
        val name: String,
        val latitude: Double,
        val longitude: Double,
        val description: String
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupDrawer(R.id.nav_map)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val clinics = listOf(
            Clinic("Toronto General Hospital", 43.6592, -79.3888, "University Health Network"),
            Clinic("Mount Sinai Hospital", 43.6568, -79.3925, "Academic medical center"),
            Clinic("St. Michael's Hospital", 43.6546, -79.3805, "Downtown teaching hospital"),
        )
        clinics.forEach { clinic ->
            val location = LatLng(clinic.latitude, clinic.longitude)
            mMap.addMarker(MarkerOptions().position(location).title(clinic.name).snippet(clinic.description))
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(43.6592, -79.3888), 15f))
    }
}
