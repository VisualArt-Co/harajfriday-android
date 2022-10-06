package com.benAbdelWahed.ui.deals

import com.benAbdelWahed.R
import com.benAbdelWahed.ui.PagerAdapter
import com.benAbdelWahed.utils.CustomViewModel
import com.benAbdelWahed.utils.PrefManager

class DealsViewModel : CustomViewModel() {
    val titles = arrayListOf(R.string.current_deals,R.string.finished_deals)
    lateinit var pagerAdapter: PagerAdapter
    lateinit var prefManager: PrefManager
    fun init(prefManager: PrefManager, pagerAdapter: PagerAdapter) {
        this.prefManager = prefManager
        this.pagerAdapter = pagerAdapter
        pagerAdapter.addFragment(AllDealsFragment.getInstance(),false)
    }

}