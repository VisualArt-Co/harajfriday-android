package com.benAbdelWahed.ui.payment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.benAbdelWahed.R
import com.benAbdelWahed.databinding.ActivityPaymentFromWalletBinding
import com.benAbdelWahed.models.Method
import com.benAbdelWahed.network.ApiService
import com.benAbdelWahed.network.repos.AuthRepoImpl
import com.benAbdelWahed.ui.wallet.WalletActivity
import com.benAbdelWahed.utils.BaseActivity
import com.benAbdelWahed.utils.PrefManager
import com.benAbdelWahed.utils.StaticMembers
import java.io.Serializable

class PaymentFromWalletActivity : BaseActivity<ActivityPaymentFromWalletBinding, PaymentFromWalletViewModel>(R.layout.activity_payment_from_wallet, PaymentFromWalletViewModel::class.java) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.init( intent.getFloatExtra(StaticMembers.TOTAL_PRICE, 0f), AuthRepoImpl(ApiService.invoke(this)), PrefManager(this),intent.getStringExtra(StaticMembers.TYPE)?:"",intent.getIntExtra(StaticMembers.COUNT, 1))
        binding.viewModel = viewModel
    }

    override fun initObservables() {
        super.initObservables()
        viewModel.payNowLiveData.observe(this) {
            setResult(RESULT_OK)
            actionListener?.onAction()
            finish()
        }
        viewModel.openWalletLiveData.observe(this) {
            startActivityForResult(Intent(this, WalletActivity::class.java), StaticMembers.WALLET_CODE)
        }
    }

    interface ActionListener : Serializable {
        fun onAction()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == StaticMembers.WALLET_CODE && resultCode == RESULT_OK)
            viewModel.getUserBalance()
    }

    public enum class PayFor{
        deals,membership,mazad
    }

    companion  object {
        private const val FROM_WALLET_CODE: Int = 987
        private const val ACTION_ = "action"
        var actionListener: ActionListener? = null

        fun openActivity(activity: Activity,type:String, price: Float, itemCount: Int=1, action: ActionListener? = null) {
            Intent(activity, PaymentFromWalletActivity::class.java).apply {
                putExtra(StaticMembers.TOTAL_PRICE, price)
                putExtra(StaticMembers.COUNT, itemCount)
                putExtra(StaticMembers.TYPE, type)
                actionListener = action
                activity.startActivityForResult(this, FROM_WALLET_CODE)
            }
        }

        fun isResultOk(requestCode: Int, resultCode: Int) =
                (requestCode == FROM_WALLET_CODE && resultCode == RESULT_OK)

    }
}