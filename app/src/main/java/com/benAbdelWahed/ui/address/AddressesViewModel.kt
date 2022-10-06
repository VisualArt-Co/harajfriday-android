package com.benAbdelWahed.ui.address

import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.benAbdelWahed.R
import com.benAbdelWahed.network.ResultWrapper
import com.benAbdelWahed.network.repos.DealsRepo
import com.benAbdelWahed.responses.ErrorResponse
import com.benAbdelWahed.responses.address.Address
import com.benAbdelWahed.responses.address.AddressResponse
import com.benAbdelWahed.utils.CustomViewModel
import com.benAbdelWahed.utils.PrefManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddressesViewModel : CustomViewModel() {

    private var selectedId: Int = 0
    private var hasMorePages = true
    val noAddress = ObservableBoolean()
    private var page = 1
    val addOrEditAddressLiveData = MutableLiveData<Address>()
    val selectAddressLiveData = MutableLiveData<Address>()
    val refreshObservable = ObservableBoolean()
    var onRefreshListener = SwipeRefreshLayout.OnRefreshListener {
        page = 1
        getData()
    }
    val list = ArrayList<Address>()

    lateinit var dealsRepo: DealsRepo
    lateinit var prefManager: PrefManager

    val addressAdapter = AddressAdapter(true,list,
            onItemEditClick = { item, index ->
                addOrEditAddressLiveData.value = item
            }, onItemDeleteClick = { item, index ->
        deleteAddress(item, index)
    }, { item, index ->
        selectedId = item.id?:0
        selectAddressLiveData.value = item
    })

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

    fun init(selectedId: Int, dealsRepo: DealsRepo, prefManager: PrefManager) {
        this.selectedId = selectedId
        this.dealsRepo = dealsRepo
        this.prefManager = prefManager
        page = 1
        getData()
    }

    private fun getData(page: Int = 1) {
        refreshObservable.set(true)
        viewModelScope.launch(Dispatchers.IO) {
            when (val response =
                    dealsRepo.getAddresses(page = page)) {
                is ResultWrapper.NetworkError -> handleNetworkError()
                is ResultWrapper.GenericError -> handleGenericError(response)
                is ResultWrapper.Success -> showAddressSuccess(response.value)
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

    private suspend fun showAddressSuccess(value: AddressResponse) {
        withContext(Dispatchers.Main)
        {
            refreshObservable.set(false)
            if (value.success) {
                val data = value.data.data
                if (page == 1)
                    list.clear()
                list.addAll(data)
                noAddress.set(list.isEmpty())
                hasMorePages = value.data.meta.hasMorePages
                addressAdapter.selectAddressById(selectedId)
                if (list.isEmpty() || page == 1)
                    addressAdapter.notifyDataSetChanged()
                else
                    addressAdapter.notifyItemRangeInserted(
                            list.size,
                            data.size
                    )
            }
        }
    }

    private suspend fun showAddAddressSuccess(value: ErrorResponse) {
        withContext(Dispatchers.Main)
        {
            dismissLoading()
            if (value.success) {
                page = 1
                showToast(R.string.done_address_added)
                getData(page)
            }
        }
    }

    private fun deleteAddress(address: Address, index: Int) {
        showLoading()
        viewModelScope.launch(Dispatchers.IO) {
            when (val response =
                    dealsRepo.deleteAddress(address.id!!)) {
                is ResultWrapper.NetworkError -> handleNetworkError()
                is ResultWrapper.GenericError -> handleGenericError(response)
                is ResultWrapper.Success -> showDeleteAddressSuccess(response.value, index)
            }
        }
    }

    private suspend fun showDeleteAddressSuccess(value: ErrorResponse, index: Int) {
        withContext(Dispatchers.Main)
        {
            dismissLoading()
            if (value.success) {
                addressAdapter.removeItem(index)
                showToast(R.string.address_deleted)
            }
        }
    }

    fun onAddAddressClick(view: View) {
        addOrEditAddressLiveData.value = null
    }

    fun addOrEditAddress(address: Address) {
        showLoading()
        viewModelScope.launch(Dispatchers.IO) {
            when (val response =
                    if (address.id != null && address.id != 0) dealsRepo.editAddress(address.id, address) else dealsRepo.addAddress(address)) {
                is ResultWrapper.NetworkError -> handleNetworkError()
                is ResultWrapper.GenericError -> handleGenericError(response)
                is ResultWrapper.Success -> showAddAddressSuccess(response.value)
            }
        }
    }

}