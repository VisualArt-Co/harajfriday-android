package com.benAbdelWahed.ui.membership.choosePaymentMethodForMembership

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.benAbdelWahed.R
import com.benAbdelWahed.models.Method
import com.benAbdelWahed.network.ResultWrapper
import com.benAbdelWahed.network.repos.DealsRepo
import com.benAbdelWahed.network.repos.DealsRepoImpl
import com.benAbdelWahed.responses.payment.BuyResponse
import com.benAbdelWahed.responses.payment.Data
import com.benAbdelWahed.ui.payment.MethodsAdapter
import com.benAbdelWahed.utils.CustomViewModel
import com.benAbdelWahed.utils.StaticMembers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChoosePaymentMethodForMembershipViewModel : CustomViewModel() {

    private lateinit var dealsRepo: DealsRepo
    private var selectedMethod: Method? = null
    val list = ArrayList<Method>()
    var adapter = MethodsAdapter(list) { item, index ->
        selectedMethod = item
    }


    val paymentWebLiveData = MutableLiveData<Data>()
    val doneLiveData = MutableLiveData<Data>()
    val openWalletLiveData = MutableLiveData<Data>()

    fun init(dealsRepoImpl: DealsRepoImpl, parcelableArrayListExtra: java.util.ArrayList<String>) {
        this.dealsRepo = dealsRepoImpl
        addAvailablePaymentMethods(parcelableArrayListExtra)
    }

    private fun addAvailablePaymentMethods(parcelableArrayListExtra: java.util.ArrayList<String>) {
        parcelableArrayListExtra.forEach {
            list.add(Method(it))
        }
        if (list.isNotEmpty())
            selectedMethod = list[0]
        adapter.notifyDataSetChanged()
    }

    fun onPayClick(view: View) {
        selectedMethod?.run {
            if (key == StaticMembers.paymentEnum.pay_by_wallet.name)
                openWalletLiveData.value = null
            else upgradeToPremium(key ?: "")
        }
    }

    private suspend fun handleGenericError(response: ResultWrapper.GenericError) {
        withContext(Dispatchers.Main)
        {
            dismissLoading()
            if (!response.error?.message.isNullOrEmpty())
                showToast(R.string.connection_error)
        }
    }

    private suspend fun handleNetworkError() {
        withContext(Dispatchers.Main)
        {
            dismissLoading()
            showToast(R.string.connection_error)
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
                }
            }
        }
    }

    fun upgradeToPremium(key: String) {
        showLoading()
        viewModelScope.launch(Dispatchers.IO) {
            when (val response =
                    dealsRepo.upgradeToPremium(key)) {
                is ResultWrapper.NetworkError -> handleNetworkError()
                is ResultWrapper.GenericError -> handleGenericError(response)
                is ResultWrapper.Success -> showPaySuccess(response.value)
            }
        }
    }
}