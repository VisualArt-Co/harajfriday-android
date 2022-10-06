package com.benAbdelWahed.ui.wallet.add

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.benAbdelWahed.R
import com.benAbdelWahed.databinding.ActivityAddToWalletWithMethodsBinding
import com.benAbdelWahed.network.ApiService
import com.benAbdelWahed.network.repos.AuthRepoImpl
import com.benAbdelWahed.ui.payment.PaymentWebActivity
import com.benAbdelWahed.utils.BaseActivity
import com.benAbdelWahed.utils.Utils.showToast

class AddToWalletWithMethodsActivity : BaseActivity<ActivityAddToWalletWithMethodsBinding, AddToWalletViewModel>(R.layout.activity_add_to_wallet_with_methods, AddToWalletViewModel::class.java) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.init(AuthRepoImpl(ApiService.invoke(this)))
        binding.viewModel = viewModel
    }

    override fun initObservables() {
        super.initObservables()
        viewModel.paymentWebLiveData.observe(this) { url ->
            PaymentWebActivity.openActivity(this, url.payment_link ?: "")
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (PaymentWebActivity.isItsRequest(requestCode)) {
            if (PaymentWebActivity.isResultOk(requestCode, resultCode))
                showToast(R.string.payment_success)
            setResult(RESULT_OK)
            finish()
        }
    }

    companion object {
        private const val PAYMENT_CODE = 148

        fun openActivity(activity: FragmentActivity) {
            Intent(activity, AddToWalletWithMethodsActivity::class.java).apply {
                activity.startActivityForResult(this, PAYMENT_CODE)
            }
        }

        fun isResultOk(requestCode: Int, resultCode: Int) =
                (requestCode == PAYMENT_CODE && resultCode == RESULT_OK)

        fun isItsRequest(requestCode: Int) =
                (requestCode == PAYMENT_CODE)

    }
}
