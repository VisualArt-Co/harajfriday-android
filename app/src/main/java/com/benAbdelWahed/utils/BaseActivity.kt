package com.benAbdelWahed.utils

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import com.benAbdelWahed.activities.LoginActivity
import com.benAbdelWahed.utils.Utils.createViewModel
import com.benAbdelWahed.utils.Utils.getBinding
import com.benAbdelWahed.utils.Utils.openActivity
import com.benAbdelWahed.utils.Utils.showLoading
import com.benAbdelWahed.utils.Utils.showToast

open class BaseActivity<Binding : ViewDataBinding, VM : CustomViewModel>(val layout: Int, private val viewModelClass: Class<VM>) : AppCompatActivity() {
    val viewModel: VM by lazy {
        createViewModel(viewModelClass)
    }
    lateinit var binding: Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getBinding(layout)
        initObservables()
    }

    open fun initObservables() {
        viewModel.backPressedLiveData.observe(this)
        {
            onBackPressed()
        }
        viewModel.showToastLiveData.observe(this) {
            showToast(it)
        }
        viewModel.showToastResourceStringLiveData.observe(this) {
            showToast(it.format(this))
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
}