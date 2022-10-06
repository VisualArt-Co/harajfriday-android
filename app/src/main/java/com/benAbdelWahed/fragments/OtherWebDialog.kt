package com.benAbdelWahed.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.DialogFragment
import com.benAbdelWahed.R
import com.benAbdelWahed.responses.settings_response.SettingsData
import com.benAbdelWahed.utils.PrefManager
import com.benAbdelWahed.utils.StaticMembers.SETTINGS
import kotlinx.android.synthetic.main.fg_other_web.*

class OtherWebDialog : DialogFragment() {
    private var prefManager: PrefManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.DialogTrans)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fg_other_web, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefManager = PrefManager.getInstance(context)
        backButton!!.setOnClickListener { v -> dismiss() }
        titleText!!.setText(R.string.black_list)
        val data = prefManager!!.getObject(SETTINGS, SettingsData::class.java) as SettingsData
        webView.settings.javaScriptEnabled = true
        progressBar.visibility = View.VISIBLE
        progressBar.progress = 0
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    progressBar.setProgress(newProgress, true)
                } else progressBar.progress = newProgress
                if (newProgress == 100)
                    progressBar.visibility = View.GONE
            }
        }
        webView.webViewClient = WebViewClient()
        webView.loadUrl(data.other)
    }

}
