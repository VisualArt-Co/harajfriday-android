package com.benAbdelWahed.ui.deals

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.benAbdelWahed.R
import com.benAbdelWahed.databinding.ActivityDealsBinding
import com.benAbdelWahed.ui.PagerAdapter
import com.benAbdelWahed.utils.PrefManager
import com.benAbdelWahed.utils.ProfileUtil
import com.benAbdelWahed.utils.StaticMembers.PAGE
import com.benAbdelWahed.utils.Utils.createViewModel
import com.benAbdelWahed.utils.Utils.getBinding

class DealsActivity : AppCompatActivity() {

    private var selectedPosition: Int = 0

    lateinit var viewModel: DealsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityDealsBinding = getBinding(R.layout.activity_deals)
        viewModel = createViewModel()
        viewModel.init(PrefManager.getInstance(this), PagerAdapter(this))
        ProfileUtil.getInstance(baseContext).getProfile()
        binding.viewModel = viewModel
        initObservables()
    }

    private fun initObservables() {
        viewModel.backPressedLiveData.observe(this)
        {
            onBackPressed()
        }
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(PAGE, selectedPosition)
    }
}
