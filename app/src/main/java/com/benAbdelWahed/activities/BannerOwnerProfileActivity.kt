package com.benAbdelWahed.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import com.benAbdelWahed.R
import com.benAbdelWahed.adapters.PagerAdapter
import com.benAbdelWahed.fragments.BannerFragment
import com.benAbdelWahed.fragments.ReviewsDialog
import com.benAbdelWahed.fragments.ReviewsFragment
import com.benAbdelWahed.responses.customer_response.ProfileResponse
import com.benAbdelWahed.responses.error_response.ErrorLoginResponse
import com.benAbdelWahed.responses.haraj_responses.Customer
import com.benAbdelWahed.utils.CallbackRetrofit
import com.benAbdelWahed.utils.PrefManager
import com.benAbdelWahed.utils.RetrofitModel
import com.benAbdelWahed.utils.StaticMembers
import com.benAbdelWahed.utils.StaticMembers.CUSTOMER
import com.benAbdelWahed.utils.StaticMembers.showLoginDialog
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_banner_owner_profile.*
import kotlinx.android.synthetic.main.activity_banner_owner_profile.tabLayout
import kotlinx.android.synthetic.main.activity_haraj_details.toolbar
import kotlinx.android.synthetic.main.progress_layout.*
import retrofit2.Call
import retrofit2.Response
import java.util.*

class BannerOwnerProfileActivity : AppCompatActivity() {

    private lateinit var pagerAdapter: PagerAdapter
    private lateinit var customer: Customer
    var myUserProfile: Customer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_banner_owner_profile)

        customer = intent.getSerializableExtra(CUSTOMER) as Customer
        myUserProfile = PrefManager.getInstance(this).getObject(StaticMembers.PROFILE, Customer::class.java) as Customer?

        toolbar.title = customer.user_name
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        rateSeller.setOnClickListener {
            ReviewsDialog.getInstance(customer, object : ReviewsDialog.OnActionListener {
                override fun onConfirm() {
                    getProfile()
                }
            }).show(supportFragmentManager, "ReviewsDialog")
        }
        updateUI()
        getProfile()
    }

    private fun updateUI() {
        userName.text = customer.user_name
        verifyImage.visibility = if (customer.isPremium) VISIBLE else View.GONE
        if (customer.image != null && customer.image!!.isNotEmpty())
            Glide.with(this).load(customer.image).centerCrop().placeholder(R.drawable.place_holder_logo).into(userImage)
        else
            userImage.setImageResource(R.drawable.place_holder_logo)

        rating.rating = customer.averageRate.toFloat()
        noOfRates.text = String.format(Locale.getDefault(), getString(R.string._f_5_rates), customer.averageRate.toFloat(), customer.allRates.size)

        if (myUserProfile != null)
            if (myUserProfile!!.id != customer.id)
                message.visibility = VISIBLE

        message.setOnClickListener {
            if (myUserProfile != null) {
                val intent = Intent(this, ChatRoomActivity::class.java)
                intent.putExtra(StaticMembers.OTHER_ID, customer.id)
                startActivity(intent)
            } else showLoginDialog(this)
        }

        pagerAdapter = PagerAdapter(this)
        pagerAdapter.addFragment(BannerFragment.getInstance(customer), getString(R.string.banners_and_ads))
        pagerAdapter.addFragment(ReviewsFragment.getInstance(customer), getString(R.string.reviews))
        viewPager.adapter = pagerAdapter
        pagerAdapter.notifyDataSetChanged()
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = pagerAdapter.getTitle(position)
        }.attach()
    }

    private fun getProfile() {
        progress.visibility = VISIBLE
        val call = RetrofitModel.getApi(this).getUserDetailsById(customer.id)
        call.enqueue(object : CallbackRetrofit<ProfileResponse>(this) {
            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                progress.visibility = View.GONE
                val result = response.body()
                if (response.isSuccessful && result != null) {
                    customer = result.data
                    updateUI()
                } else {
                    try {
                        var errorLoginResponse: ErrorLoginResponse? = null
                        if (response.errorBody() != null) {
                            errorLoginResponse = GsonBuilder().create().fromJson<Any>(response.errorBody()!!.string(), ErrorLoginResponse::class.java) as ErrorLoginResponse?
                            if (errorLoginResponse != null) {
                                StaticMembers.toastMessageShort(baseContext, errorLoginResponse.message)
                                if (errorLoginResponse.data.password != null && errorLoginResponse.data.password.isNotEmpty()) {
                                    StaticMembers.toastMessageShort(baseContext, errorLoginResponse.data.password[0])
                                }
                                if (errorLoginResponse.data.phone != null && errorLoginResponse.data.phone.isNotEmpty()) {
                                    StaticMembers.toastMessageShort(baseContext, errorLoginResponse.data.phone[0])
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        StaticMembers.toastMessageShort(baseContext, R.string.connection_error)
                    }
                    finish()
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                super.onFailure(call, t)
                progress.visibility = View.GONE
                finish()
            }
        })
    }


}