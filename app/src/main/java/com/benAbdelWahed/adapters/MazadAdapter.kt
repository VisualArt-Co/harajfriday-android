package com.benAbdelWahed.adapters

import android.content.Intent
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.benAbdelWahed.R
import com.benAbdelWahed.activities.EditProfileActivity
import com.benAbdelWahed.activities.MazadDetailsActivity
import com.benAbdelWahed.fragments.PlaceBidDialog
import com.benAbdelWahed.fragments.SubscribeDialog
import com.benAbdelWahed.models.Method
import com.benAbdelWahed.responses.add_inc_response.AddIncResponse
import com.benAbdelWahed.responses.haraj_responses.Customer
import com.benAbdelWahed.responses.mazads_response.DataItem
import com.benAbdelWahed.responses.payment.BuyResponse
import com.benAbdelWahed.responses.payment.BuyWalletResponse
import com.benAbdelWahed.ui.membership.MembershipActivity
import com.benAbdelWahed.ui.payment.PaymentFromWalletActivity
import com.benAbdelWahed.ui.payment.PaymentMethodsActivity
import com.benAbdelWahed.ui.payment.PaymentWebActivity
import com.benAbdelWahed.ui.wallet.WalletActivity
import com.benAbdelWahed.utils.AlertUtil
import com.benAbdelWahed.utils.CallbackRetrofit
import com.benAbdelWahed.utils.PrefManager
import com.benAbdelWahed.utils.RetrofitModel
import com.benAbdelWahed.utils.StaticMembers.*
import com.benAbdelWahed.utils.Utils.showToast
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_bid_card.view.*
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class MazadAdapter(private val activity: FragmentActivity, private val progress: RelativeLayout?, private val list: List<DataItem>, private val type: Int, private val onActionListener: OnActionListener) : RecyclerView.Adapter<MazadAdapter.Holder>() {
    private var cancelTimer: Boolean = false
    private val prefManager: PrefManager = PrefManager.getInstance(activity)
    private var isPremium = prefManager.getBoolean(IS_PREMIUM)
    private var myUser = prefManager.getObject(PROFILE, Customer::class.java) as Customer?

    private var subscribeDialog: SubscribeDialog? = null

    private var alertDialog: AlertDialog? = null

    private var alertDialog2: AlertDialog? = null

    internal fun showProgress() {
        if (progress != null)
            progress.visibility = View.VISIBLE
    }

    internal fun hideProgress() {
        if (progress != null)
            progress.visibility = View.GONE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(activity).inflate(R.layout.item_bid_card, parent, false))
    }

    fun changeAllCountDownTimers(cancelTimer: Boolean) {
        this.cancelTimer = cancelTimer
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    internal fun subscribeToMazad(subscribe: CardView?, item: DataItem, paymentMethodKey: String) {
        if (paymentMethodKey == paymentEnum.pay_by_wallet.name)
            subscribeToMazadWallet(subscribe, item, paymentMethodKey)
        else
            subscribeToMazadOther(subscribe, item, paymentMethodKey)
    }

    internal fun subscribeToMazadWallet(subscribe: CardView?, item: DataItem, paymentMethod: String) {
        showProgress()
        val call = RetrofitModel.getApi(activity).subscribeToMazadWallet(paymentMethod, item.id)
        call.enqueue(object : CallbackRetrofit<BuyWalletResponse>(activity) {
            override fun onResponse(call: Call<BuyWalletResponse>, response: Response<BuyWalletResponse>) {
                hideProgress()
                val result = response.body()
                if (response.isSuccessful && result != null) {
                    doneSubscribe(subscribe, item)
                } else {
                    useError(response, subscribe, item)
                }

            }

            override fun onFailure(call: Call<BuyWalletResponse>, t: Throwable) {
                super.onFailure(call, t)
                activity.showToast(R.string.connection_error)
                hideProgress()
            }
        })
    }

    internal fun subscribeToMazadOther(subscribe: CardView?, item: DataItem, paymentMethod: String) {
        showProgress()
        val call = RetrofitModel.getApi(activity).subscribeToMazad(paymentMethod, item.id)
        call.enqueue(object : CallbackRetrofit<BuyResponse>(activity) {
            override fun onResponse(call: Call<BuyResponse>, response: Response<BuyResponse>) {
                hideProgress()
                val result = response.body()
                if (response.isSuccessful && result != null) {
                    if (result.data.payment_link.isNullOrEmpty()) {
                        doneSubscribe(subscribe, item)
                    } else {
                        PaymentWebActivity.openActivity(activity, result.data.payment_link, getPaymentWebActivityListener(subscribe, item))
                    }
                } else {
                    useError(response, subscribe, item)
                }

            }

            override fun onFailure(call: Call<BuyResponse>, t: Throwable) {
                super.onFailure(call, t)
                activity.showToast(R.string.connection_error)
                hideProgress()
            }
        })
    }

    private fun doneSubscribe(subscribe: CardView?, item: DataItem) {
        toastMessageShort(activity, R.string.success)
        if (item.guaranteePrice == 0)
            item.isSubscribedInMazad = true
        else
            item.paymentStatus = "pendding"
        if (subscribe != null)
            subscribe.isEnabled = true
        updateAdapter()
        onActionListener.onRefresh()
    }

    private fun getPaymentWebActivityListener(subscribe: CardView?, item: DataItem): PaymentWebActivity.ActionListener {
        return object : PaymentWebActivity.ActionListener {
            override fun onAction(isSuccess: Boolean) {
                if (isSuccess)
                    doneSubscribe(subscribe, item)
                else activity.showToast(R.string.payment_failed)
            }
        }
    }

    private fun useError(response: Response<*>, subscribe: CardView?, item: DataItem) {
        val error = getError(response.errorBody()?.string())
        if (error.errorCode == "0x419")
            showNeedPremiumSubscriptionError(subscribe, error.message)
        else if (error.errorCode == "0x424")
            showWalletPageError(subscribe, error.message, item.startPrice)
        else checkError(activity, error)
    }

    internal fun addIncreaseToMazad(mazad: DataItem, price: Int) {

        if (mazad.leastPrice != null && mazad.mostPrice != null) {
            val least = Integer.parseInt(mazad.leastPrice)
            val most = Integer.parseInt(mazad.mostPrice)
            if (price > most || price < least) {
                toastMessageShort(activity, String.format(Locale.getDefault(), activity.getString(R.string.req_less_most_price), least, most))
                return
            }
        }
        showProgress()
        val call = RetrofitModel.getApi(activity).addIncrement(mazad.id, price)
        call.enqueue(object : CallbackRetrofit<AddIncResponse>(activity) {
            override fun onResponse(call: Call<AddIncResponse>, response: Response<AddIncResponse>) {
                hideProgress()
                val result = response.body()
                if (response.isSuccessful && result != null) {
                    toastMessageShort(activity, R.string.success)
                    onActionListener.onRefresh()
                } else {
                    useError(response, null, mazad)
                }

            }

            override fun onFailure(call: Call<AddIncResponse>, t: Throwable) {
                super.onFailure(call, t)
                hideProgress()
            }
        })
    }

    private fun showSubscribeDialog(subscribe: CardView, item: DataItem) {
        if (subscribeDialog != null && subscribeDialog!!.isVisible)
            subscribeDialog!!.dismiss()
        subscribeDialog = SubscribeDialog(item.guaranteePrice) {
            showPayGuaranteePriceDialog(subscribe, item)
        }
        subscribeDialog!!.show(activity.supportFragmentManager, "subscribeDialog")
    }

    private fun getPaymentMethodsListener(subscribe: CardView, item: DataItem): PaymentMethodsActivity.ActionListener {
        return object : PaymentMethodsActivity.ActionListener {
            override fun onAction(method: Method?) {
                if (method?.key == paymentEnum.pay_by_wallet.name) {
                    PaymentFromWalletActivity.openActivity(activity,
                            PaymentFromWalletActivity.PayFor.mazad.name,
                            item.guaranteePrice.toFloat(), 1, getPaymentFromWalletListener(subscribe, item, method.key))
                } else
                    subscribeToMazad(subscribe, item, method?.key ?: "")
            }
        }
    }

    private fun getPaymentFromWalletListener(subscribe: CardView, item: DataItem, key: String): PaymentFromWalletActivity.ActionListener? {
        return object : PaymentFromWalletActivity.ActionListener {
            override fun onAction() {
                subscribeToMazad(subscribe, item, key)
            }
        }
    }

    private fun showNeedPremiumSubscriptionError(button: CardView?, s: String?) {
        if (alertDialog != null && alertDialog!!.isShowing)
            alertDialog!!.dismiss()
        val builder = AlertDialog.Builder(activity)
        val isPendingPremium = myUser!!.checkPremuimStatus == PENDDING
        builder.setMessage(getTextHTML(s ?: activity.getString(R.string.mazad_for_premium_only_)))
        if (isPendingPremium) {
            builder.setPositiveButton(activity.getString(R.string.ok), null)
        } else {
            builder.setPositiveButton(activity.getString(R.string.subscribe)) { dialog, which -> showSubscribePremiumDialog() }
            builder.setNegativeButton(activity.getString(R.string.cancel), null)
        }
        alertDialog = builder.show()
        alertDialog!!.setOnDismissListener { dialog ->
            if (button != null)
                button.isEnabled = true
        }
    }

    private fun showSubscribePremiumDialog() {
        MembershipActivity.openActivity(activity, getMembershipListener())
    }

    private fun getMembershipListener(): MembershipActivity.ActionListener {
        return object : MembershipActivity.ActionListener {
            override fun onAction() {
                updateAdapter()
            }
        }
    }

    fun updateAdapter() {
        isPremium = prefManager.getBoolean(IS_PREMIUM)
        myUser = prefManager.getObject(PROFILE, Customer::class.java) as Customer?
        notifyDataSetChanged()
    }

    private fun showWalletPageError(button: CardView?, s: String?, startPrice: String) {
        if (alertDialog != null && alertDialog!!.isShowing)
            alertDialog!!.dismiss()
        val builder = AlertDialog.Builder(activity)
        builder.setMessage(getTextHTML(s).toString() + String.format(activity.getString(R.string.req_wallet_is_d), startPrice))
        builder.setPositiveButton(activity.getString(R.string.ok)) { dialog, which -> showWalletPage() }
        builder.setNegativeButton(activity.getString(R.string.cancel), null)
        alertDialog = builder.show()
        alertDialog!!.setOnDismissListener { dialog ->
            if (button != null)
                button.isEnabled = true
        }
    }

    private fun showWalletPage() {
        activity?.startActivity(Intent(activity, WalletActivity::class.java))
    }

    private fun showCannotAddBid() {
        if (alertDialog2 != null && alertDialog2!!.isShowing)
            alertDialog2!!.dismiss()
        val builder = AlertDialog.Builder(activity)
        builder.setMessage(R.string.cannot_add_bid)
        builder.setPositiveButton(activity.getString(R.string.cancel), null)
        alertDialog2 = builder.show()
    }


    private fun showPendingAlert(s: String) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(R.string.alert)
        builder.setMessage(getTextHTML(s))
        builder.setPositiveButton(activity.getString(R.string.ok), null)
        builder.show()
    }


    private fun showNameTriAlert() {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(R.string.alert)
        builder.setMessage(R.string.alert_name_tri)
        builder.setNegativeButton(activity.getString(R.string.cancel), null)
        builder.setPositiveButton(activity.getString(R.string.ok)) { dialog, which -> activity.startActivityForResult(Intent(activity, EditProfileActivity::class.java), EDIT_PROFILE_CODE) }
        builder.show()
    }

    private fun showPlaceBidDialog(item: DataItem) {
        val placeBidDialog = PlaceBidDialog(if (item.closeingPrice != null && item.closeingPrice != "0") item.closeingPrice else item.startPrice) { bid ->
            hideKeyboard(activity)
            addIncreaseToMazad(item, bid)
        }
        placeBidDialog.show(activity.supportFragmentManager, "placeBid")
    }


    private fun showMazadEndedDialog(item: DataItem) {
        if (item.winner != null) {
            toastMessageLong(activity, String.format(Locale.getDefault(), activity.getString(R.string.mazad_has_ended), item.winner.fullName))
        }
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var countDownTimer: CountDownTimer? = null

        fun bind() = with(itemView) {
            val p = adapterPosition
            val item = list[p]

            itemView.setOnClickListener { v ->
                val intent = Intent(activity, MazadDetailsActivity::class.java)
                intent.putExtra(MAZAD, item)
                activity.startActivityForResult(intent, EDIT_CODE)
            }
            name.text = item.name
            mazadId.text = "#${item.id}"
            timerText.text = ""
            description.text = item.description
            if (item.images != null && item.images.size > 0)
                Glide.with(activity).load(item.images[0].image).centerCrop().placeholder(R.drawable.place_holder_logo).error(R.drawable.place_holder_logo).into(image)
            subscribe!!.setOnClickListener { v ->
                if (!prefManager.apiToken.isEmpty()) {
                    val ss = myUser!!.fullName.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    if (ss.size < 3) {
                        showNameTriAlert()
                        return@setOnClickListener
                    }
                    subscribe!!.isEnabled = false
                    showProgress()
                    object : CountDownTimer(5000, 1000) {
                        override fun onTick(millisUntilFinished: Long) {}

                        override fun onFinish() {
                            hideProgress()
                            subscribe!!.isEnabled = true
                            if (item.isPending) {
                                val s = if (item.mazadType == PUBLIC) "طلبك للأشتراك في المزاد قيد المراجعة" else "طلبك للأشتراك في العضوية المميزة قيد المراجعة"
                                showPendingAlert(s)
                            } else {
                                if (item.guaranteePrice > 0 && !isPremium) {
                                    showSubscribeDialog(subscribe, item)
                                } else {
                                    subscribeToMazad(subscribe, item, paymentEnum.pay_by_wallet.name)
                                }
                            }

                        }
                    }.start()
                } else {
                    showLoginDialog(activity)
                }
            }
            subscribe!!.visibility = if (type == 1) View.VISIBLE else View.GONE
            addBid!!.visibility = if (type == 0) View.VISIBLE else View.GONE

            subscribe!!.isEnabled = false

            if (item.isSubscribedInMazad) {
                subscribeText!!.setText(R.string.done_sub)
                subscribe!!.setCardBackgroundColor(activity.resources.getColor(R.color.green))
            } else if (item.paymentStatus == null) {
                subscribeText!!.setText(R.string.subscribe)
                subscribe!!.setCardBackgroundColor(activity.resources.getColor(R.color.colorAccent))
                subscribe!!.isEnabled = true
            }

            addBid!!.setOnClickListener { v ->
                if (!prefManager.apiToken.isEmpty()) {
                    if (item.isSubscribedInMazad || isPremium)
                        showPlaceBidDialog(item)
                    else
                        showCannotAddBid()
                } else {
                    showLoginDialog(activity)
                }

            }

            val calendarNow = Calendar.getInstance(TimeZone.getTimeZone("GMT+3"))
            val calendarTime = Calendar.getInstance(TimeZone.getTimeZone("GMT+3"))

            if (countDownTimer != null) {
                countDownTimer!!.cancel()
            }
            when (type) {
                0 -> {
                    //Running Case:
                    //if user is login
                    highestPrice!!.text = getTextHTML(String.format(Locale.getDefault(), activity.getString(R.string.highest_price), item.closeingPrice))
                    winnerName!!.visibility = View.VISIBLE
                    if (item.winner != null) {
                        winnerName!!.text = item.winner.fullName
                    } else {
                        winnerName!!.text = ""
                    }
                    if (item.endIncreasingTime != null)
                        try {
                            calendarTime.time = SimpleDateFormat(DATE_FORMAT_BACKEND, Locale.US).parse(item.endIncreasingTime)!!
                            val timeDiff = calendarTime.timeInMillis - calendarNow.timeInMillis + BACKEND_DELAY_VAL
                            if (timeDiff > 0) {

                                countDownTimer = object : CountDownTimer(timeDiff, 1000) {
                                    override fun onFinish() {
                                        if (!cancelTimer && item.closeingPrice != null && item.closeingPrice.isNotEmpty()) {
                                            if (item.closeingPrice.toInt() >= item.minPrice.toInt())
                                                showMazadEndedDialog(item)
                                            onActionListener.onRefresh()
                                        }
                                    }

                                    override fun onTick(millisUntilFinished: Long) {
                                        timerText.text = getTextHTML(String.format(Locale.getDefault(), activity.getString(R.string.end_at), getTimeForCounter(millisUntilFinished)))
                                    }
                                }
                                countDownTimer!!.start()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                }
                1 -> {
                    //Comming case
                    highestPrice!!.text = getTextHTML(String.format(Locale.getDefault(), activity.getString(R.string.start_price), item.startPrice))
                    winnerName!!.visibility = View.GONE
                    if (item.startDateTime != null)
                        try {
                            calendarTime.time = SimpleDateFormat(DATE_FORMAT_BACKEND, Locale.US).parse(item.startDateTime)!!
                            val timeDiff = calendarTime.timeInMillis - calendarNow.timeInMillis + BACKEND_DELAY_VAL
                            if (timeDiff > 0) {
                                countDownTimer = object : CountDownTimer(timeDiff, 1000) {
                                    override fun onFinish() {
                                        if (!cancelTimer)
                                            onActionListener.onRefresh()
                                    }

                                    override fun onTick(millisUntilFinished: Long) {
                                        timerText.text = getTextHTML(String.format(Locale.getDefault(), activity.getString(R.string.start_at), getTimeForCounter(millisUntilFinished)))
                                    }
                                }
                                countDownTimer!!.start()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                }
                2 -> {
                    if (item.winner != null) {
                        winnerName!!.visibility = View.VISIBLE
                        winnerName!!.text = item.winner.fullName
                    }
                    if (item.endIncreasingTime != null)
                        timerText!!.text = getTextHTML(String.format(Locale.getDefault(), activity.getString(R.string.finished_at), item.endIncreasingTime))
                    if (item.closeingPrice != null && item.closeingPrice != "0")
                        highestPrice!!.text = getTextHTML(String.format(Locale.getDefault(), activity.getString(R.string.closing_price), item.closeingPrice))
                }
            }
        }
    }

    private fun showPayGuaranteePriceDialog(subscribe: CardView, mazad: DataItem) {
        AlertUtil(activity).apply {
            message(String.format(activity.getString(R.string.terms_of_guarantee_s), mazad.guaranteePrice))
            positiveButton(R.string.ok) { _, _ ->
                PaymentMethodsActivity.openActivity(activity, mazad.payment_method_allowed, null, getPaymentMethodsListener(subscribe, mazad))
            }
            negativeButton(R.string.cancel, null)
            show()
        }
    }

    interface OnActionListener {
        fun onRefresh()
    }
}
