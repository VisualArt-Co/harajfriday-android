package com.benAbdelWahed.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.benAbdelWahed.R
import com.benAbdelWahed.adapters.PagerAdapter
import com.benAbdelWahed.fragments.MazadsFragment
import com.benAbdelWahed.fragments.SubscribePremiumDialog
import com.benAbdelWahed.ui.membership.MembershipActivity
import com.benAbdelWahed.utils.PrefManager
import com.benAbdelWahed.utils.ProfileUtil
import com.benAbdelWahed.utils.StaticMembers
import com.benAbdelWahed.utils.StaticMembers.*
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_bids.*

class MazadsActivity : AppCompatActivity() {

    private var selectedPosition: Int = 0
    private lateinit var pagerAdapter: PagerAdapter
    private lateinit var prefManager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bids)
        setSupportActionBar(toolbar)
        prefManager = PrefManager.getInstance(this)
        ProfileUtil.getInstance(baseContext).getProfile()
        if (savedInstanceState != null)
            selectedPosition = savedInstanceState.getInt(PAGE)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        if (prefManager.hasToken()) {
            haraj.visibility = VISIBLE
            if (!prefManager.getBoolean(IS_PREMIUM)) {
                subscribe2.visibility = VISIBLE
                haraj.visibility = GONE
            }
        }
        haraj.setOnClickListener {
            startActivity(Intent(this, HarajActivity::class.java))
        }
        subscribe2.setOnClickListener {
            startActivity(Intent(this, MembershipActivity::class.java))
        }
        pagerAdapter = PagerAdapter(this)
        pagerAdapter.addFragment(MazadsFragment.getInstance(0), getString(R.string.current_mazad))
        pagerAdapter.addFragment(MazadsFragment.getInstance(1), getString(R.string.next_bid))
        pagerAdapter.addFragment(MazadsFragment.getInstance(2), getString(R.string.expired_bid))
        pagerAdapter.notifyDataSetChanged()
        pager.adapter = pagerAdapter

        TabLayoutMediator(tabLayout, pager) { tab, position ->
            tab.text = pagerAdapter.getTitle(position)
        }.attach()

        pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                selectedPosition = position
            }
        })
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(PAGE, selectedPosition)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == EDIT_CODE || requestCode == EDIT_PROFILE_CODE) {
                (pagerAdapter.getItem(selectedPosition) as MazadsFragment).getBids()
                pagerAdapter.notifyDataSetChanged()
                pager.setCurrentItem(selectedPosition,false)
            }
        }
    }
}
