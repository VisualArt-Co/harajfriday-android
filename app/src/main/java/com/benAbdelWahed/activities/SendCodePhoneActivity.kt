package com.benAbdelWahed.activities

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import com.benAbdelWahed.R
import com.benAbdelWahed.responses.error_response.ErrorLoginResponse
import com.benAbdelWahed.responses.send_sms.SMSResponse
import com.benAbdelWahed.utils.CallbackRetrofit
import com.benAbdelWahed.utils.RetrofitModel
import com.benAbdelWahed.utils.StaticMembers
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_send_code_phone.*
import kotlinx.android.synthetic.main.progress_layout.*
import retrofit2.Call
import retrofit2.Response

class SendCodePhoneActivity : AppCompatActivity() {

    private var isRegistration: Boolean = true
    lateinit var cdt: CountDownTimer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_code_phone)
        isRegistration = intent.getBooleanExtra(StaticMembers.FROM_REGISTRATION, true)
        phoneEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                sendCode()
            }
            return@setOnEditorActionListener false
        }
        next.setOnClickListener { sendCode() }
    }

    private fun sendCode() {
        StaticMembers.checkTextInputEditText(phoneEditText, phoneLayout, getString(R.string.phone_req))
        if (!termsCheck.isChecked) {
            StaticMembers.toastMessageShort(baseContext, getString(R.string.confirm_terms))
            return
        }
        if (
                StaticMembers.checkTextInputEditText(phoneEditText, phoneLayout, getString(R.string.phone_req))
        ) {
            progress.visibility = View.VISIBLE
            val selectedPhone = "0"+phoneEditText.text.toString()
            val call: Call<SMSResponse> = if (isRegistration)
                RetrofitModel.getApi(this).sendSms(selectedPhone)
            else
                RetrofitModel.getApi(this).sendCodeForget(selectedPhone)
            call.enqueue(object : CallbackRetrofit<SMSResponse>(this) {
                override fun onResponse(call: Call<SMSResponse>, response: Response<SMSResponse>) {
                    progress.visibility = View.GONE
                    val result = response.body()
                    if (response.isSuccessful && result != null) {
                        StaticMembers.toastMessageShort(baseContext, getString(R.string.code_sent))
                        var i = Intent(baseContext, VerifyPhoneSignUpActivity::class.java)
                        i.putExtra(StaticMembers.PHONE, phoneEditText.text.toString())
                        i.putExtra(StaticMembers.FROM_REGISTRATION, intent.getBooleanExtra(StaticMembers.FROM_REGISTRATION, false))
                        startActivity(i)
                    } else {
                        StaticMembers.checkError(this@SendCodePhoneActivity, response.errorBody(),false)
                    }
                }

                override fun onFailure(call: Call<SMSResponse>, t: Throwable) {
                    super.onFailure(call, t)
                    progress.visibility = View.GONE
                }
            })

        }
    }
}
