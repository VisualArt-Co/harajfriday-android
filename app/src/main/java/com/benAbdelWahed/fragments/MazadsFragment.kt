package com.benAbdelWahed.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import butterknife.ButterKnife
import com.benAbdelWahed.R
import com.benAbdelWahed.adapters.MazadAdapter
import com.benAbdelWahed.responses.mazads_response.AllMazadResponse
import com.benAbdelWahed.responses.mazads_response.DataItem
import com.benAbdelWahed.ui.membership.MembershipActivity
import com.benAbdelWahed.utils.CallbackRetrofit
import com.benAbdelWahed.utils.PrefManager
import com.benAbdelWahed.utils.RetrofitModel
import com.benAbdelWahed.utils.StaticMembers
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fg_mazads.*
import kotlinx.android.synthetic.main.progress_layout.*
import retrofit2.Call
import retrofit2.Response

class MazadsFragment : Fragment() {

    private var adapter: MazadAdapter? = null
    private lateinit var bidCardList: ArrayList<DataItem>
    private var type = 0

    private var prefManager: PrefManager? = null

    private lateinit var ref: DatabaseReference
    private lateinit var valueEventListener: ValueEventListener
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fg_mazads, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view)

        bidCardList = ArrayList()
        adapter = MazadAdapter(requireActivity(), progress, bidCardList, type, object : MazadAdapter.OnActionListener {
            override fun onRefresh() {
                getBids()
            }
        })

        progress.setOnClickListener { }
        recycler!!.adapter = adapter
        swipe!!.setOnRefreshListener {
            this.getBids()
        }
        prefManager = PrefManager.getInstance(context)
        subscribe!!.setOnClickListener { view1 ->
            if (prefManager!!.hasToken()) {
                startActivity(Intent(requireContext(), MembershipActivity::class.java))
            } else
                StaticMembers.showLoginDialog(activity)
        }
        ref = FirebaseDatabase.getInstance().getReference(StaticMembers.MAZADS)

    }

    override fun onStart() {
        super.onStart()
        valueEventListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                StaticMembers.toastMessageShort(activity, R.string.connection_error)
            }

            override fun onDataChange(p0: DataSnapshot) {
                getBids()

            }
        }
        ref.addValueEventListener(valueEventListener)

    }

    override fun onPause() {
        super.onPause()
        ref.removeEventListener(valueEventListener)
        adapter!!.changeAllCountDownTimers(true)
    }

    override fun onResume() {
        super.onResume()
        adapter!!.changeAllCountDownTimers(false)
    }

    fun getBids() {
        if (context != null) {
            swipe!!.isRefreshing = true
            val call = RetrofitModel.getApi(context).getAllMazads(StaticMembers.mazadTypes[type])
            call.enqueue(object : CallbackRetrofit<AllMazadResponse>(context) {
                override fun onResponse(call: Call<AllMazadResponse>, response: Response<AllMazadResponse>) {
                    if (context != null) {
                        errorCard!!.visibility = View.GONE
                        swipe!!.isRefreshing = false
                        bidCardList.clear()
                        val result = response.body()
                        if (response.isSuccessful && result != null) {
                            bidCardList.addAll(result.data)
                            adapter!!.notifyDataSetChanged()
                            if (bidCardList.isEmpty()) {
                                errorCard!!.visibility = View.VISIBLE
                                when (type) {
                                    0 -> {
                                        errorMessage!!.setText(R.string.subscribe_to_next_mazads)
                                        subscribe!!.visibility = if (!prefManager!!.getBoolean(StaticMembers.IS_PREMIUM)) View.VISIBLE else View.GONE
                                    }
                                    1 -> errorMessage!!.setText(R.string.soon_next_mazad)
                                }
                            }
                        } else {
                            StaticMembers.toastMessageShort(context, R.string.connection_error)
                        }
                    }

                }

                override fun onFailure(call: Call<AllMazadResponse>, t: Throwable) {
                    super.onFailure(call, t)
                    if (context != null) {
                        swipe!!.isRefreshing = false
                        errorCard!!.visibility = View.GONE
                    }
                }
            })
        }
    }

    companion object {
        fun getInstance(type: Int): MazadsFragment {
            val fragment = MazadsFragment()
            fragment.type = type
            return fragment
        }
    }
}
