package com.benAbdelWahed.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import com.benAbdelWahed.R
import com.benAbdelWahed.adapters.HarajAdapter
import com.benAbdelWahed.responses.haraj_responses.DataItem
import com.benAbdelWahed.responses.haraj_responses.HarajProductsResponse
import com.benAbdelWahed.utils.CallbackRetrofit
import com.benAbdelWahed.utils.RetrofitModel
import com.benAbdelWahed.utils.StaticMembers.EDIT_CODE
import com.benAbdelWahed.utils.StaticMembers.toastMessageShort
import kotlinx.android.synthetic.main.activity_fav_haraj.*
import retrofit2.Call
import retrofit2.Response

class FollowedHarajActivity : AppCompatActivity() {

    private var isWaitingForData: Boolean = false
    lateinit var adapter: HarajAdapter
    var list = ArrayList<DataItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fav_haraj)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle(R.string.followed_products)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        adapter = HarajAdapter(this, list, true)
        {
            getFollowedHaraj()
        }

        recycler.adapter = adapter

        swipe.setOnRefreshListener {
            getFollowedHaraj()
        }
        getFollowedHaraj()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK)
            if (requestCode == EDIT_CODE)
                getFollowedHaraj()
    }


    fun getFollowedHaraj() {
        if (!isWaitingForData) {
            adapter.setShowLoading(true)
            swipe.isRefreshing = true
            errorMessage.visibility = GONE
            isWaitingForData = true
            val call = RetrofitModel.getApi(this).followedProducts
            call.enqueue(object : CallbackRetrofit<HarajProductsResponse>(this) {
                override fun onResponse(call: Call<HarajProductsResponse>, response: Response<HarajProductsResponse>) {
                    swipe.isRefreshing = false
                    adapter.setShowLoading(false)
                    isWaitingForData = false
                    list.clear()
                    val result = response.body()
                    if (response.isSuccessful && result != null) {
                        list.addAll(result.data)
                        adapter.notifyDataSetChanged()
                        errorMessage.visibility = if (list.isEmpty()) VISIBLE else GONE
                    } else {
                        toastMessageShort(this@FollowedHarajActivity, R.string.connection_error)
                    }

                }

                override fun onFailure(call: Call<HarajProductsResponse>, t: Throwable) {
                    super.onFailure(call, t)
                    swipe.isRefreshing = false
                    adapter.setShowLoading(false)
                    isWaitingForData = false
                    getFollowedHaraj()
                }
            })
        }
    }

}
