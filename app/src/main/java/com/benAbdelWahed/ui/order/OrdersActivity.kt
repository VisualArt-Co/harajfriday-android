package com.benAbdelWahed.ui.order

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.benAbdelWahed.R
import com.benAbdelWahed.databinding.ActivityOrdersBinding
import com.benAbdelWahed.network.ApiService
import com.benAbdelWahed.network.repos.DealsRepoImpl
import com.benAbdelWahed.utils.BaseActivity
import com.benAbdelWahed.utils.PrefManager
import com.benAbdelWahed.utils.StaticMembers

class OrdersActivity : BaseActivity<ActivityOrdersBinding, OrdersViewModel>(R.layout.activity_orders, OrdersViewModel::class.java) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel
        viewModel.init(DealsRepoImpl(ApiService.invoke(this)), PrefManager(this))
    }

    override fun initObservables() {
        super.initObservables()
        viewModel.selectOrderLiveData.observe(this) {
            Intent(this, OrderDetailsActivity::class.java).apply {
                putExtra(StaticMembers.ORDER, it)
                startActivity(this)
            }
        }
    }

    companion object {
        fun openActivity(activity: FragmentActivity) {
            Intent(activity, OrdersActivity::class.java).apply {
                activity.startActivity(this)
            }
        }
    }
}
