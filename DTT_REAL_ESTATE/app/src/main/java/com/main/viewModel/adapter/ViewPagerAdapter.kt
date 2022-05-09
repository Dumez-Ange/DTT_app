package com.main.viewModel.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.main.utils.Constants.NUM_TABS
import com.main.viewModel.fragments.HomeFragment
import com.main.viewModel.fragments.InformationFragment


class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle){

    override fun getItemCount(): Int {
        return NUM_TABS
    }

    // Create fragments dynamically
    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return HomeFragment()
            1 -> return InformationFragment()
        }
        return HomeFragment()
    }
}