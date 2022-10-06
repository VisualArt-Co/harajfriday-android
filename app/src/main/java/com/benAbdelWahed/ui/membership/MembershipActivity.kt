package com.benAbdelWahed.ui.membership

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.benAbdelWahed.R
import com.benAbdelWahed.databinding.ActivityMembershipBinding
import com.benAbdelWahed.network.ApiService
import com.benAbdelWahed.network.repos.DealsRepoImpl
import com.benAbdelWahed.ui.membership.choosePaymentMethodForMembership.ChoosePaymentMethodForMembershipActivity
import com.benAbdelWahed.utils.BaseActivity
import com.benAbdelWahed.utils.PrefManager
import kotlinx.android.synthetic.main.activity_membership.*
import java.io.Serializable

class MembershipActivity : BaseActivity<ActivityMembershipBinding, MembershipViewModel>(R.layout.activity_membership, MembershipViewModel::class.java) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.init(PrefManager.getInstance(this), DealsRepoImpl(ApiService.invoke(this)))
        binding.viewModel = viewModel
    }

    override fun initObservables() {
        super.initObservables()
        viewModel.statusLiveData.observe(this) {
            membershipCardHeader.text = it.format(this)
            subscriptionSuspended.text = it.format(this)
        }
        viewModel.subscribeLiveData.observe(this) {
            ChoosePaymentMethodForMembershipActivity.openActivity(this, (viewModel.paymentOptions?.payment_locations?.upgrade_to_premium?.allowed
                    ?: ArrayList()) as ArrayList<String>, viewModel.membershipPrice)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (ChoosePaymentMethodForMembershipActivity.isResultOk(requestCode, resultCode)) {
            viewModel.getSubscriptionDetails()
            setResult(RESULT_OK)
            actionListener?.onAction()
        } else {

        }
    }

    interface ActionListener : Serializable {
        fun onAction()
    }

    companion object {

        var actionListener: ActionListener? = null
        val requestCode = 2983
        fun openActivity(context: Context, onActionListener: ActionListener) {
            this.actionListener = onActionListener
            context.startActivity(Intent(context, MembershipActivity::class.java))
        }

        fun openActivity(activity: FragmentActivity) {
            activity.startActivityForResult(Intent(activity, MembershipActivity::class.java), requestCode)
        }

        fun isResultOk(requestCode: Int, resultCode: Int): Boolean {
            return (requestCode == this.requestCode && resultCode == RESULT_OK)
        }
    }
}