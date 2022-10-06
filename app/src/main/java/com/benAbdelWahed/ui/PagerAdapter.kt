package com.benAbdelWahed.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class PagerAdapter(activty: FragmentActivity) :
        FragmentStateAdapter(activty) {
    public val FRAGMENT_INDEX = "index"
    val fragmentList = ArrayList<Fragment>()
    fun addFragment(fragment: Fragment, hasNoArgs: Boolean = true) {
        val args = if (hasNoArgs) Bundle() else fragment.arguments
        addFragment(fragment, args)
    }

    private fun addFragment(fragment: Fragment, args: Bundle?) {
        args?.putInt(FRAGMENT_INDEX, fragmentList.size)
        fragment.arguments = args
        fragmentList.add(fragment)
    }

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }
}