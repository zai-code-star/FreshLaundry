package com.example.freshlaundry.admin.kurir

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class KurirPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    private val fragments = listOf(
        PenjemputanFragment(),
        PengantaranFragment()
    )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]
}
