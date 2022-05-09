package com.main.viewModel.activities


import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.main.R
import com.main.databinding.ActivityMainBinding
import com.main.utils.Constants
import com.main.utils.LocationUtility
import com.main.viewModel.adapter.ViewPagerAdapter
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions


class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewPager: ViewPager2

    lateinit var navigationView: BottomNavigationView


    // ----------------------
    // CALLBACKS
    // ----------------------

    // Creating ViewPager Callback here to unregister it in the onDestroy methode
    private val viewPagerOnPageChangeCallBack = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            when(position){
                0 -> navigationView.menu.findItem(R.id.nav_home).isChecked = true
                1 -> navigationView.menu.findItem(R.id.nav_info).isChecked = true
            }
        }
    }

    // ----------------------
    // ACTIVITY OVERRIDDEN METHODS
    // ----------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen().apply {}

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root

        requestPermissions()

        setupViewPager()
        setupNavigationBar()

        setContentView(view)
    }


    override fun onDestroy() {
        super.onDestroy()
        // Unregister the viewPager callback to avoid memory leak
        viewPager.unregisterOnPageChangeCallback(viewPagerOnPageChangeCallBack)
    }

    // ----------------------
    // VIEW PAGER & NAVIGATION BAR
    // ----------------------

    private fun setupViewPager(){
        viewPager = binding.viewPager
        val adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        viewPager.adapter = adapter
        viewPager.registerOnPageChangeCallback(viewPagerOnPageChangeCallBack)
    }

    private fun setupNavigationBar(){
        navigationView = binding.bottomNavigation

        navigationView.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.nav_home -> viewPager.currentItem = 0
                R.id.nav_info -> viewPager.currentItem = 1
            }
            true
        }
    }

    // ----------------------
    // PERMISSIONS (using EasyPermissions)
    // ----------------------

    private fun requestPermissions(){
        if(LocationUtility.hasLocationPermissions(this)){
            return
        }
        EasyPermissions.requestPermissions(
            this,
            "You need to accept location permissions to use this app.",
            Constants.REQUEST_CODE_LOCATION_PERMISSION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {

        finish()
        overridePendingTransition(0, 0)
        startActivity(intent)
        overridePendingTransition(0, 0)

    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)){
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermissions()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)

    }
}