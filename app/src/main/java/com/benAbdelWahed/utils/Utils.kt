package com.benAbdelWahed.utils

import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.telephony.PhoneNumberUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.benAbdelWahed.R
import com.benAbdelWahed.models.Method
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*
import java.util.regex.Pattern

object Utils {
    const val DATA = "data"
    fun Context.openActivity(c: Class<*>) {
        startActivity(Intent(this, c))
    }

    inline fun <reified T : Activity> Activity.openActivity(bundle: Bundle = Bundle()) {
        Intent(baseContext, T::class.java).apply {
            putExtra(DATA, bundle)
            startActivity(this)
        }
    }

    inline fun <reified T : Activity> Activity.openActivityForResult(code:Int, bundle: Bundle = Bundle()) {
        Intent(baseContext, T::class.java).apply {
            putExtra(DATA, bundle)
            startActivityForResult(this,code)
        }
    }

    inline fun <reified T : Activity> Fragment.openActivity(bundle: Bundle = Bundle()) {
        Intent(context, T::class.java).apply {
            putExtra(DATA, bundle)
            startActivity(this)
        }
    }

    fun Intent.getExtraStringFromBundle(key: String): String? {
        return getBundleExtra(DATA)?.getString(key)
    }

    inline fun <reified T : Parcelable> Intent.getExtraObjectFromBundle(key: String): T? {
        return getBundleExtra(DATA)?.getParcelable<T>(key)
    }

    fun Intent.getExtraBooleanFromBundle(key: String, def: Boolean = false): Boolean {
        return getBundleExtra(DATA)?.getBoolean(key) ?: def
    }

    var toast: Toast? = null
    fun Fragment.showToast(resId: Int) {
        context?.run { showToast(resId) }
    }

    fun Fragment.showToast(value: String) {
        context?.run { showToast(value) }
    }

    fun Context.showToast(resId: Int) {
        toast?.cancel()
        toast = Toast.makeText(this, resId, Toast.LENGTH_SHORT).also { it.show() }
    }

    fun Context.showToast(value: String) {
        toast?.cancel()
        toast = Toast.makeText(this, value, Toast.LENGTH_SHORT).also { it.show() }
    }

    fun Fragment.showToastLong(resId: Int) {
        context?.run { showToastLong(resId) }
    }

    fun Context.showToastLong(resId: Int) {
        toast?.cancel()
        toast = Toast.makeText(this, resId, Toast.LENGTH_LONG).also { it.show() }
    }


    var alertDialog: AlertDialog? = null

    fun Fragment.showLoading() {
        activity?.run {
            showLoading()
        }
    }

    fun Context.showLoading() {
        val builder =
                AlertDialog.Builder(this)
        builder.setView(R.layout.progress_layout2)
        alertDialog?.cancel()
        alertDialog = builder.create().apply {
            setCancelable(true)
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        alertDialog?.show()
    }

    fun dismissLoading() {
        alertDialog?.dismiss()
    }

    public fun <T : ViewDataBinding> Activity.getBinding(resId: Int): T {
        return DataBindingUtil.setContentView(this, resId)
    }

    public fun <T : ViewDataBinding> ViewGroup.getBinding(
            resId: Int,
    ): T {
        return getBinding(LayoutInflater.from(context), resId, this)
    }

    public fun ViewGroup.inflate(
            resId: Int
    ): View {
        return LayoutInflater.from(context).inflate(resId, this, false)
    }

    public fun <T : ViewDataBinding> getBinding(
            inflater: LayoutInflater,
            resId: Int,
            container: ViewGroup?,
    ): T {
        return DataBindingUtil.inflate(inflater, resId, container, false)
    }

    public inline fun <reified T : ViewModel> Application.createViewModel(): T {
        return ViewModelProvider.AndroidViewModelFactory.getInstance(this)
                .create(T::class.java)
    }

    public fun <T : ViewModel> Application.createViewModel(c: Class<T>): T {
        return ViewModelProvider.AndroidViewModelFactory.getInstance(this)
                .create(c)
    }

    public inline fun <reified T : ViewModel> Activity.createViewModel(): T {
        return application.createViewModel()
    }

    public fun <T : ViewModel> Activity.createViewModel(c: Class<T>): T {
        return application.createViewModel(c)
    }

    public inline fun <reified T : ViewModel> Fragment.createViewModel(): T {
        return requireActivity().application.createViewModel()
    }

    fun String.isValidEmailOrPhone(): Boolean {
        if (PhoneNumberUtils.isGlobalPhoneNumber(this))
            return true
        val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(this)
        return matcher.matches()
    }

    fun String.isValidEmail(): Boolean {
        val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(this)
        return matcher.matches()
    }

    fun String.isValidPhone(): Boolean {
        return PhoneNumberUtils.isGlobalPhoneNumber(this)
    }

    fun String.concatenatePhoneCode(): String {
        Log.v("Phone", this)
        if (this.startsWith("00971"))
            return this
        if (this.startsWith("+971"))
            return "00" + this.substring(1)
        /*var str = "00971$this"
        if (this[0] == '0')
            str = "00971" + this.substring(1)*/
        return this
    }

    fun String.isValidPassword(): Boolean {
        val expression = "^(?=.{8,}\$)(?=.*[a-z])(?=.*[0-9]).*\$"
        val pattern = Pattern.compile(expression)
        val matcher = pattern.matcher(this)
        return matcher.matches()
    }

    fun updateResources(context: Context, language: String): Context {
        val res: Resources = context.resources
        val config = Configuration(res.configuration)
        // Change locale settings in the app.
        val dm = res.displayMetrics

        config.setLocale(Locale(language))
        res.updateConfiguration(config, dm)
        // Use conf.locale = new Locale(...) if targeting lower versions
        val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales[0]
        } else {
            context.resources.configuration.locale
        }
        Locale.setDefault(locale)

        if (Build.VERSION.SDK_INT >= 24) {
            config.setLocale(locale);
            context.applicationContext.createConfigurationContext(config);
        } else {
            config.locale = locale;
            context.resources.updateConfiguration(config, context.resources.displayMetrics);
        }

        res.updateConfiguration(config, dm)
        return context
    }

    val gson = Gson()

    //convert a data class to a map
    fun <T> T.serializeToMap(): Map<String, Any> {
        return convert()
    }

    //convert a map to a data class
    inline fun <reified T> Map<String, Any>.toDataClass(): T {
        return convert()
    }

    //convert an object of type I to type O
    inline fun <I, reified O> I.convert(): O {
        val json = gson.toJson(this)
        return gson.fromJson(json, object : TypeToken<O>() {}.type)
    }


    fun Any.toJson(): String {
        return Gson().toJson(this)
    }

    fun <T> String.toObject(c: Class<T>): T? {
        try {
            return Gson().fromJson(this, c)
        } catch (e: Exception) {
            return null
        }
    }

    fun getMethodBasedOnKey(item: Method) {
        when (item.key) {
            StaticMembers.paymentEnum.VISA.name -> {
                item.image = R.drawable.ic_icon_metro_visa
                item.textRes = R.string.visa_card
            }
            StaticMembers.paymentEnum.MASTERCARD.name -> {
                item.image = R.drawable.ic_mastercard
                item.textRes = R.string.mastercard_card
            }
            StaticMembers.paymentEnum.MADA.name -> {
                item.image = R.drawable.ic_mada
                item.textRes = R.string.mada_card
            }
            StaticMembers.paymentEnum.pay_by_wallet.name -> {
                item.image = R.drawable.ic_icon_awesome_wallet2
                item.textRes = R.string.wallet_payment
            }
            else -> {
                item.image = R.drawable.ic_icon_ionic_md_cash
                item.textRes = R.string.cash_payment
            }
        }
    }

/*
    inline fun <reified T : Any> T.asMap() : Map<String, Any?> {
        val props = T::class.memberProperties.associateBy { it.name }
        return props.keys.associateWith { props[it]?.get(this) }
    }*/
}