package com.benAbdelWahed.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.benAbdelWahed.R
import com.benAbdelWahed.activities.LoginActivity
import com.benAbdelWahed.adapters.NotificationListAdapter
import com.benAbdelWahed.responses.add_inc_response.AddIncResponse
import com.benAbdelWahed.responses.notification_responses.NotificationItem
import com.benAbdelWahed.responses.notification_responses.NotificationsResponse
import com.benAbdelWahed.utils.CallbackRetrofit
import com.benAbdelWahed.utils.PrefManager
import com.benAbdelWahed.utils.RetrofitModel
import com.benAbdelWahed.utils.StaticMembers.startActivityOverAll
import com.benAbdelWahed.utils.StaticMembers.toastMessageShort
import kotlinx.android.synthetic.main.fragment_chat_list.errorCard
import kotlinx.android.synthetic.main.fragment_chat_list.errorMessage
import kotlinx.android.synthetic.main.fragment_chat_list.login
import kotlinx.android.synthetic.main.fragment_chat_list.recycler
import kotlinx.android.synthetic.main.fragment_notification_list.*
import retrofit2.Call
import retrofit2.Response

class NotificationsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_notification_list, container, false)
    }

    lateinit var prefManager: PrefManager
    private lateinit var notificationListAdapter: NotificationListAdapter
    var list = ArrayList<NotificationItem>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefManager = PrefManager(context)
        login.setOnClickListener {
            startActivityOverAll(activity, LoginActivity::class.java)
        }
        notificationListAdapter = NotificationListAdapter(activity, list)
        recycler.adapter = notificationListAdapter
        if (prefManager.apiToken.isNotEmpty()) {
            clearNotifications.visibility = VISIBLE
            getNotifications()
            swipe.setOnRefreshListener {
                getNotifications()
            }
        } else {
            errorCard.visibility = VISIBLE
            login.visibility = VISIBLE
            errorMessage.text = getString(R.string.need_login)
            login.setOnClickListener {
                startActivityOverAll(activity, LoginActivity::class.java)
            }
        }
        clearNotifications.setOnClickListener {
            showClearAlert()
        }
    }

    fun showClearAlert() {
        val alertDialog = AlertDialog.Builder(activity!!)
        alertDialog.setTitle(getString(R.string.sure_clear_noti))
        alertDialog.setPositiveButton(activity!!.getString(R.string.yes)) { dialog, which -> clearNotifications() }
        alertDialog.setNegativeButton(activity!!.getString(R.string.no), null)
        alertDialog.show()
    }

    fun clearNotifications() {
        if (context != null) {
            val call = RetrofitModel.getApi(context).deleteAllNotification()
            call.enqueue(object : CallbackRetrofit<AddIncResponse>(context) {
                override fun onResponse(call: Call<AddIncResponse>, response: Response<AddIncResponse>) {
                    if (context != null && view != null) {
                        val result = response.body()
                        if (response.isSuccessful && result != null) {
                            getNotifications()
                        } else {
                            toastMessageShort(context!!, R.string.connection_error)
                        }
                    }
                }

                override fun onFailure(call: Call<AddIncResponse>, t: Throwable) {
                    toastMessageShort(context!!, R.string.connection_error)
                }
            })
        }
    }

    fun getNotifications() {
        if (context != null) {
            errorCard.visibility = GONE
            login.visibility = GONE
            swipe.isRefreshing = true
            val call = RetrofitModel.getApi(context).allNotification
            call.enqueue(object : CallbackRetrofit<NotificationsResponse>(context) {
                override fun onResponse(call: Call<NotificationsResponse>, response: Response<NotificationsResponse>) {
                    if (context != null && view != null) {
                        swipe.isRefreshing = false
                        list.clear()
                        val result = response.body()
                        if (response.isSuccessful && result != null) {
                            list.addAll(result.data)
                            notificationListAdapter.notifyDataSetChanged()
                            errorCard.visibility = if (list.isEmpty()) VISIBLE else GONE
                            errorMessage.text = getString(R.string.no_notifications)
                        } else {
                            showError()
                        }
                    }
                }

                override fun onFailure(call: Call<NotificationsResponse>, t: Throwable) {
                    swipe.isRefreshing = false
                    showError()
                }
            })
        }
    }

    fun showError() {
        errorCard.visibility = VISIBLE
        login.visibility = VISIBLE
        errorMessage.text = getString(R.string.need_login)
    }

    fun refresh() {
        getNotifications()
    }
}