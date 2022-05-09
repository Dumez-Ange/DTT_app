package com.main.viewModel.fragments


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.main.model.House
import com.main.databinding.FragmentHomeBinding
import com.main.utils.Constants.API_KEY
import com.main.utils.Constants.TAG
import com.main.viewModel.activities.DetailActivity
import com.main.viewModel.adapter.HouseAdapter
import com.main.utils.RecyclerViewInterface
import com.main.utils.RetrofitInstance
import retrofit2.HttpException
import java.io.IOException
import java.text.DecimalFormat
import java.text.NumberFormat

class HomeFragment : Fragment(), RecyclerViewInterface {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var houseAdapter: HouseAdapter
    private lateinit var listHouses: ArrayList<House>

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient



    @SuppressLint("VisibleForTests")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        fusedLocationProviderClient = FusedLocationProviderClient(requireActivity())

        fetchDataFromApi()
        getLocation()
        setupRecyclerView()
        setupSearchView()



        return binding.root
    }

    // ----------------------
    // DTT API
    // ----------------------

    private fun fetchDataFromApi(){

        lifecycleScope.launchWhenCreated {
            val response = try {
                RetrofitInstance.api.getHouses(API_KEY)
            } catch (e: IOException) {
                Log.e(TAG, "IOException, you might not have internet connection")
                return@launchWhenCreated
            } catch (e: HttpException){
                Log.e(TAG, "HttpException, unexpected response ")
                return@launchWhenCreated
            }

            if (response.isSuccessful && response.body() != null){

                // Cast to arraylist to sort the list by price
                listHouses = (response.body() as ArrayList<House>?)!!

                listHouses.sortWith(compareBy {it.price})

                houseAdapter.houses = listHouses

            }else{
                Log.e(TAG, "Response not successful")
            }
        }
    }

    // ----------------------
    // SEARCH BAR
    // ----------------------


    // Add OnQueryTextListener to the SearchView
    private fun setupSearchView(){
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            //Create a new filteredList based on Query
            override fun onQueryTextChange(newText: String): Boolean {
                filterList(newText)
                return true
            }

        })
    }

    // Handles the creation of the filtered list
    private fun filterList(text: String) {
        val filteredList = arrayListOf<House>()
        var address: String

        // Filter list by address (city name and zip)
        listHouses.forEach { house : House ->
            address = "${house.zip} ${house.city}"
            if (address.contains(text)){
                filteredList.add(house)
            }
        }

        if (filteredList.isEmpty()){
            binding.rvHouses.visibility = View.INVISIBLE
            binding.ivSearchEmpty.visibility = View.VISIBLE
            binding.tvSearchEmpty1.visibility = View.VISIBLE
            binding.tvSearchEmpty2.visibility = View.VISIBLE
        }else{
            houseAdapter.setFilteredList(filteredList)
            binding.rvHouses.visibility = View.VISIBLE
            binding.ivSearchEmpty.visibility = View.INVISIBLE
            binding.tvSearchEmpty1.visibility = View.INVISIBLE
            binding.tvSearchEmpty2.visibility = View.INVISIBLE
        }

    }

    // ----------------------
    // RECYCLER VIEW
    // ----------------------

    // Setup the RecyclerView with the HouseAdapter
    private fun setupRecyclerView() = binding.rvHouses.apply {
        houseAdapter = HouseAdapter(this@HomeFragment)
        adapter = houseAdapter
        layoutManager = LinearLayoutManager(context)
    }

    // Called when user click on House item - Pass properties from the chosen house to DetailActivity
    override fun onItemClick(position: Int) {
        val i = Intent(this.context, DetailActivity::class.java)
        val formatter : NumberFormat = DecimalFormat("#.##km")

        if (houseAdapter.locationEnable){
            houseAdapter.houseLocation.latitude = houseAdapter.houses[position].latitude.toDouble()
            houseAdapter.houseLocation.longitude = houseAdapter.houses[position].longitude.toDouble()
            val dist = formatter.format(houseAdapter.userLocation.distanceTo(houseAdapter.houseLocation) / 1000)
            i.putExtra("DIST", dist)
        }

        i.putExtra("PRICE", houseAdapter.houses[position].price)
        i.putExtra("IMAGE_URL", houseAdapter.houses[position].image)
        i.putExtra("BEDS", houseAdapter.houses[position].bedrooms)
        i.putExtra("BATH", houseAdapter.houses[position].bathrooms)
        i.putExtra("SIZE", houseAdapter.houses[position].size)
        i.putExtra("LAT", houseAdapter.houses[position].latitude)
        i.putExtra("LONG", houseAdapter.houses[position].longitude)
        i.putExtra("DESC", houseAdapter.houses[position].description)

        startActivity(i)
    }

   // ----------------------
   // LOCATION PERMISSIONS
   // ----------------------

    // Check if app has location permissions
    private fun checkPermissions():Boolean {
       if(ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
               ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
           return true
       }
       return false
    }

    // Check if location is enabled on device
    private fun isLocationEnabled(): Boolean {
       val locationManager: LocationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
       return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    // Get the user location on app start
    @SuppressLint("MissingPermission")
    private fun getLocation() {
        if(checkPermissions()) {
            if (isLocationEnabled()){
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(requireActivity()){ task ->

                    houseAdapter.userLocation = task.result
                    houseAdapter.locationEnable = true


                }
            }


        }
    }


}