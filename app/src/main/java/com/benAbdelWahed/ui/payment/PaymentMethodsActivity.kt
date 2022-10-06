package com.benAbdelWahed.ui.payment

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.FragmentActivity
import com.benAbdelWahed.R
import com.benAbdelWahed.models.Method
import com.benAbdelWahed.utils.StaticMembers
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_payment_methods.*
import java.io.Serializable

class PaymentMethodsActivity : FragmentActivity() {
    val list = ArrayList<Method>()
    lateinit var adapter: MethodsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_methods)
        intent.getStringArrayListExtra(SELECT_PAYMENT)?.forEach {
            list.add(Method(it))
        }
        adapter = MethodsAdapter(list)
        adapter.selectMethod(intent.getStringExtra(SELECTED_METHOD) ?: "")
        recycler.adapter = adapter
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        ok.setOnClickListener {
            Intent().apply {
                putExtra(StaticMembers.PAYMENT, adapter.getSelectedItem())
                setResult(RESULT_OK, this)
                onBackPressed()
            }
            actionListener?.onAction(adapter.getSelectedItem())
        }
    }

    interface ActionListener : Serializable {
        fun onAction(method: Method?)
    }

    companion object {
        private const val SELECT_PAYMENT = "selectedPayments"
        private const val SELECTED_METHOD = "selectedMethod"
        private const val ACTION_ = "action"
        private const val PAYMENT_CODE = 148
        private var  actionListener: ActionListener ? = null

        fun openActivity(activity: FragmentActivity, list: ArrayList<String>, selectedMethod: Method?) {
            Intent(activity, PaymentMethodsActivity::class.java).apply {
                putExtra(SELECT_PAYMENT, list)
                putExtra(SELECTED_METHOD, selectedMethod?.key)
                activity.startActivityForResult(this, PAYMENT_CODE)
            }
        }

        fun openActivity(context: Context, list: ArrayList<String>, selectedMethod: Method?, action: ActionListener) {
            Intent(context, PaymentMethodsActivity::class.java).apply {
                putExtra(SELECT_PAYMENT, list)
                putExtra(SELECTED_METHOD, selectedMethod?.key)
                actionListener = action
                flags = FLAG_ACTIVITY_NEW_TASK
                context.startActivity(this)
            }
        }

        fun isResultOk(requestCode: Int, resultCode: Int) =
                (requestCode == PAYMENT_CODE && resultCode == RESULT_OK)

        fun getSelectedPayment(data: Intent?): Method? {
            return data?.getParcelableExtra(StaticMembers.PAYMENT)
        }
    }
}
