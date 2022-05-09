package com.main.viewModel.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.main.R
import com.main.databinding.FragmentHouseDetailBinding
import kotlin.properties.Delegates


class HouseDetailFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentHouseDetailBinding? = null
    val binding get() = _binding!!

    private var latitude by Delegates.notNull<Double>()
    private var longitude by Delegates.notNull<Double>()

    private var mMap: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHouseDetailBinding.inflate(inflater, container, false)

        if(mMap == null){
            val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this)
        }

        // Get marker's coordinates
        latitude = arguments?.getInt("LAT", 0)?.toDouble()!!
        longitude = arguments?.getInt("LONG", 0)?.toDouble()!!

        return binding.root
    }

    // ----------------------
    // MAP
    // ----------------------

    // Setup the map
    override fun onMapReady(googleMap: GoogleMap) {
        val houseAddress = LatLng(latitude, longitude)

        googleMap.addMarker(
            MarkerOptions()
                .position(houseAddress)
                .title("Property")
        )

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(houseAddress, 12f))

        // Deactivate page scrolling when camera is moving
        googleMap.setOnCameraMoveStartedListener {
            binding.scrollView.requestDisallowInterceptTouchEvent(true)
        }


    }

}