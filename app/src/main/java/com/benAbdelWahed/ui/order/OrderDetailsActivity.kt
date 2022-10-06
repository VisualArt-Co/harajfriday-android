package com.benAbdelWahed.ui.order

import android.os.Bundle
import com.benAbdelWahed.R
import com.benAbdelWahed.databinding.ActivityOrderDetailsBinding
import com.benAbdelWahed.network.ApiService
import com.benAbdelWahed.network.repos.DealsRepoImpl
import com.benAbdelWahed.responses.order.Order
import com.benAbdelWahed.utils.BaseActivity
import com.benAbdelWahed.utils.PrefManager
import com.benAbdelWahed.utils.StaticMembers

class OrderDetailsActivity : BaseActivity<ActivityOrderDetailsBinding, OrderViewModel>(R.layout.activity_order_details, OrderViewModel::class.java) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel
        val order = intent.getParcelableExtra<Order>(StaticMembers.ORDER)
        if (order == null)
            viewModel.init(DealsRepoImpl(ApiService.invoke(this)), PrefManager(this))
        else
            viewModel.init(order, PrefManager(this))
    }
}