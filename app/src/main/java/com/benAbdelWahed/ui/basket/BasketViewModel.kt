package com.benAbdelWahed.ui.basket

import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.benAbdelWahed.R
import com.benAbdelWahed.models.Method
import com.benAbdelWahed.network.ResultWrapper
import com.benAbdelWahed.network.repos.DealsRepo
import com.benAbdelWahed.responses.address.Address
import com.benAbdelWahed.responses.address.AddressResponse
import com.benAbdelWahed.responses.deals.Deal
import com.benAbdelWahed.responses.payment.BuyResponse
import com.benAbdelWahed.responses.payment.Data
import com.benAbdelWahed.ui.address.AddressAdapter
import com.benAbdelWahed.utils.CustomViewModel
import com.benAbdelWahed.utils.KeyResourceString
import com.benAbdelWahed.utils.PrefManager
import com.benAbdelWahed.utils.StaticMembers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BasketViewModel : CustomViewModel() {
    private var selectedAddress: Address? = null
    private var selectedMethod: Method? = null
    lateinit var currentDeal: Deal
    val noAddress = ObservableBoolean()
    val methodText = ObservableInt()
    val methodImage = ObservableInt()
    val itemCount = ObservableInt()
    val totalPrice = ObservableField("")
    val dealPrice = ObservableField("0")
    val deliveryPrice = ObservableField("0")
    val selectAddressLiveData = MutableLiveData<Void>()
    val selectPaymentMethodLiveData = MutableLiveData<Method>()
    val paymentWebLiveData = MutableLiveData<Data>()
    val paymentWithWalletLiveData = MutableLiveData<Void>()
    val doneLiveData = MutableLiveData<Void>()
    val list = ArrayList<Address>()

    lateinit var dealsRepo: DealsRepo
    lateinit var prefManager: PrefManager

    val addressAdapter = AddressAdapter(false, list)

    fun init(count: Int, currentDeal: Deal, dealsRepo: DealsRepo, prefManager: PrefManager) {
        itemCount.set(count)
        totalPrice.set(StaticMembers.toFloatFormat(count * currentDeal.price.toFloat() + currentDeal.paymentInfo.shipPrice))
        dealPrice.set(StaticMembers.toFloatFormat(count * currentDeal.price.toFloat()))
        deliveryPrice.set(currentDeal.paymentInfo.shipPrice.toString())
        this.currentDeal = currentDeal
        this.dealsRepo = dealsRepo
        this.prefManager = prefManager
        changeAddressView(prefManager.getObject(StaticMembers.ADDRESS, Address::class.java) as Address?)
//        getData()
    }

    private fun getData(page: Int = 1) {
        showLoading()
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

    private suspend fun showAddressSuccess(value: AddressResponse) {
        withContext(Dispatchers.Main)
        {
            dismissLoading()
            if (value.success) {
                val data = value.data.data
                if (data.isNotEmpty())
                    selectAddress(data[0])
            }
        }
    }

    fun onAddAddressClick(view: View) {
        selectAddressLiveData.value = null
    }

    fun onSelectPaymentClick(view: View) {
        selectPaymentMethodLiveData.value = selectedMethod
    }

    fun onPayClick(view: View) {
        if (selectedAddress != null && selectedMethod != null) {
            if (selectedMethod?.key == StaticMembers.paymentEnum.pay_by_wallet.name)
                paymentWithWalletLiveData.value = null
            else
                buyTheDealNow()
        } else if (selectedAddress == null)
            onAddAddressClick(view)
        else if (selectedMethod == null)
            onSelectPaymentClick(view)
    }

    fun buyTheDealNow() {
        showLoading()
        viewModelScope.launch(Dispatchers.IO) {
            when (val response =
                    dealsRepo.buyDeal(currentDeal.id, selectedMethod?.key
                            ?: "", itemCount.get(), selectedAddress?.id ?: 0)) {
                is ResultWrapper.NetworkError -> handleNetworkError()
                is ResultWrapper.GenericError -> handleGenericError(response)
                is ResultWrapper.Success -> showPaySuccess(response.value)
            }
        }
    }

    private suspend fun showPaySuccess(value: BuyResponse) {
        withContext(Dispatchers.Main)
        {
            dismissLoading()
            if (value.success) {
                val data = value.data
                when (selectedMethod?.key) {
                    StaticMembers.paymentEnum.VISA.name,
                    StaticMembers.paymentEnum.MASTERCARD.name,
                    StaticMembers.paymentEnum.MADA.name -> {
                        paymentWebLiveData.value = data
                    }
                    StaticMembers.paymentEnum.pay_by_wallet.name -> {
                        doneLiveData.value = null
                    }
                    else -> {
                        doneLiveData.value = null
                    }
                }
            }
        }
    }

    fun selectAddress(address: Address) {
        prefManager.setObject(StaticMembers.ADDRESS, address)
        changeAddressView(address)
    }

    private fun changeAddressView(address: Address?) {
        address?.run {
            selectedAddress = address
            list.clear()
            list.add(selectedAddress!!)
            noAddress.set(list.isEmpty())
            addressAdapter.notifyDataSetChanged()
        }
    }

    fun selectPaymentMethod(method: Method) {
        methodImage.set(method.image)
        methodText.set(method.textRes)
        selectedMethod = method
    }

    fun getSelectedAddressId(): Int {
        return selectedAddress?.id ?: 0
    }

    fun getAvailablePaymentMethods(): ArrayList<String> {
        return currentDeal.payment_method_allowed!!
    }


}