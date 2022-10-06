package com.benAbdelWahed.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.benAbdelWahed.R
import com.benAbdelWahed.adapters.PagerAdapter
import com.benAbdelWahed.fragments.HarajDetailsFragment
import com.benAbdelWahed.responses.haraj_responses.HarajProductsResponse
import com.benAbdelWahed.utils.PrefManager
import com.benAbdelWahed.utils.StaticMembers
import com.benAbdelWahed.utils.StaticMembers.HARAJ_ID
import com.benAbdelWahed.utils.StaticMembers.HARAJ_RESPONSE
import kotlinx.android.synthetic.main.activity_haraj_details.*
import java.util.*

class HarajDetailsActivity : AppCompatActivity() {

    private lateinit var pagerAdapter: PagerAdapter
    private var harajProductsResponse: HarajProductsResponse? = null
    var selectedProductID = ""
    private lateinit var prefManager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_haraj_details)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        prefManager = PrefManager(this)
        harajProductsResponse = prefManager.getObject(HARAJ_RESPONSE, HarajProductsResponse::class.java) as HarajProductsResponse?

        shareImageView.setOnClickListener {
            val shareBody = String.format(Locale.US, "%s%s", StaticMembers.getBaseURL(), selectedProductID)
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name)
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
            startActivity(
                    Intent.createChooser(
                            sharingIntent,
                            resources.getString(R.string.share_using)
                    )
            )
        }
        pagerAdapter = PagerAdapter(this)
        if (harajProductsResponse != null && harajProductsResponse!!.data != null) {
            for (i in harajProductsResponse!!.data) {
                pagerAdapter.addFragment(HarajDetailsFragment.newInstance(i))
            }
            selectedProductID = harajProductsResponse!!.data[intent.getIntExtra(StaticMembers.POSITION, 0)].id.toString()
            prefManager.removeKey(HARAJ_RESPONSE)
        } else {
            if (intent.getStringExtra(HARAJ_ID) == null) {
                val uri = intent.data
                if (uri != null) {
                    selectedProductID = uri.pathSegments.last() as String
                }
            } else {
                selectedProductID = intent.getStringExtra(HARAJ_ID)!!
            }
            pagerAdapter.addFragment(HarajDetailsFragment.newInstance(selectedProductID))
        }
        pager.adapter = pagerAdapter
        pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val id = (pagerAdapter.getItem(position) as HarajDetailsFragment).productId
                if (id.isNotEmpty())
                    selectedProductID = id
            }
        })
        pager.setCurrentItem(intent.getIntExtra(StaticMembers.POSITION, 0),false)
        pager.offscreenPageLimit = 2

        pagerAdapter.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK)
            if (requestCode == StaticMembers.EDIT_CODE)
                setResult(Activity.RESULT_OK)
    }
}
