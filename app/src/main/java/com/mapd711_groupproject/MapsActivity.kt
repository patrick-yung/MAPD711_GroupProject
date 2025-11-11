package com.mapd711_groupproject

import android.os.Bundle
<<<<<<< HEAD
=======
import androidx.appcompat.app.AppCompatActivity
>>>>>>> 9fbd727a9c42aef0b94fdce1dc2f347d6bfdb20b
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.mapd711_groupproject.databinding.ActivityMapsBinding

<<<<<<< HEAD
class MapsActivity : BaseActivity(), OnMapReadyCallback {
=======
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
>>>>>>> 9fbd727a9c42aef0b94fdce1dc2f347d6bfdb20b

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
<<<<<<< HEAD
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupDrawer(R.id.nav_map)

=======

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
>>>>>>> 9fbd727a9c42aef0b94fdce1dc2f347d6bfdb20b
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

<<<<<<< HEAD
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
=======
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

>>>>>>> 9fbd727a9c42aef0b94fdce1dc2f347d6bfdb20b
        val clinics = listOf(
            Clinic("Toronto General Hospital", 43.6592, -79.3888, "University Health Network"),
            Clinic("Mount Sinai Hospital", 43.6568, -79.3925, "Academic medical center"),
            Clinic("St. Michael's Hospital", 43.6546, -79.3805, "Downtown teaching hospital"),
        )
<<<<<<< HEAD
        clinics.forEach { clinic ->
            val location = LatLng(clinic.latitude, clinic.longitude)
            mMap.addMarker(MarkerOptions().position(location).title(clinic.name).snippet(clinic.description))
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(43.6592, -79.3888), 15f))
    }
}
=======

        // Add markers for all clinics
        clinics.forEach { clinic ->
            val location = LatLng(clinic.latitude, clinic.longitude)
            mMap.addMarker(
                MarkerOptions()
                    .position(location)
                    .title(clinic.name)
                    .snippet(clinic.description)
            )
            }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(43.6592, -79.3888), 15f))
    }
}
>>>>>>> 9fbd727a9c42aef0b94fdce1dc2f347d6bfdb20b
