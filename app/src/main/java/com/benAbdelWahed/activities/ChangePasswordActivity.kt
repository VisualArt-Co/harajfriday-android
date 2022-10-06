package com.benAbdelWahed.activities

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import com.benAbdelWahed.R
import com.benAbdelWahed.models.ChangePasswordModel
import com.benAbdelWahed.responses.add_inc_response.AddIncResponse
import com.benAbdelWahed.responses.error_response.ErrorLoginResponse
import com.benAbdelWahed.utils.CallbackRetrofit
import com.benAbdelWahed.utils.RetrofitModel
import com.benAbdelWahed.utils.StaticMembers
import com.benAbdelWahed.utils.StaticMembers.checkError
import com.benAbdelWahed.utils.StaticMembers.checkTextInputEditText
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_change_password.*
import kotlinx.android.synthetic.main.progress_layout.*
import retrofit2.Call
import retrofit2.Response

class ChangePasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
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

        val oldPassword = oldPasswordEditText.text!!.toString()
        val password = passwordEditText.text!!.toString()
        val confirm = confirmPasswordEditText.text!!.toString()

        if (password == oldPassword) {
            StaticMembers.toastMessageShort(baseContext, R.string.password_the_same)
            return
        }

        if (password != confirm) {
            StaticMembers.toastMessageShort(baseContext, R.string.password_doesnt_match)
            return
        }
        if (
                checkTextInputEditText(passwordEditText, passwordLayout, getString(R.string.password_req)) &&
                checkTextInputEditText(confirmPasswordEditText, confirmPasswordLayout, getString(R.string.confirm_req))
        ) {

            progress.visibility = View.VISIBLE
            val model = ChangePasswordModel()
            model.old_password = oldPassword
            model.password = password
            model.password_confirmation = confirm

            val call = RetrofitModel.getApi(this).changePassword(model)
            call.enqueue(object : CallbackRetrofit<AddIncResponse>(this) {
                override fun onResponse(call: Call<AddIncResponse>, response: Response<AddIncResponse>) {
                    progress.visibility = View.GONE
                    val result = response.body()
                    if (response.isSuccessful && result != null) {
                        finish()
                    } else {
                        checkError(this@ChangePasswordActivity,response.errorBody())
                    }
                }

                override fun onFailure(call: Call<AddIncResponse>, t: Throwable) {
                    super.onFailure(call, t)
                    progress.visibility = View.GONE
                }
            })

        }
    }
}
