package com.benAbdelWahed.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.benAbdelWahed.R
import com.benAbdelWahed.adapters.HarajReviewsAdapter
import com.benAbdelWahed.responses.haraj_responses.Customer
import kotlinx.android.synthetic.main.fg_mazads.*

class ReviewsFragment : Fragment() {

    lateinit var customer: Customer
    lateinit var harajReviewsAdapter: HarajReviewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (customer.allRates == null)
            customer.allRates = ArrayList()
        harajReviewsAdapter = HarajReviewsAdapter(activity, customer.allRates)
        recycler.adapter = harajReviewsAdapter
        swipe.setOnRefreshListener {
            swipe.isRefreshing = false
        }
    }

    companion object {
        fun getInstance(customer: Customer): ReviewsFragment {
            val dialog = ReviewsFragment()
            dialog.customer = customer
            return dialog
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fg_mazads, container, false)
    }
}
