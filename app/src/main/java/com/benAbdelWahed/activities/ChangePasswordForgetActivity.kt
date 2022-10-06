package com.benAbdelWahed.activities

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import com.benAbdelWahed.R
import com.benAbdelWahed.models.ChangePasswordForgetModel
import com.benAbdelWahed.responses.error_response.ErrorLoginResponse
import com.benAbdelWahed.responses.login_response.LoginResponse
import com.benAbdelWahed.utils.CallbackRetrofit
import com.benAbdelWahed.utils.PrefManager
import com.benAbdelWahed.utils.RetrofitModel
import com.benAbdelWahed.utils.StaticMembers
import com.benAbdelWahed.utils.StaticMembers.PHONE
import com.benAbdelWahed.utils.StaticMembers.checkTextInputEditText
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_change_password_forget.*
import kotlinx.android.synthetic.main.progress_layout.*
import retrofit2.Call
import retrofit2.Response

class ChangePasswordForgetActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password_forget)

        confirmPasswordEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                changePassword()
            }
            return@setOnEditorActionListener false
        }
        next.setOnClickListener { changePassword() }
    }

    private fun changePassword() {
        checkTextInputEditText(passwordEditText, passwordLayout, getString(R.string.password_req))
        checkTextInputEditText(confirmPasswordEditText, confirmPasswordLayout, getString(R.string.confirm_req))

        val password = passwordEditText.text!!.toString()
        val confirm = confirmPasswordEditText.text!!.toString()
        if (password != confirm) {
            StaticMembers.toastMessageShort(baseContext, R.string.password_doesnt_match)
            return
        }
        if (
                checkTextInputEditText(passwordEditText, passwordLayout, getString(R.string.password_req)) &&
                checkTextInputEditText(confirmPasswordEditText, confirmPasswordLayout, getString(R.string.confirm_req))
        ) {

            progress.visibility = View.VISIBLE
            val model = ChangePasswordForgetModel()
            model.phone = intent.getStringExtra(PHONE)
            model.password = passwordEditText.text.toString()
            model.password_confirmation = confirmPasswordEditText.text.toString()

            val call = RetrofitModel.getApi(this).forgetPassword(model)
            call.enqueue(object : CallbackRetrofit<LoginResponse>(this) {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    progress.visibility = View.GONE
                    val result = response.body()
                    if (response.isSuccessful && result != null) {
                        StaticMembers.toastMessageShort(baseContext, getString(R.string.welcome))
                        PrefManager.getInstance(baseContext).apiToken = result.accessToken
//                        PrefManager.getInstance(baseContext).setObject(StaticMembers.USER, result!!.getData().getUser())
                        StaticMembers.startActivityOverAll(this@ChangePasswordForgetActivity, MainActivity::class.java)
                    } else {
                        try {
                            var errorCitiesResponse: ErrorLoginResponse? = null
                            if (response.errorBody() != null) {
                                errorCitiesResponse = GsonBuilder().create().fromJson<Any>(response.errorBody()!!.string(), ErrorLoginResponse::class.java) as ErrorLoginResponse?
                                if (errorCitiesResponse != null && errorCitiesResponse.message != null) {
                                    StaticMembers.toastMessageShort(baseContext, errorCitiesResponse.message)
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            StaticMembers.toastMessageShort(baseContext, R.string.connection_error)
                        }

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
