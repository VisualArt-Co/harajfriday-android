package com.benAbdelWahed.utils

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class CustomViewModel : ViewModel() {
    val backPressedLiveData = MutableLiveData<Void>()
    val showToastLiveData = MutableLiveData<Int>()
    val showToastStringLiveData = MutableLiveData<String>()
    val showToastResourceStringLiveData = MutableLiveData<ResourceString>()
    val showLoadingLiveData = MutableLiveData<Boolean>()
    val openLoginLiveData = MutableLiveData<Boolean>()

    val onBack = {
        backPressedLiveData.value = null
    }

    fun onBackPressed(view:View)
    {
        onBack()
    }

    fun showLoading() {
        showLoadingLiveData.value = true
    }

    fun dismissLoading() {
        showLoadingLiveData.value = false
    }

    fun showToast(resId: Int) {
        showToastLiveData.value = resId
    }

    fun showToast(resourceString: ResourceString) {
        showToastResourceStringLiveData.value = resourceString
    }

    fun showToast(s: String) {
        showToastStringLiveData.value = s
    }
}