package com.benAbdelWahed.ui.basket

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.benAbdelWahed.R
import com.benAbdelWahed.databinding.ActivityBasketBinding
import com.benAbdelWahed.network.ApiService
import com.benAbdelWahed.network.repos.DealsRepoImpl
import com.benAbdelWahed.responses.deals.Deal
import com.benAbdelWahed.ui.address.AddressesActivity
import com.benAbdelWahed.ui.order.OrdersActivity
import com.benAbdelWahed.ui.payment.PaymentFromWalletActivity
import com.benAbdelWahed.ui.payment.PaymentMethodsActivity
import com.benAbdelWahed.ui.payment.PaymentWebActivity
import com.benAbdelWahed.utils.AlertUtil
import com.benAbdelWahed.utils.BaseActivity
import com.benAbdelWahed.utils.PrefManager
import com.benAbdelWahed.utils.StaticMembers
import com.benAbdelWahed.utils.Utils.showToast

class BasketActivity : BaseActivity<ActivityBasketBinding, BasketViewModel>(R.layout.activity_basket, BasketViewModel::class.java) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel
        viewModel.init(intent.getIntExtra(StaticMembers.COUNT, 1), intent.getParcelableExtra(StaticMembers.CURRENT_DEALS)!!, DealsRepoImpl(ApiService.invoke(this)), PrefManager(this))
    }

    override fun initObservables() {
        super.initObservables()
        viewModel.selectAddressLiveData.observe(this) { address ->
            AddressesActivity.openActivity(this, viewModel.getSelectedAddressId())
        }
        viewModel.selectPaymentMethodLiveData.observe(this) { selectedMethod ->
            PaymentMethodsActivity.openActivity(this, viewModel.getAvailablePaymentMethods(), selectedMethod)
        }
        viewModel.paymentWebLiveData.observe(this) { url ->
            PaymentWebActivity.openActivity(this, url.payment_link?:"")
        }
        viewModel.paymentWithWalletLiveData.observe(this) {
            PaymentFromWalletActivity.openActivity(this, PaymentFromWalletActivity.PayFor.deals.name, viewModel.currentDeal.price.toFloat(), viewModel.itemCount.get())
        }
        viewModel.doneLiveData.observe(this) { url ->
            doneMessage()
        }
    }

    private fun doneMessage() {
        AlertUtil(this).apply {
            message(R.string.done_buying)
            positiveButton(R.string.continue_shopping) { dialog, which ->
                setResult(RESULT_OK)
                finish()
            }
            negativeButton(R.string.my_orders) { dialog, which ->
                openOrders()
            }
            show()
        }
    }

    private fun openOrders() {
        startActivity(Intent(this, OrdersActivity::class.java))
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (AddressesActivity.isResultOk(requestCode, resultCode))
            viewModel.selectAddress(AddressesActivity.getSelectedAddress(data)!!)
        if (PaymentMethodsActivity.isResultOk(requestCode, resultCode))
            viewModel.selectPaymentMethod(PaymentMethodsActivity.getSelectedPayment(data)!!)
        if (PaymentFromWalletActivity.isResultOk(requestCode, resultCode))
            viewModel.buyTheDealNow()
        if (PaymentWebActivity.isResultNotOk(requestCode, resultCode)) {
            showToast(R.string.payment_failed)
        } else if (PaymentWebActivity.isResultOk(requestCode, resultCode)) {
            openOrders()
        }
    }
    companion object{
        private const val BASKET_CODE = 3245

        fun openBasket(activity:FragmentActivity, deal: Deal, currentQuantity:Int){
            Intent(activity, BasketActivity::class.java).apply {
                putExtra(StaticMembers.CURRENT_DEALS, deal)
                putExtra(StaticMembers.COUNT, currentQuantity)
                activity.startActivityForResult(this,BASKET_CODE)
            }
        }

        fun isResultOK(requestCode: Int, resultCode: Int): Boolean {
           return requestCode == BASKET_CODE && resultCode == RESULT_OK
        }
    }
}