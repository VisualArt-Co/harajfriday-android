package com.benAbdelWahed.utils

import android.content.Context
import com.benAbdelWahed.R
import com.benAbdelWahed.responses.chat_responses.UserItem
import com.benAbdelWahed.responses.customer_response.ProfileResponse
import com.benAbdelWahed.responses.error_response.ErrorLoginResponse
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Response

class ProfileUtil {
    companion object {
        fun getInstance(context: Context): ProfileUtil {
            val profileUtil = ProfileUtil()
            profileUtil.context = context
            return profileUtil
        }
    }

    lateinit var context: Context

    public fun getProfile() {
        if (PrefManager.getInstance(context).apiToken.isNotEmpty()) {
            val call = RetrofitModel.getApi(context).profile
            call.enqueue(object : CallbackRetrofit<ProfileResponse>(context) {
                override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                    val result = response.body()
                    if (response.isSuccessful && result != null) {
                        PrefManager.getInstance(context).setBoolean(StaticMembers.IS_PREMIUM,result.data.isPremium)
                        PrefManager.getInstance(context).setObject(StaticMembers.PROFILE, result.data)
                        val user = UserItem(result.data.fullName, result.data.user_name, result.data.id, result.data.image, null)
                        FirebaseDatabase.getInstance().reference.child(StaticMembers.USERS).child("${user.id}").child(StaticMembers.INFO).setValue(user)
                    } else {
                        try {
                            var errorLoginResponse: ErrorLoginResponse? = null
                            if (response.errorBody() != null) {
                                errorLoginResponse = GsonBuilder().create().fromJson<Any>(response.errorBody()!!.string(), ErrorLoginResponse::class.java) as ErrorLoginResponse?
                                if (errorLoginResponse != null) {
                                    StaticMembers.toastMessageShort(context, errorLoginResponse.message)
                                    if (errorLoginResponse.data.password != null && errorLoginResponse.data.password.isNotEmpty()) {
                                        StaticMembers.toastMessageShort(context, errorLoginResponse.data.password[0])
                                    }
                                    if (errorLoginResponse.data.phone != null && errorLoginResponse.data.phone.isNotEmpty()) {
                                        StaticMembers.toastMessageShort(context, errorLoginResponse.data.phone[0])
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            StaticMembers.toastMessageShort(context, R.string.connection_error)
                        }
                    }
                }
            })

        }
    }
}