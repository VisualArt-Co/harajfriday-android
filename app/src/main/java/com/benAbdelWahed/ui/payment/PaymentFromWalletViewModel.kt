package com.benAbdelWahed.ui.payment

import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.benAbdelWahed.R
import com.benAbdelWahed.network.ResultWrapper
import com.benAbdelWahed.network.repos.AuthRepo
import com.benAbdelWahed.responses.auth.balance.BalanceResponse
import com.benAbdelWahed.utils.CustomViewModel
import com.benAbdelWahed.utils.PrefManager
import com.benAbdelWahed.utils.StaticMembers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PaymentFromWalletViewModel : CustomViewModel() {
    val itemCount = ObservableInt()
    val isLessThanPrice = ObservableBoolean()
    val totalPrice = ObservableField("")
    val balance = ObservableField("")
    val payFor = ObservableField("")
    val payNowLiveData = MutableLiveData<Void>()
    val openWalletLiveData = MutableLiveData<Void>()

    lateinit var authRepo: AuthRepo
    lateinit var prefManager: PrefManager


    fun init(totalPrice: Float, authRepo1: AuthRepo, prefManager: PrefManager,type:String,count: Int = 0) {
        itemCount.set(count)
        this.totalPrice.set(StaticMembers.toFloatFormat(count * totalPrice))
        this.authRepo = authRepo1
        this.prefManager = prefManager
        payFor.set(type)
        getUserBalance()
    }

    internal fun getUserBalance() {
        showLoading()
        viewModelScope.launch(Dispatchers.IO) {
            when (val response =
                    authRepo.getUserUsableBalance()) {
                is ResultWrapper.NetworkError -> handleNetworkError()
                is ResultWrapper.GenericError -> handleGenericError(response)
                is ResultWrapper.Success -> handleSuccess(response.value)
            }
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

    private suspend fun handleSuccess(value: BalanceResponse) {
        withContext(Dispatchers.Main)
        {
            dismissLoading()
            if (value.success) {
                val data = value.data
                balance.set(data.blance.toString())
                isLessThanPrice.set(data.blance.toFloat() < totalPrice.get()?.toFloat()!!)
            }
        }
    }

    fun onPayClick(view: View) {
        payNowLiveData.value = null
    }

    fun onOpenWalletClick(view: View) {
        openWalletLiveData.value = null
    }
}