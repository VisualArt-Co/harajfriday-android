package com.benAbdelWahed.ui.order

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.benAbdelWahed.R
import com.benAbdelWahed.network.ResultWrapper
import com.benAbdelWahed.network.repos.DealsRepo
import com.benAbdelWahed.responses.order.Order
import com.benAbdelWahed.responses.order.OrderResponse
import com.benAbdelWahed.utils.CustomViewModel
import com.benAbdelWahed.utils.PrefManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrdersViewModel : CustomViewModel() {

    private var hasMorePages = true
    val noOrder = ObservableBoolean()
    private var page = 1
    val selectOrderLiveData = MutableLiveData<Order>()
    val refreshObservable = ObservableBoolean()
    var onRefreshListener = SwipeRefreshLayout.OnRefreshListener {
        page = 1
        getData()
    }
    val list = ArrayList<Order>()

    lateinit var dealsRepo: DealsRepo
    lateinit var prefManager: PrefManager

    val orderAdapter = OrderAdapter( list) { item, _ ->
        selectOrderLiveData.value = item
    }

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val totalItemCount = recyclerView.layoutManager!!.itemCount
            val visibleItemCount =
                    (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastVisibleItemPosition()
            val firstVisibleItemPosition =
                    (recyclerView.layoutManager as LinearLayoutManager?)!!.findFirstVisibleItemPosition()

            if (!refreshObservable.get() && hasMorePages
                    && visibleItemCount + firstVisibleItemPosition >= totalItemCount
                    && firstVisibleItemPosition >= 0
            ) {
                getData(++page)
            }
        }
    }

    fun init( dealsRepo: DealsRepo, prefManager: PrefManager) {
        this.dealsRepo = dealsRepo
        this.prefManager = prefManager
        page = 1
        getData()
    }

    private fun getData(page: Int = 1) {
        refreshObservable.set(true)
        viewModelScope.launch(Dispatchers.IO) {
            when (val response =
                    dealsRepo.getOrders(page = page)) {
                is ResultWrapper.NetworkError -> handleNetworkError()
                is ResultWrapper.GenericError -> handleGenericError(response)
                is ResultWrapper.Success -> handleSuccess(response.value)
            }
        }
    }

    private suspend fun handleGenericError(response: ResultWrapper.GenericError) {
        withContext(Dispatchers.Main)
        {
            refreshObservable.set(false)
            dismissLoading()
            if (!response.error?.message.isNullOrEmpty())
                showToast(R.string.connection_error)
        }
    }

    private suspend fun handleNetworkError() {
        withContext(Dispatchers.Main)
        {
            refreshObservable.set(false)
            dismissLoading()
            showToast(R.string.connection_error)
        }
    }

    private suspend fun handleSuccess(value: OrderResponse) {
        withContext(Dispatchers.Main)
        {
            refreshObservable.set(false)
            if (value.success) {
                val data = value.data.data
                if (page == 1)
                    list.clear()
                list.addAll(data)
                noOrder.set(list.isEmpty())
                hasMorePages = value.data.meta.hasMorePages
                if (list.isEmpty() || page == 1)
                    orderAdapter.notifyDataSetChanged()
                else
                    orderAdapter.notifyItemRangeInserted(
                            list.size,
                            data.size
                    )
            }
        }
    }

}