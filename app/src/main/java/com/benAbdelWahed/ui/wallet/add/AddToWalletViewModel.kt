package com.benAbdelWahed.ui.wallet.add

import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.benAbdelWahed.R
import com.benAbdelWahed.models.Method
import com.benAbdelWahed.network.ResultWrapper
import com.benAbdelWahed.network.repos.AuthRepo
import com.benAbdelWahed.responses.auth.methods.MethodsResponse
import com.benAbdelWahed.responses.payment.BuyResponse
import com.benAbdelWahed.responses.payment.Data
import com.benAbdelWahed.ui.payment.MethodsAdapter
import com.benAbdelWahed.utils.AlertUtil
import com.benAbdelWahed.utils.CustomViewModel
import com.benAbdelWahed.utils.StaticMembers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddToWalletViewModel : CustomViewModel() {

    private lateinit var authRepo: AuthRepo
    private var selectedMethod: Method? = null
    val creditObservable = ObservableField("0")
    val list = ArrayList<Method>()
    var adapter = MethodsAdapter(list) { item, index ->
        selectedMethod = item
    }


    val paymentWebLiveData = MutableLiveData<Data>()

    fun init(authRepo: AuthRepo) {
        this.authRepo = authRepo
        getAvailableMethods()
    }

    private fun getAvailableMethods() {
        showLoading()
        viewModelScope.launch(Dispatchers.IO) {
            when (val response =
                    authRepo.getAvailableMethodsForWallet()) {
                is ResultWrapper.NetworkError -> handleNetworkError()
                is ResultWrapper.GenericError -> handleGenericError(response)
                is ResultWrapper.Success -> showSuccess(response.value)
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

    private suspend fun showSuccess(value: MethodsResponse) {
        withContext(Dispatchers.Main)
        {
            dismissLoading()
            if (value.success) {
                list.clear()
                value.data[0].forEach {
                    list.add(Method(it))
                }
                if (list.isNotEmpty())
                    selectedMethod = list[0]
                adapter.notifyDataSetChanged()
            }
        }
    }

    fun addCreditToWallet(credit: Int, key: String) {
        showLoading()
        viewModelScope.launch(Dispatchers.IO) {
            when (val response =
                    authRepo.addCreditToWallet(credit, key)) {
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
                }
            }
        }
    }

    fun onPayClick(view: View) {
        selectedMethod?.run {
            val amount = (creditObservable.get() ?: "0").toInt()
            if (amount > 0) {
                addCreditToWallet(amount, this.key ?: "")
            }
            else {
                AlertUtil(view.context).apply {
                    title(R.string.pay_credit_to_wallet)
                    message(R.string.choose_payment_method_and_write_more_than_0)
                    positiveButton(R.string.ok) { _, _ ->

                    }
                    negativeButton(R.string.cancel) { _, _ ->
                        onBackPressed(view)
                    }
                    show()
                }
            }
        }
    }
}