package com.benAbdelWahed.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.benAbdelWahed.R
import com.benAbdelWahed.models.ContactUsModel
import com.benAbdelWahed.responses.add_inc_response.AddIncResponse
import com.benAbdelWahed.utils.CallbackRetrofit
import com.benAbdelWahed.utils.RetrofitModel
import com.benAbdelWahed.utils.StaticMembers
import kotlinx.android.synthetic.main.fg_contact_us.*
import retrofit2.Call
import retrofit2.Response

class ContactUsDialog : DialogFragment() {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        backButton.setOnClickListener { v -> dismiss() }
        send.setOnClickListener {
            sendContactUs()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.DialogTrans)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fg_contact_us, container, false)
    }

    fun sendContactUs() {
        StaticMembers.checkTextInputEditText(nameEditText, nameLayout, getString(R.string.name_req))
        StaticMembers.checkTextInputEditText(emailEditText, emailLayout, getString(R.string.email_req))
        StaticMembers.checkTextInputEditText(phoneEditText, phoneLayout, getString(R.string.phone_req))
        StaticMembers.checkTextInputEditText(messageEditText, messageLayout, getString(R.string.message_req))

        if (
                StaticMembers.checkTextInputEditText(nameEditText, nameLayout, getString(R.string.name_req))
                && StaticMembers.checkTextInputEditText(emailEditText, emailLayout, getString(R.string.email_req))
                && StaticMembers.checkTextInputEditText(phoneEditText, phoneLayout, getString(R.string.phone_req))
                && StaticMembers.checkTextInputEditText(messageEditText, messageLayout, getString(R.string.message_req))
        ) {


            val contactUsModel = ContactUsModel(nameEditText.text.toString(),
                    emailEditText.text.toString(),
                    phoneEditText.text.toString(),
                    messageEditText.text.toString())
            val call = RetrofitModel.getApi(context).contactUs(contactUsModel)
            call.enqueue(object : CallbackRetrofit<AddIncResponse>(context) {
                override fun onResponse(call: Call<AddIncResponse>, response: Response<AddIncResponse>) {
                    if (response.isSuccessful) {
                        if (response.body() != null && response.body()!!.data != null && response.body()!!.isSuccess) {
                            StaticMembers.toastMessageLong(activity, "تم ارسال الرسالة بنجاح")
                            dismiss()
                        }
                        else {
                            StaticMembers.toastMessageShort(activity, R.string.connection_error)
                        }
                    }
                    else {
                        StaticMembers.toastMessageShort(activity, R.string.connection_error)
                    }
                }

            })
        }
    }
}
