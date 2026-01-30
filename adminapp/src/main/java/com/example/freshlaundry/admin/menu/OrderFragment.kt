package com.example.freshlaundry.admin.menu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.example.freshlaundry.admin.R
import com.example.freshlaundry.admin.tab.OrderPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class OrderFragment : Fragment(R.layout.fragment_order) {
        private lateinit var viewPager: ViewPager2
        private lateinit var tabLayout: TabLayout

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            tabLayout = view.findViewById(R.id.tab_layout)
            viewPager = view.findViewById(R.id.view_pager)

            val adapter = OrderPagerAdapter(this)
            viewPager.adapter = adapter

            val tabTitles = listOf("Masuk", "Diproses", "Selesai", "Dibatalkan")
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = tabTitles[position]
            }.attach()
        }
    }
