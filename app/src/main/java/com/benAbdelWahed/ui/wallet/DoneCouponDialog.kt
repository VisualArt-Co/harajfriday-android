package com.benAbdelWahed.ui.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.benAbdelWahed.R
import kotlinx.android.synthetic.main.dialog_done_coupon.*

class DoneCouponDialog : DialogFragment() {
    lateinit var couponValue: String
    lateinit var balance: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.DialogTrans)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_done_coupon, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        text.setText(String.format("تم إضافة قسيمة رصيد بقيمة %s ريال \nرصيدك الحالي هو %s ريال سعودي", couponValue, balance))
        okButton.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        private var INSTANCE: DoneCouponDialog? = null
        fun getInstance(couponValue: String, balance: String): DoneCouponDialog {
            INSTANCE?.dismiss()
            INSTANCE = DoneCouponDialog().apply {
                this.couponValue = couponValue
                this.balance = balance
            }
            return INSTANCE!!
        }
    }

}