package com.benAbdelWahed.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.VISIBLE
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import com.benAbdelWahed.R
import com.benAbdelWahed.models.LoginModel
import com.benAbdelWahed.responses.chat_responses.UserItem
import com.benAbdelWahed.responses.customer_response.ProfileResponse
import com.benAbdelWahed.responses.error_response.ErrorLoginResponse
import com.benAbdelWahed.responses.login_response.LoginResponse
import com.benAbdelWahed.utils.CallbackRetrofit
import com.benAbdelWahed.utils.PrefManager
import com.benAbdelWahed.utils.RetrofitModel
import com.benAbdelWahed.utils.StaticMembers.*
import com.google.android.gms.tasks.Task
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.progress_layout.*
import retrofit2.Call
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private var instanceId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        password.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginNow()
            }
            return@setOnEditorActionListener false
        }
        progress.setOnClickListener {  }
        login.setOnClickListener {
            loginNow()
        }
        register.setOnClickListener {
            val i = Intent(baseContext, SendCodePhoneActivity::class.java)
            i.putExtra(FROM_REGISTRATION, true)
            startActivity(i)
            finish()
        }
        forgetPassword.setOnClickListener {
            val i = Intent(baseContext, SendCodePhoneActivity::class.java)
            startActivity(i)
            finish()
        }
        skip.setOnClickListener {
            val i = Intent(baseContext, MainActivity::class.java)
            startActivity(i)
            finish()
        }
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener {
            if (it.isSuccessful && it.result != null) {
                instanceId = it.result!!.token
            }
        }
    }

    private fun getProfile() {
        val call = RetrofitModel.getApi(this).profile
        call.enqueue(object : CallbackRetrofit<ProfileResponse>(this) {
            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                val result = response.body()
                if (response.isSuccessful && result != null) {
                    PrefManager.getInstance(baseContext).setObject(PROFILE, result.data)
                    val user = UserItem(result.data.fullName,result.data.user_name, result.data.id, result.data.image, null)
                    FirebaseDatabase.getInstance().reference.child(USERS).child("${user.id}").child(FIREBASE_TOKENS).child(instanceId!!).setValue(0)
                    FirebaseDatabase.getInstance().reference.child(USERS).child("${user.id}").child(INFO).setValue(user)
                            .addOnCompleteListener {
                        progress.visibility = View.GONE
                        toastMessageShort(this@LoginActivity, R.string.welcome)
                        if (intent.getBooleanExtra(ACTION, false))
                            finish()
                        else
                            startActivityOverAll(this@LoginActivity, MainActivity::class.java)
                    }

                } else {
                    progress.visibility = View.GONE
                    try {
                        var errorLoginResponse: ErrorLoginResponse? = null
                        if (response.errorBody() != null) {
                            errorLoginResponse = GsonBuilder().create().fromJson<Any>(response.errorBody()!!.string(), ErrorLoginResponse::class.java) as ErrorLoginResponse?
                            if (errorLoginResponse != null) {
                                toastMessageShort(baseContext, errorLoginResponse.message)
                                if (errorLoginResponse.data.password != null && errorLoginResponse.data.password.isNotEmpty()) {
                                    toastMessageShort(baseContext, errorLoginResponse.data.password[0])
                                }
                                if (errorLoginResponse.data.phone != null && errorLoginResponse.data.phone.isNotEmpty()) {
                                    toastMessageShort(baseContext, errorLoginResponse.data.phone[0])
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        toastMessageShort(baseContext, R.string.connection_error)
                    }

                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                super.onFailure(call, t)
                progress.visibility = View.GONE
            }
        })

    }

    private fun loginNow() {
        checkTextInputEditText(phoneEditText, phoneLayout, getString(R.string.phone_empty))
        checkTextInputEditText(password, passwordLayout, getString(R.string.password_req))
        if (checkTextInputEditText(phoneEditText, phoneLayout, getString(R.string.phone_empty)) &&
                checkTextInputEditText(password, passwordLayout, getString(R.string.password_req))) {
            val sendModel = LoginModel()

            sendModel.phone = phoneEditText.text!!.toString()
            sendModel.password = password.text!!.toString()
            sendModel.firebaseToken = instanceId
            progress.visibility = VISIBLE
            val call = RetrofitModel.getApi(this).login(sendModel)
            call.enqueue(object : CallbackRetrofit<LoginResponse>(this) {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    val result = response.body()
                    if (response.isSuccessful && result != null) {
                        PrefManager.getInstance(baseContext).apiToken = result.accessToken
                        PrefManager.getInstance(baseContext).setBoolean(IS_PREMIUM, result.userType == PREMIUM)
                        getProfile()
                    } else {
                        progress.visibility = View.GONE
                        toastMessageShort(baseContext, R.string.wrong_phone_password)
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    super.onFailure(call, t)
                    progress.visibility = View.GONE
                }
            })
        }
    }
}
