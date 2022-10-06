package com.benAbdelWahed.activities

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View.*
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import com.benAbdelWahed.R
import com.benAbdelWahed.responses.error_response.ErrorLoginResponse
import com.benAbdelWahed.responses.send_sms.SMSResponse
import com.benAbdelWahed.utils.CallbackRetrofit
import com.benAbdelWahed.utils.RetrofitModel
import com.benAbdelWahed.utils.StaticMembers
import com.benAbdelWahed.utils.StaticMembers.*
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_verify_phone_sign_up.*
import kotlinx.android.synthetic.main.progress_layout.*
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class VerifyPhoneSignUpActivity : AppCompatActivity() {

    private var isRegistration: Boolean = true
    private var selectedPhone: String? = null
    lateinit var cdt: CountDownTimer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_phone_sign_up)
        selectedPhone = intent.getStringExtra(PHONE)
        isRegistration = intent.getBooleanExtra(FROM_REGISTRATION, true)
        doneSendText.text = String.format(Locale.getDefault(), getString(R.string.done_sent_code), selectedPhone)

        next.setOnClickListener {
            checkCode()
        }
        codeEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                checkCode()
            }
            return@setOnEditorActionListener false
        }

        cdt = object : CountDownTimer(210000, 1000) {
            override fun onFinish() {
                resend.visibility = VISIBLE
            }

            override fun onTick(millisUntilFinished: Long) {
                val s = SimpleDateFormat("mm:ss", Locale.US).format(millisUntilFinished)
                remains.text = String.format(Locale.getDefault(), getString(R.string.time_remains), s)
            }
        }
        cdt.start()
        resend.setOnClickListener {
            resendCode()
        }
        remains.setOnClickListener { onBackPressed() }
    }

    private fun checkCode() {
        if (codeEditText.text!!.isNotEmpty()) {
            var i: Intent
            if (isRegistration) {
                i = Intent(baseContext, RegistrationActivity::class.java)
                i.putExtra(PHONE, intent.getStringExtra(PHONE))
            } else {
                i = Intent(baseContext, ChangePasswordForgetActivity::class.java)
                i.putExtra(PHONE, intent.getStringExtra(PHONE))
            }
            startActivity(i)
        }
            /*progress.visibility = VISIBLE
            val call: Call<SMSResponse> = if (isRegistration)
                RetrofitModel.getApi(this).checkPhone(selectedPhone, codeEditText.text!!.toString())
            else
                RetrofitModel.getApi(this).checkPhoneForget(selectedPhone, codeEditText.text!!.toString())
            call.enqueue(object : CallbackRetrofit<SMSResponse>(this) {
                override fun onResponse(call: Call<SMSResponse>, response: Response<SMSResponse>) {
                    progress.visibility = GONE
                    if (response.isSuccessful) {
                        var i: Intent
                        if (isRegistration) {
                            i = Intent(baseContext, RegistrationActivity::class.java)
                            i.putExtra(PHONE, intent.getStringExtra(PHONE))
                        } else {
                            i = Intent(baseContext, ChangePasswordForgetActivity::class.java)
                            i.putExtra(PHONE, intent.getStringExtra(PHONE))
                        }
                        startActivity(i)
                    } else {
                        checkError(this@VerifyPhoneSignUpActivity, response.errorBody(), false)
                    }
                }

                override fun onFailure(call: Call<SMSResponse>, t: Throwable) {
                    super.onFailure(call, t)
                    resend.visibility = VISIBLE
                    progress.visibility = GONE
                }
            })

        } else {
            toastMessageShort(baseContext, R.string.code_empty)
        }*/
    }

    private fun resendCode() {
        resend.visibility = INVISIBLE
        progress.visibility = VISIBLE
        val call: Call<SMSResponse> = if (isRegistration)
            RetrofitModel.getApi(this).sendSms("0$selectedPhone")
        else
            RetrofitModel.getApi(this).sendCodeForget("0$selectedPhone")
        call.enqueue(object : CallbackRetrofit<SMSResponse>(this) {
            override fun onResponse(call: Call<SMSResponse>, response: Response<SMSResponse>) {
                progress.visibility = GONE
                val result = response.body()
                if (response.isSuccessful && result != null) {
                    cdt.start()
                    toastMessageShort(baseContext, getString(R.string.code_sent))
                } else {
                    resend.visibility = VISIBLE
                    try {
                        var errorCitiesResponse: ErrorLoginResponse? = null
                        if (response.errorBody() != null) {
                            errorCitiesResponse = GsonBuilder().create().fromJson<Any>(response.errorBody()!!.string(), ErrorLoginResponse::class.java) as ErrorLoginResponse?
                            if (errorCitiesResponse != null && errorCitiesResponse.message != null) {
                                toastMessageShort(baseContext, errorCitiesResponse.message)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        toastMessageShort(baseContext, R.string.connection_error)
                    }

                }
            }

            override fun onFailure(call: Call<SMSResponse>, t: Throwable) {
                super.onFailure(call, t)
                resend.visibility = VISIBLE
                progress.visibility = GONE
            }
        })

    }
}
