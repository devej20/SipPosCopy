package net.sipconsult.jubensippos.ui.products

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import net.sipconsult.jubensippos.ui.home.HomeFragment

class ProductsTabsPagerAdapter (fm: FragmentManager): FragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                HomeFragment()
            }
            else -> HomeFragment()
        }
    }

    override fun getCount(): Int  = 3

}