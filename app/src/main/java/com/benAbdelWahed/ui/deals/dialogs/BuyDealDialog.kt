package com.benAbdelWahed.ui.deals.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.benAbdelWahed.R
import com.benAbdelWahed.responses.deals.Deal
import com.benAbdelWahed.utils.StaticMembers
import kotlinx.android.synthetic.main.fragment_buy_deal.*

class BuyDealDialog : DialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.DialogTrans)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_buy_deal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        text.text = arguments?.getParcelable<Deal>(StaticMembers.CURRENT_DEALS)?.accessAlert
        okButton.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        private var INSTANCE: BuyDealDialog? = null
        fun getInstance(deal: Deal): BuyDealDialog {
            INSTANCE?.dismiss()
            INSTANCE = BuyDealDialog()
            INSTANCE?.arguments = Bundle().apply { putParcelable(StaticMembers.CURRENT_DEALS, deal) }
            return INSTANCE!!
        }
    }

}