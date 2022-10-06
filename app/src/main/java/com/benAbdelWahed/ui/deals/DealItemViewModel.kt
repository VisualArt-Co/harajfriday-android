package com.benAbdelWahed.ui.deals

import android.os.CountDownTimer
import android.view.View
import androidx.databinding.ObservableField
import com.benAbdelWahed.responses.deals.Deal
import com.benAbdelWahed.utils.ItemViewModel
import com.benAbdelWahed.utils.StaticMembers
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class DealItemViewModel(var isCurrentDeals: Boolean, var countDownTimer: CountDownTimer?, val deal: Deal, index: Int, val onBuyClick: (deal: Deal, index: Int) -> Unit, onItemClick: (deal: Deal, index: Int) -> Unit) : ItemViewModel<Deal>(deal, index, onItemClick) {
    val timerObservable = ObservableField("00:00:00")

    init {
        if (isCurrentDeals) {
            countDownTimer?.cancel()
            countDownTimer = object : CountDownTimer(getTimeOfDeal(), 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    timerObservable.set(StaticMembers.getTimeForCounter(millisUntilFinished))
                }

                override fun onFinish() {

                }

            }
            countDownTimer?.start()
        } else {
            timerObservable.set(StaticMembers.getDateWithoutTimeFromBackend(deal.end_date_time))
        }
    }

    private fun getTimeOfDeal(): Long {
        return try {
            val time = SimpleDateFormat(StaticMembers.ISO_DATE_FORMAT, Locale.US).parse(deal.end_date_time)
            val currentTimeInMillis = System.currentTimeMillis()
            abs((time?.time ?: currentTimeInMillis) - currentTimeInMillis)
        } catch (e: Exception) {
            0
        }
    }

    fun buyNow(view: View) {
        onBuyClick(deal, index)
    }
}