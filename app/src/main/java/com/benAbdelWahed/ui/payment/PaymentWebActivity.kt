package com.benAbdelWahed.ui.payment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.benAbdelWahed.R
import com.benAbdelWahed.utils.StaticMembers
import kotlinx.android.synthetic.main.activity_web_payment.*
import java.io.Serializable

class PaymentWebActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_payment)
        setupToolbar()
        progressBar.visibility = View.VISIBLE
        progressBar.progress = 0
        webView.apply {
            settings.javaScriptEnabled = true

            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        progressBar.setProgress(newProgress, true)
                    } else progressBar.progress = newProgress
                    if (newProgress == 100)
                        progressBar.visibility = View.GONE
                    else
                        progressBar.visibility = View.VISIBLE
                }
            }

            webViewClient = object : WebViewClient() {
                override fun doUpdateVisitedHistory(view: WebView, url: String, isReload: Boolean) {
                    super.doUpdateVisitedHistory(view, url, isReload)
                    if (url.contains("success")) {
                        setResult(RESULT_OK)
                        finish()
                        actionListener?.onAction(true)
                    } else if (url.contains("failure")) {
                        finish()
                        actionListener?.onAction(false)
                    }
                }
            }

            if (savedInstanceState == null) {
                val url = intent.getStringExtra(StaticMembers.DATA)
                loadUrl(url ?: "")
            }
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        toolbar.setTitle(R.string.payment_platform)
    }

    interface ActionListener : Serializable {
        fun onAction(isSuccess:Boolean)
    }

    companion object {
        private const val PAYMENT_CODE = 150
        private const val ACTION_ = "action"
        var actionListener: ActionListener? = null

        fun openActivity(activity: FragmentActivity, url: String,action:ActionListener?= null) {
            Intent(activity, PaymentWebActivity::class.java).apply {
                putExtra(StaticMembers.DATA, url)
                actionListener = action
                activity.startActivityForResult(this, PAYMENT_CODE)
            }
        }

        fun isResultOk(requestCode: Int, resultCode: Int) =
                (requestCode == PAYMENT_CODE && resultCode == RESULT_OK)

        fun isResultNotOk(requestCode: Int, resultCode: Int) =
                (requestCode == PAYMENT_CODE && resultCode != RESULT_OK)

        fun isItsRequest(requestCode: Int) =
                (requestCode == PAYMENT_CODE)

    }

}
