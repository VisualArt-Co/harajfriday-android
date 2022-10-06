package com.benAbdelWahed.fragments

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.benAbdelWahed.R
import com.benAbdelWahed.adapters.HarajReviewsAdapter
import com.benAbdelWahed.models.ReviewModel
import com.benAbdelWahed.responses.haraj_responses.Customer
import com.benAbdelWahed.responses.login_response.LoginResponse
import com.benAbdelWahed.utils.CallbackRetrofit
import com.benAbdelWahed.utils.PrefManager
import com.benAbdelWahed.utils.RetrofitModel
import com.benAbdelWahed.utils.StaticMembers
import kotlinx.android.synthetic.main.fg_accept_haraj_terms.toolbar
import kotlinx.android.synthetic.main.fg_product_owner_reviews.*
import kotlinx.android.synthetic.main.progress_layout.*
import retrofit2.Call
import retrofit2.Response

class ReviewsDialog : DialogFragment() {

    lateinit var customer: Customer
    lateinit var onActionListener: OnActionListener
    lateinit var harajReviewsAdapter: HarajReviewsAdapter

    private var myUser: Customer? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.setNavigationOnClickListener { dismiss() }
        toolbar.title = customer.user_name
        myUser = PrefManager(context).getObject(StaticMembers.PROFILE, Customer::class.java) as Customer?
        if (myUser != null)
            addReview.visibility = if (myUser!!.id == customer.id) GONE else VISIBLE
        addReview.setOnClickListener {
            AddReviewLikeDialog.getInstance(customer, myUser!!, object : AddReviewLikeDialog.OnActionListener {
                override fun onConfirm(reviewModel: ReviewModel) {
                    addRating(if (reviewModel.isLikeHim) 1 else -1, reviewModel.comment)
                }
            }).show(activity!!.supportFragmentManager, "AddReviewLikeDialog")
        }
        if (customer.allRates == null)
            customer.allRates = ArrayList()
        harajReviewsAdapter = HarajReviewsAdapter(activity, customer.allRates)
        recycler.adapter = harajReviewsAdapter
        swipe.setOnRefreshListener {
            swipe.isRefreshing = false
        }

    }

    private fun addRating(rating: Int, toString: String) {
        progress.visibility = VISIBLE
        val call = RetrofitModel.getApi(context).addRate(customer.id, toString, rating)
        call.enqueue(object : CallbackRetrofit<LoginResponse>(context) {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                progress.visibility = GONE
                val result = response.body()
                if (response.isSuccessful && result != null) {
                    onActionListener.onConfirm()
                    StaticMembers.toastMessageShort(context, getString(R.string.done_add_comment))
                    (activity)!!.setResult(Activity.RESULT_OK)
                    dismiss()
                } else {
                    StaticMembers.checkError(activity, response.errorBody())
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                super.onFailure(call, t)
                progress.visibility = GONE
            }
        })
    }

    companion object {
        fun getInstance(customer: Customer, onActionListener: OnActionListener): ReviewsDialog {
            val dialog = ReviewsDialog()
            dialog.customer = customer
            dialog.onActionListener = onActionListener
            return dialog
        }
    }


    public interface OnActionListener {
        fun onConfirm()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.DialogTrans)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fg_product_owner_reviews, container, false)
    }
}
