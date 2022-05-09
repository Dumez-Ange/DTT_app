package com.main.viewModel.adapter

import android.location.Location
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.main.model.House
import com.main.databinding.ItemHouseBinding
import com.main.utils.Constants.BASE_URL
import com.main.utils.RecyclerViewInterface
import java.text.DecimalFormat
import java.text.NumberFormat


class HouseAdapter(private var recyclerViewInterface: RecyclerViewInterface) : RecyclerView.Adapter<HouseAdapter.HouseViewHolder>() {

    lateinit var userLocation: Location
    var houseLocation: Location = Location("House")
    var locationEnable: Boolean = false

    // ----------------------
    // VIEW HOLDER
    // ----------------------

    inner class HouseViewHolder(val binding: ItemHouseBinding) : RecyclerView.ViewHolder(binding.root){

        private val moneyFormatter : NumberFormat = DecimalFormat("$#,###")
        private val distanceFormatter : NumberFormat = DecimalFormat("#.##km")

        fun bind(house: House) {

            // <1> Calculates distance from user
            houseLocation.latitude = house.latitude.toDouble()
            houseLocation.longitude = house.longitude.toDouble()

            if(locationEnable){
                binding.tvLocation.text = distanceFormatter.format(userLocation.distanceTo(houseLocation) / 1000)
            }
            // </1>

            // Bind item's components with house properties
            binding.tvPrice.text = moneyFormatter.format(house.price)
            "${house.zip} ${house.city}".also { binding.tvAddress.text = it }
            binding.tvBed.text = house.bedrooms.toString()
            binding.tvBath.text = house.bathrooms.toString()
            binding.tvLayers.text = house.size.toString()
            val houseImageUrl : String = BASE_URL + house.image

            // Use Glide to dynamically attribute image to Image View
            Glide.with(binding.root.context).load(houseImageUrl).centerCrop().into(binding.ivHouse)

        }


    }

    // ----------------------
    // ASYNC LIST DIFFER
    // ----------------------

    private val diffCallBack = object : DiffUtil.ItemCallback<House>(){

        // Call to check whether two items represent the same item
        override fun areItemsTheSame(oldItem: House, newItem: House): Boolean {
            return oldItem.id == newItem.id
        }

        // Call to check whether two items have the same data
        override fun areContentsTheSame(oldItem: House, newItem: House): Boolean {
            return oldItem == newItem
        }

    }

    private val differ = AsyncListDiffer(this, diffCallBack)

    var houses: List<House>
        get() = differ.currentList
        set(value) {differ.submitList(value)}

    // ----------------------
    // ADAPTER OVERRIDDEN FUNCTIONS
    // ----------------------

    override fun getItemCount(): Int {
        return houses.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HouseViewHolder {
        return HouseViewHolder(ItemHouseBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }


    override fun onBindViewHolder(holder: HouseViewHolder, position: Int) {

        holder.bind(houses[position])

        holder.binding.vBody.setOnClickListener {
            recyclerViewInterface.onItemClick(holder.adapterPosition)
        }
    }

    // ----------------------
    // FILTER LIST RESULT
    // ----------------------

    fun setFilteredList(filteredList: List<House>){
        this.houses = filteredList

    }


}