package com.benAbdelWahed.ui.deals

import androidx.core.widget.NestedScrollView
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.benAbdelWahed.R
import com.benAbdelWahed.network.ResultWrapper
import com.benAbdelWahed.network.repos.DealsRepo
import com.benAbdelWahed.responses.deals.Deal
import com.benAbdelWahed.responses.deals.DealsResponse
import com.benAbdelWahed.utils.CustomViewModel
import com.benAbdelWahed.utils.PrefManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DealsFragmentViewModel : CustomViewModel() {

    private var hasMorePages = true
    private var page = 1
    val oldList = ArrayList<Deal>()
    val newList = ArrayList<Deal>()
    lateinit var adapterOld: DealsAdapter
    lateinit var adapterNew: DealsAdapter

    lateinit var dealsRepo: DealsRepo
    lateinit var prefManager: PrefManager

    val openAlertDealLiveData = MutableLiveData<Deal>()
    val openBuyDealActivityLiveData = MutableLiveData<Deal>()
    val openDealDetailsLiveData = MutableLiveData<Deal>()
    val noData = ObservableBoolean()
    val noDataNew = ObservableBoolean()
    val noDataOld = ObservableBoolean()
    val refreshObservable = ObservableBoolean()
    var onRefreshListener = SwipeRefreshLayout.OnRefreshListener {
        page = 1
        getDataNew()
        getData()
    }


    val scrollListener = NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, oldScrollY ->
        if (v.getChildAt(v.childCount - 1) != null) {
            if (!refreshObservable.get() && hasMorePages
                    && (scrollY >= (v.getChildAt(v.childCount - 1).measuredHeight - v.measuredHeight)) &&
                    scrollY > oldScrollY
            ) {
                getData(++page)
            }
        }
    }

    fun init(dealsRepo: DealsRepo, prefManager: PrefManager) {
        this.dealsRepo = dealsRepo
        this.prefManager = prefManager
        adapterOld = DealsAdapter(false, oldList, { item, index ->
            /*if (!prefManager.hasToken() && !item.can_access && !item.accessAlert.isNullOrEmpty())
                openAlertDealLiveData.value = item
            else {
                openBuyDealActivityLiveData.value = item
            }*/
            openDealDetailsLiveData.value = item
        }, { item, index ->
            openDealDetailsLiveData.value = item
        })
        adapterNew = DealsAdapter(true, newList, { item, index ->
            /*if (!prefManager.hasToken() && !item.can_access && !item.accessAlert.isNullOrEmpty())
                openAlertDealLiveData.value = item
            else {
                openBuyDealActivityLiveData.value = item
            }*/
            openDealDetailsLiveData.value = item
        }, { item, index ->
            openDealDetailsLiveData.value = item
        })
        page = 1
        getDataNew()
        getData()
    }

    private fun getDataNew(page: Int = 1) {
        refreshObservable.set(true)
        noData.set(false)
        viewModelScope.launch(Dispatchers.IO) {
            when (val response = dealsRepo.getDeals(page = page)) {
                is ResultWrapper.NetworkError -> handleNetworkError()
                is ResultWrapper.GenericError -> handleGenericError(response)
                is ResultWrapper.Success -> showNewSuccess(response.value)
            }
        }
    }

    private fun getData(page: Int = 1) {
        refreshObservable.set(true)
        noData.set(false)
        viewModelScope.launch(Dispatchers.IO) {
            when (val response =
                    dealsRepo.getPastDeals(page = page)) {
                is ResultWrapper.NetworkError -> handleNetworkError()
                is ResultWrapper.GenericError -> handleGenericError(response)
                is ResultWrapper.Success -> showOldSuccess(response.value)
            }
        }
    }

    private suspend fun handleGenericError(response: ResultWrapper.GenericError) {
        withContext(Dispatchers.Main)
        {
            refreshObservable.set(false)
            if (!response.error?.message.isNullOrEmpty())
                showToast(R.string.connection_error)
        }
    }

    private suspend fun handleNetworkError() {
        withContext(Dispatchers.Main)
        {
            refreshObservable.set(false)
            showToast(R.string.connection_error)
        }
    }

    private suspend fun showNewSuccess(value: DealsResponse) {
        withContext(Dispatchers.Main)
        {
            refreshObservable.set(false)
            if (value.success) {
                val data = value.data.data
                if (page == 1)
                    newList.clear()
                newList.addAll(data)
                noData.set(newList.isEmpty() && oldList.isEmpty())
                noDataNew.set(newList.isEmpty())
//                hasMorePages = value.data.meta.hasMorePages
                if (newList.isEmpty() || page == 1)
                    adapterNew.notifyDataSetChanged()
                else
                    adapterNew.notifyItemRangeInserted(
                            newList.size,
                            data.size
                    )
            }
        }
    }

    private suspend fun showOldSuccess(value: DealsResponse) {
        withContext(Dispatchers.Main)
        {
            refreshObservable.set(false)
            if (value.success) {
                val data = value.data.data
                if (page == 1)
                    oldList.clear()
                oldList.addAll(data)
                noData.set(newList.isEmpty() && oldList.isEmpty())
                noDataOld.set(oldList.isEmpty())
                hasMorePages = value.data.meta.hasMorePages
                if (oldList.isEmpty() || page == 1)
                    adapterOld.notifyDataSetChanged()
                else
                    adapterOld.notifyItemRangeInserted(
                            oldList.size,
                            data.size
                    )
            }
        }
    }


}