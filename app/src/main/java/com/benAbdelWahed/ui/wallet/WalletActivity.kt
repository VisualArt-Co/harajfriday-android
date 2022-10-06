package com.benAbdelWahed.ui.wallet

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.benAbdelWahed.R
import com.benAbdelWahed.activities.LoginActivity
import com.benAbdelWahed.databinding.ActivityWalletBinding
import com.benAbdelWahed.network.ApiService
import com.benAbdelWahed.network.repos.AuthRepoImpl
import com.benAbdelWahed.ui.wallet.add.AddToWalletWithMethodsActivity
import com.benAbdelWahed.utils.StaticMembers
import com.benAbdelWahed.utils.Utils
import com.benAbdelWahed.utils.Utils.createViewModel
import com.benAbdelWahed.utils.Utils.getBinding
import com.benAbdelWahed.utils.Utils.openActivity
import com.benAbdelWahed.utils.Utils.showLoading
import com.benAbdelWahed.utils.Utils.showToast

class WalletActivity : FragmentActivity() {
    lateinit var viewModel: WalletViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = createViewModel()
        val binding: ActivityWalletBinding = getBinding(R.layout.activity_wallet)
        viewModel.init(AuthRepoImpl(ApiService.invoke(this)))
        binding.viewModel = viewModel
        initObservables()
    }

    private fun initObservables() {
        viewModel.openAddBalanceScreen.observe(this)
        {
            AddToWalletWithMethodsActivity.openActivity(this)
        }
        viewModel.openAddCouponScreen.observe(this)
        {
            AddCouponDialog.getInstance { viewModel.addCoupon(it) }.show(supportFragmentManager, AddCouponDialog::class.java.simpleName)
        }
        viewModel.openDoneCouponScreen.observe(this)
        {
            DoneCouponDialog.getInstance(it.data.coupon.toString(), it.data.blance.toString()).show(supportFragmentManager, DoneCouponDialog::class.java.simpleName)
        }
        viewModel.backPressedLiveData.observe(this)
        {
            onBackPressed()
        }
        viewModel.resultOK.observe(this)
        {
            setResult(RESULT_OK, Intent().apply { putExtra(StaticMembers.WALLET_BALANCE, viewModel.paymentInformation.get()?.usable_balance) })
        }
        viewModel.showToastLiveData.observe(this) {
            showToast(it)
        }
        viewModel.showToastStringLiveData.observe(this) {
            showToast(it)
        }
        viewModel.showLoadingLiveData.observe(this) {
            if (it)
                showLoading()
            else Utils.dismissLoading()
        }
        viewModel.openLoginLiveData.observe(this) {
            openActivity<LoginActivity>()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (AddToWalletWithMethodsActivity.isItsRequest(requestCode)) {
            viewModel.refreshData()
        }
    }
}