package com.benAbdelWahed.ui.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.benAbdelWahed.R
import com.benAbdelWahed.utils.Utils.showToast
import kotlinx.android.synthetic.main.dialog_add_coupon.*

class AddCouponDialog : DialogFragment() {
    lateinit var onCouponAdded:(String)->Unit
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.DialogTrans)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_add_coupon, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        okButton.setOnClickListener {
            if (editText.text.isNullOrEmpty())
                showToast(R.string.add_coupon_num)
            else onCouponAdded(editText.text.toString())
        }
        container.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        private var INSTANCE: AddCouponDialog? = null
        fun getInstance(onCouponAdded:(String)->Unit): AddCouponDialog {
            INSTANCE?.dismiss()
            INSTANCE = AddCouponDialog()
            INSTANCE?.onCouponAdded = onCouponAdded
            return INSTANCE!!
        }
    }

}