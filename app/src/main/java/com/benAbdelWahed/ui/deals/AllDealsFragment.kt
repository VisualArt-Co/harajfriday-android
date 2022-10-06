package com.benAbdelWahed.ui.deals

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.benAbdelWahed.R
import com.benAbdelWahed.activities.LoginActivity
import com.benAbdelWahed.databinding.FragmentAllDealsBinding
import com.benAbdelWahed.network.ApiService
import com.benAbdelWahed.network.repos.DealsRepoImpl
import com.benAbdelWahed.ui.basket.BasketActivity
import com.benAbdelWahed.ui.deals.details.DealDetailsActivity
import com.benAbdelWahed.ui.deals.dialogs.BuyDealDialog
import com.benAbdelWahed.utils.PrefManager
import com.benAbdelWahed.utils.StaticMembers
import com.benAbdelWahed.utils.Utils
import com.benAbdelWahed.utils.Utils.createViewModel
import com.benAbdelWahed.utils.Utils.openActivity
import com.benAbdelWahed.utils.Utils.showLoading
import com.benAbdelWahed.utils.Utils.showToast

class AllDealsFragment : Fragment() {
    lateinit var viewModel: DealsFragmentViewModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: FragmentAllDealsBinding = Utils.getBinding(inflater, R.layout.fragment_all_deals, container)
        viewModel = createViewModel()
        viewModel.init(DealsRepoImpl(ApiService.invoke(context)), PrefManager(context))
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservables()
    }

    private fun initObservables() {
        viewModel.openAlertDealLiveData.observe(viewLifecycleOwner) {
            BuyDealDialog.getInstance(it).show(childFragmentManager, BuyDealDialog::class.java.name)
        }
        viewModel.openBuyDealActivityLiveData.observe(viewLifecycleOwner) {
            Intent(context, BasketActivity::class.java).apply {
                putExtra(StaticMembers.CURRENT_DEALS, it)
                startActivity(this)
            }
        }
        viewModel.openDealDetailsLiveData.observe(viewLifecycleOwner) {
            startActivity(Intent(context, DealDetailsActivity::class.java).apply { putExtra(StaticMembers.CURRENT_DEALS, it) })
        }
        viewModel.showToastLiveData.observe(viewLifecycleOwner) {
            showToast(it)
        }
        viewModel.showToastStringLiveData.observe(viewLifecycleOwner) {
            showToast(it)
        }
        viewModel.showLoadingLiveData.observe(viewLifecycleOwner) {
            if (it)
                showLoading()
            else Utils.dismissLoading()
        }
        viewModel.openLoginLiveData.observe(viewLifecycleOwner) {
            openActivity<LoginActivity>()
        }
    }

    companion object {
        fun getInstance(): AllDealsFragment {
            val args = Bundle()
            return AllDealsFragment().apply { arguments = args }
        }
    }
}