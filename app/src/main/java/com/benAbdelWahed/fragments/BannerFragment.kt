package com.benAbdelWahed.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.benAbdelWahed.R
import com.benAbdelWahed.adapters.BannerAdapter
import com.benAbdelWahed.responses.banners_responses.Banner
import com.benAbdelWahed.responses.banners_responses.BannersResponse
import com.benAbdelWahed.responses.haraj_responses.Customer
import com.benAbdelWahed.utils.CallbackRetrofit
import com.benAbdelWahed.utils.RetrofitModel
import com.benAbdelWahed.utils.StaticMembers
import kotlinx.android.synthetic.main.fg_mazads.*
import retrofit2.Call
import retrofit2.Response

class BannerFragment : Fragment() {

    lateinit var customer: Customer
    lateinit var bannerAdapter: BannerAdapter
    lateinit var list: MutableList<Banner>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        list = ArrayList()
        bannerAdapter = BannerAdapter(activity, list)
        recycler.adapter = bannerAdapter
        getCustomerBanners()
        swipe.setOnRefreshListener {
            getCustomerBanners()
        }
    }

    companion object {
        fun getInstance(customer: Customer): BannerFragment {
            val dialog = BannerFragment()
            dialog.customer = customer
            return dialog
        }
    }

    fun getCustomerBanners() {
        swipe.isRefreshing = true
        val call = RetrofitModel.getApi(context).getCustomerBanners(customer.id)
        call.enqueue(object : CallbackRetrofit<BannersResponse>(context) {
            override fun onResponse(call: Call<BannersResponse>, response: Response<BannersResponse>) {
                if (view != null) {
                    errorCard.visibility = View.GONE
                    swipe.isRefreshing = false
                    list.clear()
                    val result = response.body()
                    if (response.isSuccessful && result != null) {
                        list.addAll(result.data)
                        bannerAdapter.notifyDataSetChanged()
                        if (list.isEmpty()) {
                            errorCard.visibility = View.VISIBLE
                            errorMessage.text = getString(R.string.no_banners)
                        }
                    } else {
                        StaticMembers.toastMessageShort(context, R.string.connection_error)
                    }
                }
            }

            override fun onFailure(call: Call<BannersResponse>, t: Throwable) {
                if (view != null) {
                    super.onFailure(call, t)
                    swipe.isRefreshing = false
                    errorCard.visibility = View.GONE
                }
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fg_mazads, container, false)
    }
}
