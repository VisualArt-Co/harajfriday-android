package com.benAbdelWahed.ui.address

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.benAbdelWahed.R
import com.benAbdelWahed.databinding.ActivityAddressesBinding
import com.benAbdelWahed.network.ApiService
import com.benAbdelWahed.network.repos.DealsRepoImpl
import com.benAbdelWahed.responses.address.Address
import com.benAbdelWahed.utils.BaseActivity
import com.benAbdelWahed.utils.PrefManager
import com.benAbdelWahed.utils.StaticMembers

class AddressesActivity : BaseActivity<ActivityAddressesBinding, AddressesViewModel>(R.layout.activity_addresses, AddressesViewModel::class.java) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel
        viewModel.init(intent.getIntExtra(PRE_SELECT_ID,0),DealsRepoImpl(ApiService.invoke(this)), PrefManager(this))
    }

    override fun initObservables() {
        super.initObservables()
        viewModel.addOrEditAddressLiveData.observe(this) { address ->
            AddAddressDialog.getInstance(address) {
                viewModel.addOrEditAddress(it)
            }.show(supportFragmentManager, AddAddressDialog::javaClass.name)
        }
        viewModel.selectAddressLiveData.observe(this) {
            Intent().apply {
                putExtra(StaticMembers.ADDRESS, it)
                setResult(RESULT_OK, this)
                onBackPressed()
            }
        }
    }

    companion object {
        private const val PRE_SELECT_ID = "preSelectedId"
        private const val ADDRESS_CODE = 147

        fun openActivity(activity: FragmentActivity, preSelectedId: Int) {
            Intent(activity, AddressesActivity::class.java).apply {
                putExtra(PRE_SELECT_ID, preSelectedId)
                activity.startActivityForResult(this, ADDRESS_CODE)
            }
        }

        fun isResultOk(requestCode: Int, resultCode: Int) =
                (requestCode == ADDRESS_CODE && resultCode == RESULT_OK)

        fun getSelectedAddress(data: Intent?): Address? {
            return data?.getParcelableExtra(StaticMembers.ADDRESS)
        }
    }
}
