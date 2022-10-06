package com.benAbdelWahed.activities

import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.benAbdelWahed.R
import com.benAbdelWahed.adapters.HarajAdapter
import com.benAbdelWahed.responses.haraj_responses.DataItem
import com.benAbdelWahed.responses.haraj_responses.HarajPageProductsResponse
import com.benAbdelWahed.utils.CallbackRetrofit
import com.benAbdelWahed.utils.PrefManager
import com.benAbdelWahed.utils.RetrofitModel
import com.benAbdelWahed.utils.StaticMembers.*
import kotlinx.android.synthetic.main.activity_haraj_search_results.*
import retrofit2.Call
import retrofit2.Response

class HarajSearchResultsActivity : AppCompatActivity() {

    lateinit var adapter: HarajAdapter
    var list = ArrayList<DataItem>()
    var filters: HashMap<String, String> = HashMap()
    private lateinit var prefManager: PrefManager

    var page = 1
    var isLastPage = false
    private var isWaitingForData: Boolean = false

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val totalItemCount = recyclerView.layoutManager!!.itemCount
            val visibleItemCount =
                    (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastVisibleItemPosition()
            val firstVisibleItemPosition =
                    (recyclerView.layoutManager as LinearLayoutManager?)!!.findFirstVisibleItemPosition()

            if (!isWaitingForData && !isLastPage
                    && visibleItemCount + firstVisibleItemPosition >= totalItemCount - 4
                    && firstVisibleItemPosition >= 0
            ) {
                loadMoreHaraj()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_haraj_search_results)
        setSupportActionBar(toolbar)
        backButton.setOnClickListener {
            onBackPressed()
        }
        filters[PER_PAGE]="10"
        var catId = intent.getIntExtra(CATEGORY_ID, -1)
        if (catId > -1)
            filters[CATEGORY_ID] = "$catId"
        catId = intent.getIntExtra(SUBCATEGORY_ID, -1)
        if (catId > -1)
            filters[SUBCATEGORY_ID] = "$catId"
        catId = intent.getIntExtra(SUBSUBCATEGORY_ID, -1)
        if (catId > -1)
            filters[SUBSUBCATEGORY_ID] = "$catId"
        catId = intent.getIntExtra(MODEL, -1)
        if (catId > -1)
            filters[MODEL] = "$catId"
        catId = intent.getIntExtra(CITY, -1)
        if (catId > -1)
            filters[CITY] = "$catId"
        catId = intent.getIntExtra(STATE, -1)
        if (catId > -1) {
            filters[STATE] = "$catId"
            titleText.text = intent.getStringExtra(STATE_NAME)
        }
        val s = intent.getStringExtra(TEXT)
        if (s != null && s.isNotEmpty())
            filters[TEXT] = s

        prefManager = PrefManager.getInstance(this)
        adapter = HarajAdapter(this, list) {
            getAllHaraj()
        }
        recycler.adapter = adapter
        recycler.addOnScrollListener(scrollListener)
        swipe.setOnRefreshListener {
            getAllHaraj()
        }
        getAllHaraj()
    }

    fun getAllHaraj() {
        page =1
        loadMoreHaraj()
    }

    fun loadMoreHaraj() {
        filters[PAGE] = "$page"
        adapter.setShowLoading(true)
        swipe.isRefreshing = true
        errorMessage.visibility = GONE
        isWaitingForData = true
        val call = RetrofitModel.getApi(this).getAllProductsPagination(filters)
        call.enqueue(object : CallbackRetrofit<HarajPageProductsResponse>(this) {
            override fun onResponse(call: Call<HarajPageProductsResponse>, response: Response<HarajPageProductsResponse>) {
                swipe.isRefreshing = false
                adapter.setShowLoading(false)
                isWaitingForData = false
                val result = response.body()
                if (response.isSuccessful && result != null) {
                    isLastPage = !result.data.meta.hasMorePages!!
                    if (page == 1)
                        list.clear()
                    list.addAll(result.data.data)
                    adapter.notifyDataSetChanged()
                    page++
                    errorMessage.visibility = if (list.isEmpty()) VISIBLE else GONE
                } else {
                    toastMessageShort(this@HarajSearchResultsActivity, R.string.connection_error)
                }

            }

            override fun onFailure(call: Call<HarajPageProductsResponse>, t: Throwable) {
                super.onFailure(call, t)
                swipe.isRefreshing = false
                adapter.setShowLoading(false)
                isWaitingForData = false
                getAllHaraj()
            }
        })
    }
}
