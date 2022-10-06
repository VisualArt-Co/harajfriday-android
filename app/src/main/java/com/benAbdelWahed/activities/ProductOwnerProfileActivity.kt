package com.benAbdelWahed.activities

import android.content.Intent
import android.os.Bundle
import android.view.View.*
import androidx.appcompat.app.AppCompatActivity
import com.benAbdelWahed.R
import com.benAbdelWahed.adapters.HarajAdapter
import com.benAbdelWahed.fragments.ReviewsDialog
import com.benAbdelWahed.responses.categories_response.Category
import com.benAbdelWahed.responses.categories_response.SubCategory
import com.benAbdelWahed.responses.cities_response.City
import com.benAbdelWahed.responses.customer_response.ProfileResponse
import com.benAbdelWahed.responses.error_response.ErrorLoginResponse
import com.benAbdelWahed.responses.haraj_responses.Customer
import com.benAbdelWahed.responses.haraj_responses.DataItem
import com.benAbdelWahed.utils.CallbackRetrofit
import com.benAbdelWahed.utils.PrefManager
import com.benAbdelWahed.utils.RetrofitModel
import com.benAbdelWahed.utils.StaticMembers.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_haraj_details.toolbar
import kotlinx.android.synthetic.main.activity_product_owner_profile.*
import kotlinx.android.synthetic.main.progress_layout.*
import retrofit2.Call
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class ProductOwnerProfileActivity : AppCompatActivity() {

    private var customerId: Int = 0
    private var customer: Customer? = null
    private lateinit var adapter: HarajAdapter
    var myUserProfile: Customer? = null
    private var list = ArrayList<DataItem>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_owner_profile)
        setSupportActionBar(toolbar)

        customer = intent.getSerializableExtra(CUSTOMER) as Customer?
        myUserProfile = PrefManager.getInstance(this).getObject(PROFILE, Customer::class.java) as Customer?

        if (customer == null) {
            customerId = intent.getIntExtra(CUSTOMER_ID, 0)
            progress.visibility = VISIBLE
        } else {
            customerId = customer!!.id
            supportActionBar!!.title = customer!!.user_name
        }
        FirebaseDatabase.getInstance().reference.child(USERS).child("$customerId").child(ACTIVITY).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val isConnectedNow = dataSnapshot.child(IS_ACTIVE).getValue(Boolean::class.java)
                isConnectedNow?.also {
                    isConnected.visibility = if (it) VISIBLE else GONE
                    lastConnected.visibility = if (it) GONE else VISIBLE
                }
                dataSnapshot.child(LAST_ACTIVE_TIME).getValue(String::class.java)?.also {
                    var s = getDateFromBackendForHaraj(it)
                    if (s == "الآن")
                        s = "١ دقيقة"
                    lastConnected.text = String.format(getArabicLocale(), getString(R.string.lastConnected_s), s)
                }
            }
        })
        progress.setOnClickListener { }
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        ratingLayout.setOnClickListener {
            ReviewsDialog.getInstance(customer!!, object : ReviewsDialog.OnActionListener {
                override fun onConfirm() {
                    getProfile()
                }
            }).show(supportFragmentManager, "ReviewsDialog")
        }
        updateUI()
        getProfile()
    }

    private fun updateUI() {
        if (customer != null) {
            if (myUserProfile != null)
                messageUser.visibility = if (customer!!.id == myUserProfile!!.id) INVISIBLE else VISIBLE
            messageUser.setOnClickListener {
                if (myUserProfile != null) {
                    val intent = Intent(this, ChatRoomActivity::class.java)
                    intent.putExtra(OTHER_ID, customer!!.id)
                    startActivity(intent)
                } else showLoginDialog(this)
            }
            name.text = customer!!.user_name
            verifyImage.visibility = if (customer!!.isPremium) VISIBLE else GONE
            isInActive.visibility = if (customer!!.isBlocked) VISIBLE else GONE
            messageUser.isEnabled = (!customer!!.isBlocked)
            messageUser.isClickable = (!customer!!.isBlocked)
            likes.text = String.format(Locale.getDefault(), "%d", customer!!.countOfLike)
            dislikes.text = String.format(Locale.getDefault(), "%d", customer!!.countOfDisLike)
            if (customer!!.products == null)
                customer!!.products = ArrayList()
            list = ArrayList()
            if (customer!!.products != null)
                customer!!.products.forEach {
                    val d = DataItem()
                    d.title = it.title
                    d.id = it.id
                    d.images = ArrayList()
                    if (it.images != null)
                        d.images.addAll(it.images)
                    d.comments = ArrayList()
                    if (it.comments != null)
                        d.comments.addAll(it.comments)
                    d.active = it.active
                    d.createdAt = it.createdAt
                    d.commentable = it.commentable
                    d.isProductFavoirte = it.isProductFavoirte
                    d.isProductFollow = it.isProductFollow
                    d.updatedAt = it.updatedAt
                    d.description = it.description
                    d.category = Category()
                    d.customer = customer!!
                    if (it.categoryId != null)
                        d.category.id = Integer.parseInt(it.categoryId)
                    d.subcategoryId = SubCategory()
                    if (it.subcategoryId != null)
                        d.subcategoryId.id = Integer.parseInt(it.subcategoryId)
                    d.city = City()
                    if (it.cityId != null)
                        d.city.id = Integer.parseInt(it.cityId)
                    d.state = City()
                    if (it.stateId != null)
                        d.state.id = Integer.parseInt(it.stateId)
                    list.add(d)
                }
            adapter = HarajAdapter(this, list){
                getProfile()
            }
            recycler.adapter = adapter
            errorMessage.visibility = if (customer!!.products.isEmpty()) VISIBLE else GONE
        }
    }

    private fun getProfile() {
        progress.visibility = VISIBLE
        val call = RetrofitModel.getApi(this).getUserDetailsById(customerId)
        call.enqueue(object : CallbackRetrofit<ProfileResponse>(this) {
            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                progress.visibility = GONE
                val result = response.body()
                if (response.isSuccessful && result != null) {
                    customer = result.data
                    updateUI()
                } else {
                    try {
                        var errorLoginResponse: ErrorLoginResponse? = null
                        if (response.errorBody() != null) {
                            errorLoginResponse = GsonBuilder().create().fromJson<Any>(response.errorBody()!!.string(), ErrorLoginResponse::class.java) as ErrorLoginResponse?
                            if (errorLoginResponse != null) {
                                toastMessageShort(baseContext, errorLoginResponse.message)
                                if (errorLoginResponse.data.password != null && errorLoginResponse.data.password.isNotEmpty()) {
                                    toastMessageShort(baseContext, errorLoginResponse.data.password[0])
                                }
                                if (errorLoginResponse.data.phone != null && errorLoginResponse.data.phone.isNotEmpty()) {
                                    toastMessageShort(baseContext, errorLoginResponse.data.phone[0])
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        toastMessageShort(baseContext, R.string.connection_error)
                    }
                    finish()
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                super.onFailure(call, t)
                progress.visibility = GONE
                finish()
            }
        })
    }

}

