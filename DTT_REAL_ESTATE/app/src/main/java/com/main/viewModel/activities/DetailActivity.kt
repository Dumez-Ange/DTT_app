package com.main.viewModel.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.main.databinding.ActivityDetailBinding
import com.main.utils.Constants.BASE_URL
import com.main.viewModel.fragments.HouseDetailFragment
import java.text.DecimalFormat
import java.text.NumberFormat

class DetailActivity : AppCompatActivity(){

    private var detailFragment: HouseDetailFragment? = null
    private val formatter : NumberFormat = DecimalFormat("$#,###")
    private lateinit var binding: ActivityDetailBinding

    // ----------------------
    // ACTIVITY OVERRIDDEN METHODS
    // ----------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)
        configureAndShowDetailFragment()

    }

    override fun onResume() {
        super.onResume()
        // Call updateDetailFragment here because we are sure the activity is visible
        updateDetailFragment()

    }

    // ----------------------
    // DETAIL FRAGMENT (STATIC)
    // ----------------------

    // Create HouseDetailFragment and its arguments
    private fun configureAndShowDetailFragment(){
        val latitude = intent.getIntExtra("LAT", 0)
        val longitude = intent.getIntExtra("LONG", 0)

        val bundle = Bundle()
        bundle.putInt("LAT", latitude)
        bundle.putInt("LONG", longitude)

        detailFragment = supportFragmentManager.findFragmentById(binding.frameLayoutDetail.id) as? HouseDetailFragment

        if (detailFragment == null){
            detailFragment = HouseDetailFragment()
            supportFragmentManager.beginTransaction()
                .add(binding.frameLayoutDetail.id, detailFragment!!)
                .commit()
        }

        detailFragment!!.arguments = bundle

    }

    // Bind HouseDetailFragment components with house properties
    private fun updateDetailFragment(){

        val imageURL = BASE_URL + intent.getStringExtra("IMAGE_URL")

        detailFragment!!.binding.tvPrice.text = formatter.format(intent.getIntExtra("PRICE", 0))
        detailFragment!!.binding.tvBed.text = intent.getIntExtra("BEDS", 0).toString()
        detailFragment!!.binding.tvBath.text = intent.getIntExtra("BATH", 0).toString()
        detailFragment!!.binding.tvLayers.text = intent.getIntExtra("SIZE", 0).toString()
        detailFragment!!.binding.tvDescriptionCorpus.text = intent.getStringExtra("DESC")
        detailFragment!!.binding.tvLocation.text = intent.getStringExtra("DIST")

        // Use Glide to dynamically attribute image to Image View
        Glide.with(detailFragment!!.binding.root.context).load(imageURL).centerCrop().into(detailFragment!!.binding.ivHouse)

        detailFragment!!.binding.ivTopBtn.setOnClickListener {
            this@DetailActivity.finish()
        }
    }

}