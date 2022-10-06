package com.benAbdelWahed.ui.deals.details

import android.os.CountDownTimer
import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.benAbdelWahed.R
import com.benAbdelWahed.adapters.ImagePagerAdapter
import com.benAbdelWahed.network.ResultWrapper
import com.benAbdelWahed.network.repos.DealsRepo
import com.benAbdelWahed.responses.deals.Data
import com.benAbdelWahed.responses.deals.Deal
import com.benAbdelWahed.utils.CustomViewModel
import com.benAbdelWahed.utils.PrefManager
import com.benAbdelWahed.utils.StaticMembers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class DealDetailsViewModel : CustomViewModel() {

    lateinit var dealsRepo: DealsRepo
    lateinit var pagerAdapter: ImagePagerAdapter
    var countDownTimer: CountDownTimer? = null

    val isCurrentDealObservable = ObservableBoolean()
    val quantityObservable = ObservableInt(1)
    val timerObservable = ObservableField("00:00:00")
    val daysObservable = ObservableField("")
    val maxQuantityForOneCustomerObservable = ObservableField("0")
    var deal = ObservableField<Deal>()
    val shareDeal = MutableLiveData<Int>()
    val openAlertDealLiveData = MutableLiveData<Deal>()
    val openAlertReachedLimitLiveData = MutableLiveData<Void>()
    val openAlertReachedMinLimitLiveData = MutableLiveData<Void>()
    val openAlertSubscribeLiveData = MutableLiveData<Void>()
    val openAlertAddMoreLiveData = MutableLiveData<Void>()
    val openBuyDealActivityLiveData = MutableLiveData<Deal>()
    var isPremium: Boolean = false
    fun init(
        isPremium: Boolean,
        dealsRepo: DealsRepo,
        deal: Deal,
        pagerAdapter: ImagePagerAdapter
    ) {
        this.isPremium = isPremium
        this.dealsRepo = dealsRepo
        this.pagerAdapter = pagerAdapter
        this.deal.set(deal)
        initUI()
    }

    private fun initUI() {
        quantityObservable.set(deal.get()?.MaxQuantityForThisUser ?: 0)
        maxQuantityForOneCustomerObservable.set(deal.get()?.max_allowed_to_premium.toString())
        pagerAdapter.setList(deal.get()?.images)
        initCountDownTimer()
    }

    fun init(
        isPremium: Boolean, dealsRepo: DealsRepo, dealId: Int, pagerAdapter: ImagePagerAdapter
    ) {
        this.isPremium = isPremium
        this.dealsRepo = dealsRepo
        this.pagerAdapter = pagerAdapter
        getDealDetails(dealId)
    }

    private fun initCountDownTimer() {
        cancelCountDownTimer()
        if (isCurrentDeals()) {
            countDownTimer = object : CountDownTimer(getTimeOfDeal(), 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    timerObservable.set(StaticMembers.getTimeForCounterWithLine(millisUntilFinished))
                    daysObservable.set(StaticMembers.getDaysLine(millisUntilFinished))
                }

                override fun onFinish() {

                }

            }
            countDownTimer?.start()
        } else {
            timerObservable.set(StaticMembers.getTimeInText(getTimeOfDeal()))
        }
    }

    private fun getTimeOfDeal(): Long {
        return try {
            val time = SimpleDateFormat(StaticMembers.ISO_DATE_FORMAT, Locale.US).parse(
                deal.get()?.end_date_time
                    ?: ""
            )
            val currentTimeInMillis = System.currentTimeMillis()
            return abs((time?.time ?: currentTimeInMillis) - currentTimeInMillis)
        } catch (e: Exception) {
            0
        }
    }

    private fun isCurrentDeals(): Boolean {
        try {
            val time = SimpleDateFormat(StaticMembers.ISO_DATE_FORMAT, Locale.US).parse(
                deal.get()?.end_date_time
                    ?: ""
            )
            isCurrentDealObservable.set(Date().before(time))
        } catch (e: Exception) {
            isCurrentDealObservable.set(false)
        }
        return isCurrentDealObservable.get()
    }

    private fun cancelCountDownTimer() {
        countDownTimer?.cancel()
    }

    private fun getDealDetails(dealId: Int) {
        showLoading()
        viewModelScope.launch(Dispatchers.IO) {
            when (val response = dealsRepo.getDealDetails(dealId)) {
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

    private suspend fun showSuccess(value: Data) {
        withContext(Dispatchers.Main)
        {
            dismissLoading()
            deal.set(value.data[0])
            initUI()
        }
    }

    fun onBuyClick(view: View) {
        deal.get()?.apply {
            if (!PrefManager.getInstance(view.context).hasToken())
                openLoginLiveData.value = null
            else if (!can_access && !accessAlert.isNullOrEmpty())
                openAlertDealLiveData.value = this
            else {
                if (quantityObservable.get() >= deal.get()?.MaxQuantityForThisUser ?: 1) {
                    if (isPremium)
                        openAlertReachedLimitLiveData.value = null
                    else openAlertAddMoreLiveData.value = null
                    return
                }
                openBuyDealActivityLiveData.value = this
            }
        }

    }

    fun onShareClicked(view: View) {
        shareDeal.value = deal.get()?.id
    }

    fun onDecreaseClicked(view: View) {
        if (quantityObservable.get() < 1) {
            openAlertReachedMinLimitLiveData.value = null
            return
        }
        quantityObservable.set(
            max(
                quantityObservable.get() - 1,
                deal.get()!!.MinQuantityForThisUser
            )
        )
    }

    fun onIncreaseClicked(view: View) {
        if (quantityObservable.get() >= deal.get()?.MaxQuantityForThisUser ?: 1) {
            if (isPremium)
                openAlertReachedLimitLiveData.value = null
            else openAlertSubscribeLiveData.value = null
            return
        }
        quantityObservable.set(
            min(
                quantityObservable.get() + 1, deal.get()?.MaxQuantityForThisUser
                    ?: 1
            )
        )
    }

    fun getCurrentSelectedQuantity() = quantityObservable.get()
}