package com.benAbdelWahed.ui.membership

import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.benAbdelWahed.R
import com.benAbdelWahed.network.ResultWrapper
import com.benAbdelWahed.network.repos.DealsRepo
import com.benAbdelWahed.responses.chat_responses.UserItem
import com.benAbdelWahed.responses.customer_response.ProfileResponse
import com.benAbdelWahed.responses.haraj_responses.Customer
import com.benAbdelWahed.responses.settings_response.SettingsData
import com.benAbdelWahed.responses.settings_response.paymentOptions.PaymentOptionsResponse
import com.benAbdelWahed.responses.subscription.Data
import com.benAbdelWahed.responses.subscription.SubscriptionResponse
import com.benAbdelWahed.utils.*
import com.benAbdelWahed.utils.Utils.toObject
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MembershipViewModel : CustomViewModel() {

    fun init(prefManager: PrefManager, dealsRepo: DealsRepo) {
        this.prefManager = prefManager
        this.dealsRepo = dealsRepo
        customer = prefManager.getObject(StaticMembers.PROFILE, Customer::class.java) as Customer?
        val settingsData = (prefManager.getObject(StaticMembers.SETTINGS, SettingsData::class.java) as SettingsData)
        paymentOptions = settingsData.paymentOptions.toObject(PaymentOptionsResponse::class.java)
        conditionOfMembership = settingsData.conditionsParticipationInPremiumMembership
        membershipPrice = settingsData.yearlySubscrptionPrice.toFloat()
        getSubscriptionDetails()
    }

    var membershipPrice: Float = 0f
    private lateinit var dealsRepo: DealsRepo
    private lateinit var prefManager: PrefManager
    var paymentOptions: PaymentOptionsResponse? = null
    lateinit var conditionOfMembership: String

    var customer: Customer? = null
    val isAMember = ObservableBoolean()
    val isActiveMember = ObservableBoolean()
    val daysObservable = ObservableInt(0)
    val nameObservable = ObservableField("")
    val startDateObservable = ObservableField("")
    val endDateObservable = ObservableField("")
    val subscriptionRulesObservable = ObservableField("")

    val statusLiveData = MutableLiveData<ResourceString>()
    val subscribeLiveData = MutableLiveData<Void>()

    internal fun getSubscriptionDetails() {
        showLoading()
        viewModelScope.launch(Dispatchers.IO) {
            when (val response =
                    dealsRepo.getSubscriptionDetails()) {
                is ResultWrapper.NetworkError -> handleNetworkError()
                is ResultWrapper.GenericError -> handleGenericError(response)
                is ResultWrapper.Success -> handleDetails(response.value)
            }
        }
    }

    private suspend fun handleDetails(value: SubscriptionResponse) {
        withContext(Dispatchers.Main)
        {
            dismissLoading()
            nameObservable.set(customer?.user_name)
            if (value.data.isNotEmpty()) {
                when (value.data[0].subscrption_status) {
                    Data.SubscriptionStatus.active.name -> {
                        setStatusActive(value.data[0])
                    }
                    Data.SubscriptionStatus.suspended.name -> {
                        setStatusSuspended(value.data[0])
                    }
                    else -> {
                        setStatusInactive()
                    }
                }
            }
            getUserProfile()
        }
    }

    private fun getUserProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            when (val response =
                    dealsRepo.getUserProfile()) {
                is ResultWrapper.NetworkError -> handleNetworkError()
                is ResultWrapper.GenericError -> handleGenericError(response)
                is ResultWrapper.Success -> updateUserProfile(response.value)
            }
        }
    }

    private fun updateUserProfile(value: ProfileResponse) {
        prefManager.setBoolean(StaticMembers.IS_PREMIUM,value.data.isPremium)
        prefManager.setObject(StaticMembers.PROFILE, value.data)
        val user = UserItem(value.data.fullName, value.data.user_name, value.data.id, value.data.image, null)
        FirebaseDatabase.getInstance().reference.child(StaticMembers.USERS).child("${user.id}").child(StaticMembers.INFO).setValue(user)
    }


    private fun setStatusActive(data: Data) {
        isAMember.set(true)
        isActiveMember.set(true)
        daysObservable.set(data.time_left_in_days)
        startDateObservable.set(data.start_active_time)
        endDateObservable.set(data.end_active_time)
        statusLiveData.value = IdResourceString(R.string.you_sub_in_premium_membership)
    }


    private fun setStatusSuspended(data: Data) {
        isAMember.set(true)
        isActiveMember.set(false)
        statusLiveData.value = TextResourceString(data.suspended_motivation)
    }

    private fun setStatusInactive() {
        isAMember.set(false)
        subscriptionRulesObservable.set(conditionOfMembership)
    }


    private suspend fun handleGenericError(response: ResultWrapper.GenericError) {
        withContext(Dispatchers.Main)
        {
            dismissLoading()
            if (!response.error?.message.isNullOrEmpty())
                showToast(R.string.connection_error)
        }
    }

    private suspend fun handleNetworkError() {
        withContext(Dispatchers.Main)
        {
            dismissLoading()
            showToast(R.string.connection_error)
        }
    }

    fun onSubscribeClicked(view: View) {
        subscribeLiveData.value = null
    }
}