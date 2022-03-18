package com.example.ebook.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import com.example.ebook.MainActivity
import com.example.ebook.fragments.Home.HomeFragment
import com.example.ebook.fragments.LogFragment
import com.example.ebook.fragments.Read.ReadFragment
import com.example.ebook.fragments.SignupFragment


class ViewPagerAdapterSignIn(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> LogFragment()
            1 -> SignupFragment()

            else -> Fragment()
        }
    }

    override fun onBindViewHolder(
        holder: FragmentViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        super.onBindViewHolder(holder, position, payloads)

    }


}