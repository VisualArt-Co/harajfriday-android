package com.benAbdelWahed.ui.wallet

import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.benAbdelWahed.R
import com.benAbdelWahed.network.ResultWrapper
import com.benAbdelWahed.network.repos.AuthRepo
import com.benAbdelWahed.responses.auth.coupon.CouponResponse
import com.benAbdelWahed.responses.auth.wallet.PaymentHistoryItem
import com.benAbdelWahed.responses.auth.wallet.PaymentInformation
import com.benAbdelWahed.responses.auth.wallet.WalletResponse
import com.benAbdelWahed.utils.CustomViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WalletViewModel : CustomViewModel() {

    private var page = 1
    private var hasMorePages: Boolean = false
    val list = ArrayList<PaymentHistoryItem>()
    var adapter = WalletAdapter(list) { item, index ->

    }

    lateinit var authRepo: AuthRepo

    val openAddBalanceScreen = MutableLiveData<Void>()
    val openAddCouponScreen = MutableLiveData<Void>()
    val resultOK = MutableLiveData<Void>()
    val openDoneCouponScreen = MutableLiveData<CouponResponse>()
    val noData = ObservableBoolean()
    val paymentInformation = ObservableField(PaymentInformation(0, 0, 0))
    val refreshObservable = ObservableBoolean()
    var onRefreshListener = SwipeRefreshLayout.OnRefreshListener {
        page = 1
        getData()
    }
    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val totalItemCount = recyclerView.layoutManager!!.itemCount
            val visibleItemCount =
                    (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastVisibleItemPosition()
            val firstVisibleItemPosition =
                    (recyclerView.layoutManager as LinearLayoutManager?)!!.findFirstVisibleItemPosition()

            if (!adapter.isLoading && hasMorePages
                    && visibleItemCount + firstVisibleItemPosition >= totalItemCount
                    && firstVisibleItemPosition >= 0
            ) {
                getData(++page)
            }
        }
    }

    fun init(authRepo: AuthRepo) {
        this.authRepo = authRepo
        refreshData()
    }

    fun refreshData() {
        showLoading()
        page = 1
        getData()
    }

    private fun getData(page: Int = 1) {
        refreshObservable.set(true)
        noData.set(false)
        adapter.isLoading = true
        viewModelScope.launch(Dispatchers.IO) {
            when (val response =
                    authRepo.walletSummery(page = page)) {
                is ResultWrapper.NetworkError -> handleNetworkError()
                is ResultWrapper.GenericError -> handleGenericError(response)
                is ResultWrapper.Success -> showSuccess(response.value)
            }
        }
    }

    fun addCoupon(coupon: String) {
        showLoading()
        viewModelScope.launch(Dispatchers.IO) {
            when (val response =
                    authRepo.addCoupon(coupon)) {
                is ResultWrapper.NetworkError -> handleNetworkError()
                is ResultWrapper.GenericError -> handleGenericError(response)
                is ResultWrapper.Success -> showSuccessCoupon(response.value)
            }
        }
    }

    private suspend fun showSuccessCoupon(value: CouponResponse) {
        withContext(Dispatchers.Main)
        {
            dismissLoading()
            if (value.success)
                openDoneCouponScreen.value = value
            else showToast(value.message)
        }
    }


    private suspend fun handleGenericError(response: ResultWrapper.GenericError) {
        withContext(Dispatchers.Main)
        {
            dismissLoading()
            refreshObservable.set(false)
            adapter.isLoading = false
            if (!response.error?.message.isNullOrEmpty())
                showToast(R.string.connection_error)
        }
    }

    private suspend fun handleNetworkError() {
        withContext(Dispatchers.Main)
        {
            dismissLoading()
            refreshObservable.set(false)
            adapter.isLoading = false
            showToast(R.string.connection_error)
        }
    }

    private suspend fun showSuccess(value: WalletResponse) {
        withContext(Dispatchers.Main)
        {
            dismissLoading()
            refreshObservable.set(false)
            adapter.isLoading = false
            if (value.success) {
                val data = value.data.payment_history.data
                if (page == 1)
                    list.clear()
                list.addAll(data)
                noData.set(list.isEmpty())
                paymentInformation.set(value.data.payment_information)
                hasMorePages = value.data.payment_history.meta.hasMorePages
                if (list.isEmpty() || page == 1)
                    adapter.notifyDataSetChanged()
                else
                    adapter.notifyItemRangeInserted(
                            list.size,
                            data.size
                    )
                resultOK.value = null
            }
        }
    }

    fun onAddBalanceClick(view: View) {
        openAddBalanceScreen.value = null
    }

    fun onAddCouponClick(view: View) {
        openAddCouponScreen.value = null
    }
}