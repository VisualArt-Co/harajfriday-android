package com.benAbdelWahed.ui.deals.details

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.benAbdelWahed.R
import com.benAbdelWahed.activities.LoginActivity
import com.benAbdelWahed.adapters.ImagePagerAdapter
import com.benAbdelWahed.databinding.ActivityDealDetailsBinding
import com.benAbdelWahed.network.ApiService
import com.benAbdelWahed.network.repos.DealsRepoImpl
import com.benAbdelWahed.responses.deals.Deal
import com.benAbdelWahed.responses.haraj_responses.Customer
import com.benAbdelWahed.ui.basket.BasketActivity
import com.benAbdelWahed.ui.deals.dialogs.BuyDealDialog
import com.benAbdelWahed.ui.membership.MembershipActivity
import com.benAbdelWahed.utils.AlertUtil
import com.benAbdelWahed.utils.PrefManager
import com.benAbdelWahed.utils.StaticMembers
import com.benAbdelWahed.utils.Utils
import com.benAbdelWahed.utils.Utils.createViewModel
import com.benAbdelWahed.utils.Utils.getBinding
import com.benAbdelWahed.utils.Utils.openActivity
import com.benAbdelWahed.utils.Utils.showLoading
import com.benAbdelWahed.utils.Utils.showToast
import java.util.*

class DealDetailsActivity : FragmentActivity() {
    lateinit var viewModel: DealDetailsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = createViewModel()
        val deal = intent.getParcelableExtra<Deal>(StaticMembers.CURRENT_DEALS)
        val isPremium = (PrefManager.getInstance(this)
            .getObject(StaticMembers.PROFILE, Customer::class.java) as Customer?)?.isPremium
            ?: false
        if (deal != null) {
            viewModel.init(
                isPremium,
                DealsRepoImpl(ApiService.invoke(this)),
                deal,
                ImagePagerAdapter(this)
            )
        } else {
            var dealId = intent.getIntExtra(StaticMembers.CURRENT_DEALS, 0)
            val uri = intent.data
            if (uri != null) {
                dealId = Integer.parseInt(uri.pathSegments.last() as String)
            }
            viewModel.init(
                isPremium,
                DealsRepoImpl(ApiService.invoke(this)),
                dealId,
                ImagePagerAdapter(this)
            )
        }
        val binding: ActivityDealDetailsBinding = getBinding(R.layout.activity_deal_details)
        binding.viewModel = viewModel
        initObservables()
    }

    private fun initObservables() {
        viewModel.shareDeal.observe(this)
        {
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, R.string.app_name)
                putExtra(
                    Intent.EXTRA_TEXT,
                    String.format(Locale.US, "%sdeals/%s", StaticMembers.getBaseURL(), it)
                )
                startActivity(
                    Intent.createChooser(
                        this,
                        resources.getString(R.string.share_using)
                    )
                )
            }

        }

        viewModel.openAlertDealLiveData.observe(this) {
            BuyDealDialog.getInstance(it)
                .show(supportFragmentManager, BuyDealDialog::class.java.name)
        }

        viewModel.openAlertReachedLimitLiveData.observe(this) {
            AlertUtil(this).apply {
                message(R.string.reached_limit_deal)
                positiveButton(R.string.ok, null)
                show()
            }
        }
        viewModel.openAlertReachedMinLimitLiveData.observe(this) {
            AlertUtil(this).apply {
                message(R.string.reached_min_limit_deal)
                positiveButton(R.string.ok, null)
                show()
            }
        }
        viewModel.openAlertSubscribeLiveData.observe(this) {
            AlertUtil(this).apply {
                message(R.string.subscribe_now_to_membership_to)
                positiveButton(R.string.subscribe) { _, _ ->
                    MembershipActivity.openActivity(this@DealDetailsActivity)
                }
                positiveButton(R.string.cancel, null)
                show()
            }
        }
        viewModel.openAlertAddMoreLiveData.observe(this) {
            AlertUtil(this).apply {
                message(R.string.add_more)
                positiveButton(R.string.ok, null)
                show()
            }
        }

        viewModel.openBuyDealActivityLiveData.observe(this)
        {
            BasketActivity.openBasket(this, it, viewModel.getCurrentSelectedQuantity())
        }
        viewModel.backPressedLiveData.observe(this)
        {
            onBackPressed()
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
        if (BasketActivity.isResultOK(requestCode, resultCode))
            finish()
    }
}