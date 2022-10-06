package com.benAbdelWahed.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.benAbdelWahed.R
import com.benAbdelWahed.adapters.ImagePagerAdapter
import com.benAbdelWahed.adapters.SubscribersAdapter
import com.benAbdelWahed.fragments.SubscribeDialog
import com.benAbdelWahed.responses.add_inc_response.AddIncResponse
import com.benAbdelWahed.responses.haraj_responses.Customer
import com.benAbdelWahed.responses.mazads_response.DataItem
import com.benAbdelWahed.responses.mazads_response.MazadDetailsResponse
import com.benAbdelWahed.responses.payment.BuyResponse
import com.benAbdelWahed.responses.payment.BuyWalletResponse
import com.benAbdelWahed.responses.subscribers_response.SubscribersResponse
import com.benAbdelWahed.ui.membership.MembershipActivity
import com.benAbdelWahed.ui.payment.PaymentFromWalletActivity
import com.benAbdelWahed.ui.payment.PaymentMethodsActivity
import com.benAbdelWahed.ui.payment.PaymentWebActivity
import com.benAbdelWahed.ui.wallet.WalletActivity
import com.benAbdelWahed.utils.*
import com.benAbdelWahed.utils.StaticMembers.*
import com.benAbdelWahed.utils.Utils.showToast
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_mazad_details.*
import kotlinx.android.synthetic.main.item_bid_card.view.*
import kotlinx.android.synthetic.main.progress_layout.*
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MazadDetailsActivity : AppCompatActivity() {

    private var isPremium = false
    private var mostBidValue: Int = 0
    private lateinit var subscriberList: ArrayList<com.benAbdelWahed.responses.subscribers_response.DataItem>
    private lateinit var subscribersAdapter: SubscribersAdapter
    private var countDownTimer: CountDownTimer? = null
    private lateinit var prefManager: PrefManager
    private var mazadId = 0
    private var mazad: DataItem? = null
    private lateinit var pagerAdapter: ImagePagerAdapter
    private var imageList = ArrayList<String>()

    private lateinit var ref: DatabaseReference
    private lateinit var valueEventListener: ValueEventListener
    private var oldType = 2

    private var myUser: Customer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mazad_details)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        prefManager = PrefManager.getInstance(this)
        myUser = prefManager.getObject(PROFILE, Customer::class.java) as Customer?
        progress.setOnClickListener { }
        isPremium = prefManager.getBoolean(IS_PREMIUM)
        mazad = intent.getSerializableExtra(MAZAD) as DataItem?
        if (mazad != null)
            updateUI()
        else {
            val str = intent.getStringExtra(MAZAD_ID)
            if (str == null) {
                val uri = intent.data
                if (uri != null) {
                    mazadId = Integer.parseInt(uri.pathSegments.last() as String)
                }
            } else {
                mazadId = Integer.parseInt(str)
            }
            progress.visibility = VISIBLE
            getMazadDetails(mazadId)
        }
        subscriberList = ArrayList()
        subscribersAdapter = SubscribersAdapter(this, subscriberList)
        biddersRecycler.adapter = subscribersAdapter
        ref = FirebaseDatabase.getInstance().getReference(MAZADS).child("$mazadId")
    }

    override fun onStart() {
        super.onStart()
        valueEventListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                toastMessageShort(this@MazadDetailsActivity, R.string.connection_error)
            }

            override fun onDataChange(p0: DataSnapshot) {
                getMazadDetails(mazadId)

            }
        }
        ref.addValueEventListener(valueEventListener)
    }

    override fun onPause() {
        super.onPause()
        ref.removeEventListener(valueEventListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (countDownTimer != null)
            countDownTimer!!.cancel()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_PROFILE_CODE || MembershipActivity.isResultOk(requestCode, resultCode)) {
            if (resultCode == Activity.RESULT_OK) {
                myUser = prefManager.getObject(PROFILE, Customer::class.java) as Customer?
                isPremium = prefManager.getBoolean(IS_PREMIUM)
                updateUI()
            }
        }
        if (PaymentMethodsActivity.isResultOk(requestCode, resultCode)) {
            if (PaymentMethodsActivity.getSelectedPayment(data)!!.key!! == paymentEnum.pay_by_wallet.name) {
                PaymentFromWalletActivity.openActivity(this,
                        PaymentFromWalletActivity.PayFor.mazad.name,
                        mazad!!.guaranteePrice.toFloat(), 1,
                        object : PaymentFromWalletActivity.ActionListener {
                            override fun onAction() {
                                callSubscribeToMazad(mazadId, PaymentMethodsActivity.getSelectedPayment(data)!!.key!!)
                            }
                        })
            } else
                callSubscribeToMazad(mazadId, PaymentMethodsActivity.getSelectedPayment(data)!!.key!!)
            return
        }
        if (PaymentWebActivity.isResultOk(requestCode, resultCode)) {
            doneSubscribe()
            updateUI()
        } else if (PaymentWebActivity.isResultNotOk(requestCode, resultCode)) {
            showToast(R.string.payment_failed)
        }
    }

    fun updateUI() {
        if (mazad != null) {
            mazadId = mazad!!.id
            shareImageView.setOnClickListener {
                val shareBody = String.format(Locale.US, "%smazads/%s", StaticMembers.getBaseURL(), mazadId)
                val sharingIntent = Intent(Intent.ACTION_SEND)
                sharingIntent.type = "text/plain"
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name)
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
                startActivity(
                        Intent.createChooser(
                                sharingIntent,
                                resources.getString(R.string.share_using)
                        )
                )
            }
            imageList.clear()
            if (mazad!!.images != null)
                for (s in mazad!!.images) {
                    imageList.add(s.image)
                }
            pagerAdapter = ImagePagerAdapter(this, imagePager, imageList)
            imagePager.adapter = pagerAdapter
            pagerAdapter.notifyDataSetChanged()
            indicators.attachTo(imagePager)

            name.text = mazad!!.name
            description.text = mazad!!.description
            description2.text = mazad!!.description
            typeText.text = "#${mazad!!.id}"

            subscribe.setOnClickListener {
                if (!prefManager.apiToken.isEmpty()) {
                    val ss = myUser!!.fullName.split("\\s+".toRegex())
                    if (myUser != null && ss.size < 3) {
                        showNameTriAlert()
                        return@setOnClickListener
                    }
                    progress.visibility = VISIBLE
                    object : CountDownTimer(5000, 1000) {
                        override fun onTick(millisUntilFinished: Long) {}
                        override fun onFinish() {
                            progress.visibility = GONE
                            if (mazad!!.isPending) {
                                val s = if (mazad!!.mazadType == PUBLIC) "طلبك للأشتراك في المزاد قيد المراجعة" else "طلبك للأشتراك في العضوية المميزة قيد المراجعة"
                                showPendingAlert(s)
                            } else {
                                if (mazad!!.guaranteePrice > 0 && !isPremium) {
                                    showSubscribeDialog(mazad?.id ?: 0)
                                } else {
                                    callSubscribeToMazad(mazad?.id
                                            ?: 0, paymentEnum.pay_by_wallet.name)
                                }
                            }
                        }
                    }.start()

                } else showLoginDialog(this)
            }
            subscribe2.setOnClickListener {
                subscribe.performClick()
            }


            subscribe.visibility = if (mazad!!.status == mazadTypes[1]) VISIBLE else GONE

            addBidUserLayout.visibility = if (mazad!!.status == mazadTypes[0] && (mazad!!.isSubscribedInMazad || isPremium) && prefManager.apiToken.isNotEmpty()) VISIBLE else GONE

            subscribe.isClickable = false

            if (mazad!!.isSubscribedInMazad) {
                subscribeText.setText(R.string.done_sub)
                subscribe.setCardBackgroundColor(resources.getColor(R.color.green))
            } else if (mazad!!.paymentStatus == null) {
                subscribeText.setText(R.string.subscribe)
                subscribe.setCardBackgroundColor(resources.getColor(R.color.colorAccent))
                subscribe.isClickable = true
            }
            subscribe2.visibility = if (mazad!!.status == mazadTypes[1] && !mazad!!.isSubscribedInMazad) VISIBLE else GONE


            addBid.setOnClickListener {
                hideKeyboard(this)
                if (!prefManager.apiToken.isEmpty()) {
                    if (mazad!!.isSubscribedInMazad || isPremium) {
                        if (checkTextInputEditText(addBidText, addBidLayout, getString(R.string.bid_req)))
                            addIncreaseToMazad(Integer.parseInt(addBidText.text.toString()))
                    } else
                        showCannotAddBid()
                } else {
                    showLoginDialog(this@MazadDetailsActivity)
                }

            }
            if (countDownTimer != null) {
                countDownTimer!!.cancel()
            }
            val calendarNow = Calendar.getInstance(TimeZone.getTimeZone("GMT+3"))
            val calendarTime = Calendar.getInstance(TimeZone.getTimeZone("GMT+3"))
            val currentType = when (mazad!!.status) {
                mazadTypes[0] -> 1
                mazadTypes[2] -> 2
                else -> 0
            }

            if (currentType == 2 && oldType == 1) {
                showMazadEndedDialog()
            }

            oldType = currentType

            when (mazad!!.status) {
                mazadTypes[0] -> {
                    maxPriceStart.text = String.format(Locale.getDefault(), getString(R.string.mazad_price_most), mazad!!.closeingPrice)
                    timerText.setTextColor(resources.getColor(R.color.black))
                    remainsCard.setCardBackgroundColor(resources.getColor(R.color.red))
                    remainsText.text = getString(R.string.mazad_finish_remains)
//                    notifications.visibility = VISIBLE
                    subscribersLayout.visibility = VISIBLE
                    mazadNextLayout.visibility = GONE
                    calendarTime.time = SimpleDateFormat(DATE_FORMAT_BACKEND, Locale.US).parse(mazad!!.endIncreasingTime)!!
                    val timeDiff = calendarTime.timeInMillis - calendarNow.timeInMillis + StaticMembers.BACKEND_DELAY_VAL
                    if (timeDiff > 0) {
                        countDownTimer = object : CountDownTimer(timeDiff, 1000) {
                            override fun onFinish() {
                            }

                            override fun onTick(millisUntilFinished: Long) {
                                timerText.text = getTimeForCounter(millisUntilFinished);
                            }
                        }
                        countDownTimer!!.start()
                    }
                    getSubscribers(mazad!!.id)
                }
                mazadTypes[1] -> {
                    minPriceStart.text = String.format(Locale.getDefault(), getString(R.string.mazad_price_start), mazad!!.startPrice)
                    mazadNextLayout.visibility = VISIBLE
                    calendarTime.time = SimpleDateFormat(DATE_FORMAT_BACKEND, Locale.US).parse(mazad!!.startDateTime)!!
                    val timeDiff = calendarTime.timeInMillis - calendarNow.timeInMillis + StaticMembers.BACKEND_DELAY_VAL
                    if (timeDiff > 0) {
                        countDownTimer = object : CountDownTimer(timeDiff, 1000) {
                            override fun onFinish() {
                            }

                            override fun onTick(millisUntilFinished: Long) {
                                timerText.text = getTimeForCounter(millisUntilFinished);
                            }
                        }
                        countDownTimer!!.start()
                    }
                }
                mazadTypes[2] -> {
                    remainsCard.visibility = GONE
                    timerText.visibility = GONE
                    mazadNextLayout.visibility = GONE
                    mazadEndDate.text = String.format(Locale.getDefault(), getString(R.string.mazad_over_after_s), getDateFromBackend(mazad!!.endIncreasingTime))
                    if (mazad!!.winner != null) {
                        winnerLayout.visibility = VISIBLE
                        nameWinner.text = mazad!!.winner.fullName
                        closingPrice.text = String.format(Locale.getDefault(), getString(R.string.mazad_sold_with), mazad!!.closeingPrice)
                    }
                }
            }
        }
    }


    private var alertDialog2: AlertDialog? = null

    private fun showCannotAddBid() {
        if (alertDialog2 != null && alertDialog2!!.isShowing)
            alertDialog2!!.dismiss()
        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.cannot_add_bid)
        builder.setPositiveButton(getString(R.string.cancel), null)
        alertDialog2 = builder.show()
    }

    private fun showMazadEndedDialog() {
        if (mazad!!.winner != null) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(String.format(Locale.getDefault(), getString(R.string.mazad_has_ended), mazad!!.winner.fullName))
            builder.setPositiveButton(getString(R.string.ok)) { _, _ -> finish() }
            builder.show()
        }
        toastMessageShort(this, "انتهى المزاد")
        Log.w("mazad", "انتهى المزاد")
    }

    private var subscribeDialog: SubscribeDialog? = null

    private fun showSubscribeDialog(id: Int) {
        if (subscribeDialog != null && subscribeDialog!!.isVisible)
            subscribeDialog!!.dismiss()
        subscribeDialog = SubscribeDialog(mazad!!.guaranteePrice) {
            showPayGuaranteePriceDialog()
        }
        subscribeDialog!!.show(supportFragmentManager, "subscribeDialog")
    }

    private fun showPayGuaranteePriceDialog() {
        AlertUtil(this).apply {
            message(String.format(getString(R.string.terms_of_guarantee_s), mazad!!.guaranteePrice))
            positiveButton(R.string.ok) { _, _ ->
                PaymentMethodsActivity.openActivity(this@MazadDetailsActivity, mazad!!.payment_method_allowed, null)
            }
            negativeButton(R.string.cancel, null)
            show()
        }
    }


    private fun useError(response: Response<*>, subscribe: CardView?) {
        val error = getError(response.errorBody()?.string())
        if (error.errorCode == "0x419")
            showNeedPremiumSubscriptionError(subscribe, error.message)
        else if (error.errorCode == "0x424")
            showWalletPageError(subscribe, error.message)
        else checkError(this, error)
    }

    private var alertDialog: AlertDialog? = null

    private fun showNeedPremiumSubscriptionError(button: CardView?, s: String?) {
        if (alertDialog != null && alertDialog!!.isShowing)
            alertDialog!!.dismiss()
        val builder = AlertDialog.Builder(this)
        val isPendingPremium = myUser!!.checkPremuimStatus == PENDDING
        builder.setMessage(getTextHTML(s ?: getString(R.string.mazad_for_premium_only_)))
        if (isPendingPremium) {
            builder.setPositiveButton(getString(R.string.ok), null)
        } else {
            builder.setPositiveButton(getString(R.string.subscribe)) { dialog, which -> showSubscribePremiumDialog() }
            builder.setNegativeButton(getString(R.string.cancel), null)
        }
        alertDialog = builder.show()
        alertDialog!!.setOnDismissListener { dialog ->
            if (button != null)
                button.isEnabled = true
        }
    }

    private fun showSubscribePremiumDialog() {
        MembershipActivity.openActivity(this)
    }

    private fun showWalletPageError(button: CardView?, s: String?) {
        if (alertDialog != null && alertDialog!!.isShowing)
            alertDialog!!.dismiss()
        val builder = AlertDialog.Builder(this)
        builder.setMessage(getTextHTML(s).toString() + String.format(getString(R.string.req_wallet_is_d), mazad!!.startPrice))
        builder.setPositiveButton(getString(R.string.ok)) { dialog, which -> showWalletPage() }
        builder.setNegativeButton(getString(R.string.cancel), null)
        alertDialog = builder.show()
        alertDialog!!.setOnDismissListener { dialog ->
            if (button != null)
                button.isEnabled = true
        }
    }

    private fun showWalletPage() {
        startActivity(Intent(this, WalletActivity::class.java))
    }


    private fun showPendingAlert(s: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.alert)
        builder.setMessage(getTextHTML(s))
        builder.setPositiveButton(getString(R.string.ok), null)
        builder.show()
    }


    private fun showNameTriAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.alert)
        builder.setMessage(R.string.alert_name_tri)
        builder.setNegativeButton(getString(R.string.cancel), null)
        builder.setPositiveButton(getString(R.string.ok)) { dialog, which -> startActivityForResult(Intent(this, EditProfileActivity::class.java), EDIT_PROFILE_CODE) }
        builder.show()
    }


    fun getSubscribers(id: Int) {
        val call = RetrofitModel.getApi(this).getSubscribers(id)
        call.enqueue(object : CallbackRetrofit<SubscribersResponse>(this) {
            override fun onResponse(call: Call<SubscribersResponse>, response: Response<SubscribersResponse>) {
                val result = response.body()
                if (response.isSuccessful && result != null) {
                    subscriberList.clear()
                    subscriberList.addAll(result.data)
                    if (subscriberList.isNotEmpty()) {
                        mostBidValue = Integer.parseInt(subscriberList[0].price)
                        noBidders.visibility = GONE
                    } else {
                        noBidders.visibility = VISIBLE
                    }
                    subscribersAdapter.notifyDataSetChanged()
                } else {
                    checkError(this@MazadDetailsActivity, response.errorBody())
                }

            }
        })
    }

    private fun callSubscribeToMazad(id: Int, paymentMethodKey: String) {
        if (paymentMethodKey == paymentEnum.pay_by_wallet.name)
            callSubscribeToMazadWallet(id, paymentMethodKey)
        else
            callSubscribeToMazadOther(id, paymentMethodKey)
    }

    private fun callSubscribeToMazadWallet(id: Int, paymentMethodKey: String) {
        val call = RetrofitModel.getApi(this).subscribeToMazadWallet(paymentMethodKey, id)
        call.enqueue(object : CallbackRetrofit<BuyWalletResponse>(this) {
            override fun onResponse(call: Call<BuyWalletResponse>, response: Response<BuyWalletResponse>) {
                val result = response.body()
                if (response.isSuccessful && result != null) {
                    doneSubscribe()
                } else {
                    useError(response, subscribe)
                }
            }
        })
    }

    private fun callSubscribeToMazadOther(id: Int, paymentMethodKey: String) {
        val call = RetrofitModel.getApi(this).subscribeToMazad(paymentMethodKey, id)
        call.enqueue(object : CallbackRetrofit<BuyResponse>(this) {
            override fun onResponse(call: Call<BuyResponse>, response: Response<BuyResponse>) {
                val result = response.body()
                if (response.isSuccessful && result != null) {
                    if (result.data.payment_link.isNullOrEmpty()) {
                        doneSubscribe()
                    } else {
                        PaymentWebActivity.openActivity(this@MazadDetailsActivity, result.data.payment_link)
                    }
                } else {
                    useError(response, subscribe)
                }

            }
        })
    }

    private fun doneSubscribe() {
        toastMessageShort(this@MazadDetailsActivity, R.string.success)
        subscribe.visibility = GONE
        subscribe2.visibility = GONE
        setResult(Activity.RESULT_OK)
    }

    fun addIncreaseToMazad(price: Int) {
        hideKeyboard(this)
        if (mazad!!.leastPrice != null && mazad!!.mostPrice != null) {
            val least = Integer.parseInt(mazad!!.leastPrice)
            val most = Integer.parseInt(mazad!!.mostPrice)
            if (price > most || price < least) {
                toastMessageShort(this, String.format(Locale.getDefault(), getString(R.string.req_less_most_price), least, most))
                return
            }
        }
        val call = RetrofitModel.getApi(this@MazadDetailsActivity).addIncrement(mazad!!.id, price)
        call.enqueue(object : CallbackRetrofit<AddIncResponse>(this@MazadDetailsActivity) {
            override fun onResponse(call: Call<AddIncResponse>, response: Response<AddIncResponse>) {
                val result = response.body()
                if (response.isSuccessful && result != null) {
                    toastMessageShort(this@MazadDetailsActivity, R.string.success)
                    addBidText.setText("")
                    getSubscribers(mazad!!.id)
                    setResult(Activity.RESULT_OK)
                    getMazadDetails(mazad!!.id)
                } else {
                    useError(response, subscribe)
                }

            }

        })
    }

    fun getMazadDetails(id: Int) {
        val call = RetrofitModel.getApi(applicationContext).getMazadDetails(id)
        call.enqueue(object : CallbackRetrofit<MazadDetailsResponse>(applicationContext) {
            override fun onResponse(call: Call<MazadDetailsResponse>, response: Response<MazadDetailsResponse>) {
                progress.visibility = GONE
                val result = response.body()
                if (response.isSuccessful && result != null && result.data != null) {
                    mazad = result.data
                    updateUI()
                } else {
                    toastMessageShort(applicationContext, R.string.connection_error)
                }
            }

            override fun onFailure(call: Call<MazadDetailsResponse>, t: Throwable) {
                progress.visibility = GONE
            }
        })
    }
}
