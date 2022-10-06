package com.benAbdelWahed.activities

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.benAbdelWahed.R
import com.benAbdelWahed.utils.PrefManager
import com.benAbdelWahed.utils.Utils

class SplashActivity : AppCompatActivity() {
    override fun attachBaseContext(base: Context?) {
        if (base != null) {
            val context = Utils.updateResources(base, "ar")
            super.attachBaseContext(context)
        } else
            super.attachBaseContext(base)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        val displayName = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            resources.configuration.locales[0].language
        } else {
            resources.configuration.locale.language
        }
        if ("ar" != displayName) {
            val context = Utils.updateResources(this, "ar")
            recreate()
            return
        }
        try {
            object : Thread() {
                override fun run() {
                    try {
                        sleep(SPLASH_TIME_OUT.toLong())
                        val i: Intent = if (!PrefManager.getInstance(baseContext).apiToken.isEmpty()) Intent(baseContext, MainActivity::class.java) else Intent(baseContext, LoginActivity::class.java)
                        startActivity(i)
                        finish()
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            }.start()
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
        }
    }

    companion object {
        private const val SPLASH_TIME_OUT = 2000
    }
}