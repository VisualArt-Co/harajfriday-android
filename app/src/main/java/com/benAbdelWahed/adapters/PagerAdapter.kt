package com.benAbdelWahed.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import java.util.*

class PagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    private val fragments: MutableList<Fragment>
    private val titles: MutableList<String>
    fun addFragment(fragment: Fragment, title: String) {
        titles.add(title)
        fragments.add(fragment)
    }

    fun getTitle(i: Int):String{
        return titles[i]
    }

    fun getItem(i:Int): Fragment {
        return fragments[i]
    }

    fun addFragment(fragment: Fragment) {
        titles.add("")
        fragments.add(fragment)
    }
    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    override fun getItemCount(): Int {
        return fragments.size
    }

    init {
        fragments = ArrayList()
        titles = ArrayList()
    }
}