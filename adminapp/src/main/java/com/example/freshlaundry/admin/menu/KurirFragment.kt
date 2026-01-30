package com.example.freshlaundry.admin.menu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.freshlaundry.admin.R
import com.example.freshlaundry.admin.kurir.KurirPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class KurirFragment : Fragment() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_kurir, container, false)

        tabLayout = view.findViewById(R.id.tabLayoutKurir)
        viewPager = view.findViewById(R.id.viewPagerKurir)

        val adapter = KurirPagerAdapter(this)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Penjemputan"
                1 -> "Pengantaran"
                else -> ""
            }
        }.attach()

        return view
    }
}
