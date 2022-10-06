package com.benAbdelWahed.fragments

import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.InputFilter
import android.text.TextWatcher
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.benAbdelWahed.R
import com.benAbdelWahed.models.PaymentModel
import com.benAbdelWahed.responses.add_inc_response.AddIncResponse
import com.benAbdelWahed.responses.bank_responses.BankResponse
import com.benAbdelWahed.responses.error_response.ErrorLoginResponse
import com.benAbdelWahed.utils.CallbackRetrofit
import com.benAbdelWahed.utils.PrefManager
import com.benAbdelWahed.utils.RetrofitModel
import com.benAbdelWahed.utils.StaticMembers.*
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fg_calculate.*
import retrofit2.Call
import retrofit2.Response
import java.util.*
import java.util.regex.Pattern

class CalculateDialog : DialogFragment() {

    private lateinit var prefManager: PrefManager
    private var data: BankResponse? = null
    private val commissionInfo = "<h2>عمولة حراج الجمعة</h2> <p>العمولة أمانة في ذمة المعلن سواء تمت المبايعة عن طريق الموقع أو بسببه، وموضحة قيمتها بما يلي:<h4>عمولة السيارات:</h4><ul><li><strong>بيع السيارات:</strong><span>1% من قيمة السيارة.</span></li><li><strong>سيارات للتنازل:</strong><span>1% من قيمة التنازل إذا كان التنازل بمقابل.</span></li><li><strong>تبادل السيارات:</strong><span>1% من قيمة المبادلة إذا كان هناك مقابل للمبادلة.</span></li></ul><h4>عمولة العقارات:</h4><ul><li><strong> بيع عقار عن طريق المالك:</strong><span> 1% من قيمة العقار.</span></li><li><strong> بيع عقار عن طريق وسيط:</strong><span>يعتبر الموقع شريك في الوساطة ويقتسم السعي مع الوسطاء بالتساوي ، أو يدفع المعلن للموقع 1% من قيمة العقار.</span></li><li><strong>تأجير عقارات:</strong><span>1% من قيمة عقد الإيجار الجديد فقط ، وليس العقد المجدد لنفس الشخص.</span></li></ul><h4>عمولة السلع و الخدمات الأخرى:</h4><ul><li><strong>بيع سلعة:</strong><span>1% من قيمة السلعة المباعة</span></li><li><strong>تأجير سلع(معدات وغيرها):</strong><span>1% من قيمة مبلغ الإيجار.</span></li><li><strong> تقديم خدمات:</strong><span> 1% من قيمة الخدمة المقدمة. وفي حالة تقديم الخدمة لأكثر من شخص فيتم تحويل 1% عن كل خدمة تم تقديمها.</span></li><li><strong>طلب سلعة أو خدمة:</strong><span> 1% من قيمة المبايعة.</span></li></ul><strong>إذا كانت قيمة العمولة أقل من 20 ريال سعودي ، فيمكن التصدق بها إلى أحد الجهات الخيرية الرسمية نيابة عن الموقع.</strong></p>"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefManager = PrefManager.getInstance(context)
        backButton.setOnClickListener { dismiss() }
        data = prefManager.getObject(BANK_ACCOUNTS, BankResponse::class.java) as BankResponse?
        commissionInfoTextView.text = Html.fromHtml(commissionInfo)
        commissionEditText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(7))
        priceResult.text = String.format(Locale.US, getString(R.string.commission_count), 0)
        commissionEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s != null && s.isNotEmpty()) {
                    val priceVal = Integer.parseInt(s.toString())
                    val commission = (priceVal * 0.01).toInt()
                    priceResult.text = String.format(Locale.US, getString(R.string.commission_count), commission)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        updateBankAccounts()
        getBankAccounts()

        send.setOnClickListener {
            sendPaymentData()
        }
    }

    fun updateBankAccounts() {
        if (data != null && data!!.data.isNotEmpty()) {
            bankAccounts.text = ""
            data!!.data.forEach {
                bankAccounts.text = String.format(Locale.US, getString(R.string.bank_number), bankAccounts.text.toString(), it.name, it.accountNumber)
            }
            val filter = Linkify.TransformFilter { _, url -> url.replace("/".toRegex(), "") }
            val pattern = Pattern.compile("\\+?\\d+")
            Linkify.addLinks(bankAccounts, pattern, "", null, filter)
        }
    }

    fun getBankAccounts() {
        updateBankAccounts()
        val call = RetrofitModel.getApi(context).bankAccounts
        call.enqueue(object : CallbackRetrofit<BankResponse>(context) {
            override fun onResponse(call: Call<BankResponse>, response: Response<BankResponse>) {
                if (response.isSuccessful) {
                    if (response.body() != null && response.body()!!.data != null) {
                        data = response.body()
                        prefManager.setObject(BANK_ACCOUNTS, data)
                        updateBankAccounts()
                    }
                }
            }

        })
    }


    fun sendPaymentData() {
        checkTextInputEditText(usernameEditText, usernameLayout, getString(R.string.username_req))
        checkTextInputEditText(bankNoEditText, bankNoLayout, getString(R.string.bankNo_req))
        checkTextInputEditText(commissionValueEditText, commissionValueLayout, getString(R.string.commission_val_req))
        checkTextInputEditText(senderNameEditText, senderNameLayout, getString(R.string.sender_name_req))
        checkTextInputEditText(phoneEditText, phoneLayout, getString(R.string.phone_req))
        checkTextInputEditText(productIdEditText, productIdLayout, getString(R.string.product_id_req))

        if (
                checkTextInputEditText(usernameEditText, usernameLayout, getString(R.string.username_req)) &&
                checkTextInputEditText(bankNoEditText, bankNoLayout, getString(R.string.bankNo_req)) &&
                checkTextInputEditText(commissionValueEditText, commissionValueLayout, getString(R.string.commission_val_req)) &&
                checkTextInputEditText(senderNameEditText, senderNameLayout, getString(R.string.sender_name_req)) &&
                checkTextInputEditText(phoneEditText, phoneLayout, getString(R.string.phone_req)) &&
                checkTextInputEditText(productIdEditText, productIdLayout, getString(R.string.product_id_req))
        ) {


            val paymentModel = PaymentModel(usernameEditText.text.toString(),
                    senderNameEditText.text.toString(),
                    phoneEditText.text.toString(),
                    messageEditText.text.toString(),
                    bankNoEditText.text.toString(),
                    commissionValueEditText.text.toString(),
                    productIdEditText.text.toString())
            val call = RetrofitModel.getApi(context).sendPayment(paymentModel)
            call.enqueue(object : CallbackRetrofit<AddIncResponse>(context) {
                override fun onResponse(call: Call<AddIncResponse>, response: Response<AddIncResponse>) {
                    if (response.isSuccessful) {
                        if (response.body() != null && response.body()!!.data != null && response.body()!!.isSuccess) {
                            toastMessageLong(activity, "تم ارسال بيانات الدفع بنجاح")
                            dismiss()
                        } else {
                            toastMessageShort(activity, R.string.connection_error)
                        }
                    }
                    else {
                        try {
                            val errorResponse: ErrorLoginResponse = GsonBuilder().create().fromJson(response.errorBody()!!.string(), ErrorLoginResponse::class.java)
                            if (errorResponse != null) {
                                if (errorResponse.error != null && errorResponse.error.toLowerCase().contains("unauthenticated")) showLoginDialog(activity) else {
                                    if (errorResponse.message != null && !errorResponse.message.trim { it <= ' ' }.isEmpty()) {
                                        toastMessageShort(activity, errorResponse.message)
                                    }
                                    if (errorResponse.data != null && !errorResponse.data.username.isNullOrEmpty()) toastMessageShort(activity, errorResponse.data.username[0])
                                    if (errorResponse.data != null && !errorResponse.data.product_id.isNullOrEmpty()) toastMessageShort(activity, errorResponse.data.product_id[0])
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            toastMessageShort(activity, R.string.connection_error)
                        }
                    }
                }

            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.DialogTrans)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fg_calculate, container, false)
    }

}
