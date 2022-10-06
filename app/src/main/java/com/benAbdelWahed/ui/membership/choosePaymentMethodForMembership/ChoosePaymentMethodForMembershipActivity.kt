package com.benAbdelWahed.ui.membership.choosePaymentMethodForMembership

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.benAbdelWahed.R
import com.benAbdelWahed.databinding.ActivityChoosePaymentMethodForMembershipBinding
import com.benAbdelWahed.network.ApiService
import com.benAbdelWahed.network.repos.DealsRepoImpl
import com.benAbdelWahed.responses.settings_response.paymentOptions.PaymentOption
import com.benAbdelWahed.ui.payment.PaymentFromWalletActivity
import com.benAbdelWahed.ui.payment.PaymentWebActivity
import com.benAbdelWahed.utils.BaseActivity
import com.benAbdelWahed.utils.StaticMembers
import com.benAbdelWahed.utils.Utils.showToast

class ChoosePaymentMethodForMembershipActivity : BaseActivity<ActivityChoosePaymentMethodForMembershipBinding, ChoosePaymentMethodForMembershipViewModel>(R.layout.activity_choose_payment_method_for_membership, ChoosePaymentMethodForMembershipViewModel::class.java) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.init(DealsRepoImpl(ApiService.invoke(this)), intent.getStringArrayListExtra(StaticMembers.PAYMENT)
                ?: ArrayList())
        binding.viewModel = viewModel
    }

    override fun initObservables() {
        super.initObservables()
        viewModel.paymentWebLiveData.observe(this) { url ->
            PaymentWebActivity.openActivity(this, url.payment_link?:"")
        }
        viewModel.openWalletLiveData.observe(this) { url ->
            PaymentFromWalletActivity.openActivity(this,  PaymentFromWalletActivity.PayFor.membership.name , intent.getFloatExtra(StaticMembers.TOTAL_PRICE, 0f))
        }
        viewModel.doneLiveData.observe(this) {
            setDone()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (PaymentWebActivity.isResultNotOk(requestCode, resultCode)) {
            showToast(R.string.payment_failed)
        } else if (PaymentWebActivity.isResultOk(requestCode, resultCode)) {
            setDone()
        }
        if (PaymentFromWalletActivity.isResultOk(requestCode, resultCode)) {
            viewModel.upgradeToPremium(StaticMembers.paymentEnum.pay_by_wallet.name)
        }
    }

    private fun setDone() {
        showToast(R.string.done_membership)
        setResult(RESULT_OK)
        finish()
    }

    companion object {
        private const val PAYMENT_CODE = 148

        fun openActivity(activity: FragmentActivity, paymentOptions: ArrayList<String>, totalPrice: Float) {
            Intent(activity, ChoosePaymentMethodForMembershipActivity::class.java).apply {
                putExtra(StaticMembers.PAYMENT, paymentOptions)
                putExtra(StaticMembers.TOTAL_PRICE, totalPrice)
                activity.startActivityForResult(this, PAYMENT_CODE)
            }
        }

        fun isResultOk(requestCode: Int, resultCode: Int) =
                (requestCode == PAYMENT_CODE && resultCode == RESULT_OK)

    }
}
