package com.benAbdelWahed.ui.order

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.viewModelScope
import com.benAbdelWahed.R
import com.benAbdelWahed.models.Method
import com.benAbdelWahed.network.ResultWrapper
import com.benAbdelWahed.network.repos.DealsRepo
import com.benAbdelWahed.responses.address.Address
import com.benAbdelWahed.responses.order.Order
import com.benAbdelWahed.responses.order.OrderResponse
import com.benAbdelWahed.ui.address.AddressAdapter
import com.benAbdelWahed.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrderViewModel : CustomViewModel() {
    val titleObservable = ObservableField("")
    val descriptionObservable = ObservableField("")
    val imageObservable = ObservableField("")
    val totalPrice = ObservableField("")
    val itemCount = ObservableInt()
    val orderStatus = ObservableInt()
    val list = ArrayList<Address>()
    val noAddress = ObservableBoolean()
    val methodText = ObservableInt()
    val methodImage = ObservableInt()

    private lateinit var order: Order
    lateinit var prefManager: PrefManager
    var dealsRepo: DealsRepo? = null

    val addressAdapter = AddressAdapter(false, list)

    fun init(order: Order, prefManager: PrefManager) {
        this.order = order
        this.prefManager = prefManager
        selectOrder()
    }

    fun init(dealsRepo: DealsRepo, prefManager: PrefManager) {
        this.dealsRepo = dealsRepo
        this.prefManager = prefManager
        getOrderByID()
    }

    private fun getOrderByID() {
        showLoading()
        viewModelScope.launch(Dispatchers.IO) {
            when (val response =
                    dealsRepo!!.getOrders(1)) {
                is ResultWrapper.NetworkError -> handleNetworkError()
                is ResultWrapper.GenericError -> handleGenericError(response)
                is ResultWrapper.Success -> handleData(response.value)
            }
        }
    }

    private suspend fun handleData(value: OrderResponse) {
        withContext(Dispatchers.Main)
        {
            dismissLoading()
            if (value.success) {
                val data = value.data.data
                if (data.isNotEmpty()) {
                    order = data[0]
                    selectOrder()
                }
            }
        }
    }

    private suspend fun handleGenericError(response: ResultWrapper.GenericError) {
        withContext(Dispatchers.Main)
        {
            dismissLoading()
            if (!response.error?.message.isNullOrEmpty())
                showToast(KeyResourceString("_" + response.error?.error_code, response.error?.message
                        ?: ""))
        }
    }

    private suspend fun handleNetworkError() {
        withContext(Dispatchers.Main)
        {
            dismissLoading()
            showToast(R.string.connection_error)
        }
    }

    fun selectOrder() {
        order.run {
            titleObservable.set(title)
            descriptionObservable.set(description)
            imageObservable.set(image)
            itemCount.set(quantity ?: 1)
            totalPrice.set(StaticMembers.toFloatFormat(
                    (quantity ?: 1) * (itemPrice?.toFloat() ?: 0f) + (shipPrice?.toFloat() ?: 0f)))
            address?.run {
                list.clear()
                list.add(this)
                noAddress.set(list.isEmpty())
                addressAdapter.notifyDataSetChanged()
            }
            val item = Method(paymentMethod)
            Utils.getMethodBasedOnKey(item)
            methodText.set(item.textRes)
            methodImage.set(item.image)
            this@OrderViewModel.orderStatus.set(
                    when (orderStatus) {
                        StaticMembers.orderStatus.pending.name -> R.string.pending
                        StaticMembers.orderStatus.in_progress.name -> R.string.in_progress
                        StaticMembers.orderStatus.completed.name -> R.string.completed
                        StaticMembers.orderStatus.cancelled.name -> R.string.cancelled
                        else -> R.string.pending
                    })
        }
    }


}