package com.benAbdelWahed.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.benAbdelWahed.R
import com.benAbdelWahed.adapters.BannerAdapter
import com.benAbdelWahed.adapters.MazadAdapter
import com.benAbdelWahed.responses.banners_responses.Banner
import com.benAbdelWahed.responses.banners_responses.BannersResponse
import com.benAbdelWahed.responses.mazads_response.AllMazadResponse
import com.benAbdelWahed.responses.mazads_response.DataItem
import com.benAbdelWahed.ui.deals.DealsActivity
import com.benAbdelWahed.utils.CallbackRetrofit
import com.benAbdelWahed.utils.RetrofitModel
import com.benAbdelWahed.utils.StaticMembers
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.progress_layout.*
import retrofit2.Call
import retrofit2.Response
import java.util.*

class MainFragment : Fragment() {

    private lateinit var currentAdapter: MazadAdapter
    private lateinit var bannerAdapter: BannerAdapter
    private lateinit var bidCurrentList: ArrayList<DataItem>
    private lateinit var nextAdapter: MazadAdapter
    private lateinit var bidNextList: ArrayList<DataItem>
    private lateinit var bannerList: ArrayList<Banner>
    private lateinit var ref: DatabaseReference
    private lateinit var valueEventListener: ValueEventListener
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bannerList = ArrayList()
        bannerAdapter = BannerAdapter(requireActivity(), bannerList)

        bidCurrentList = ArrayList()
        currentAdapter = MazadAdapter(requireActivity(), progress, bidCurrentList, 0, object : MazadAdapter.OnActionListener {
            override fun onRefresh() {
                getBids(0, bidCurrentList, currentAdapter)
            }
        })

        bidNextList = ArrayList()
        nextAdapter = MazadAdapter(requireActivity(), progress, bidNextList, 1, object : MazadAdapter.OnActionListener {
            override fun onRefresh() {
                getBids(1, bidNextList, nextAdapter)
            }
        })
        dealsCard.setOnClickListener {
            startActivity(Intent(context,DealsActivity::class.java))
        }
        recyclerBanner.adapter = bannerAdapter
        recyclerBidsNext.adapter = nextAdapter
        recyclerBidsNow.adapter = currentAdapter
        ref = FirebaseDatabase.getInstance().getReference(StaticMembers.MAZADS)
        getBanners()
        valueEventListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.e("network", "onCancelled")
                StaticMembers.toastMessageShort(requireActivity(), R.string.connection_error)
            }

            override fun onDataChange(p0: DataSnapshot) {
                getBids(1, bidNextList, nextAdapter)
                getBids(0, bidCurrentList, currentAdapter)
            }
        }
        ref.addValueEventListener(valueEventListener)
    }

    override fun onPause() {
        super.onPause()
        currentAdapter.changeAllCountDownTimers(true)
        nextAdapter.changeAllCountDownTimers(true)
    }

    override fun onResume() {
        super.onResume()
        currentAdapter.changeAllCountDownTimers(false)
        nextAdapter.changeAllCountDownTimers(false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    companion object {
        val instance: MainFragment
            get() = MainFragment()
    }

    fun getBids(type: Int, list: MutableList<DataItem>, adapter: MazadAdapter) {
        if (context != null && view != null) {
            val call = RetrofitModel.getApi(context).getAllMazads(StaticMembers.mazadTypes[type])
            call.enqueue(object : CallbackRetrofit<AllMazadResponse>(context) {
                override fun onResponse(call: Call<AllMazadResponse>, response: Response<AllMazadResponse>) {
                    if (context != null) {
                        list.clear()
                        val result = response.body()
                        if (response.isSuccessful && result != null) {
                            list.addAll(result.data)
                            adapter.updateAdapter()
                            when (type) {
                                0 -> {
                                    nowBidLayout.visibility = if (list.isEmpty()) GONE else VISIBLE
                                }
                                1 -> {
                                    nextBidLayout.visibility = if (list.isEmpty()) GONE else VISIBLE
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<AllMazadResponse>, t: Throwable) {
                }
            })
        }
    }

    fun getBanners() {
        if (context != null) {
            val call = RetrofitModel.getApi(context).allBanners
            call.enqueue(object : CallbackRetrofit<BannersResponse>(context) {
                override fun onResponse(call: Call<BannersResponse>, response: Response<BannersResponse>) {
                    if (context != null && view != null) {
                        bannerList.clear()
                        val result = response.body()
                        if (response.isSuccessful && result != null) {
                            bannerList.addAll(result.data)
                            bannerAdapter.notifyDataSetChanged()
                            bannerLayout.visibility = if (bannerList.isEmpty()) GONE else VISIBLE
                        }
                    }
                }

                override fun onFailure(call: Call<BannersResponse>, t: Throwable) {
                }
            })
        }
    }

    fun refresh() {
        nextAdapter.updateAdapter()
        currentAdapter.updateAdapter()
        getBids(1, bidNextList, nextAdapter)
        getBids(0, bidCurrentList, currentAdapter)
        getBanners()
    }

}
