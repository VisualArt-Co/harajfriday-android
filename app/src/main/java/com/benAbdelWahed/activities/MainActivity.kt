package com.benAbdelWahed.activities

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.benAbdelWahed.R
import com.benAbdelWahed.fragments.ChatListFragment
import com.benAbdelWahed.fragments.MainFragment
import com.benAbdelWahed.fragments.MoreFragment
import com.benAbdelWahed.fragments.NotificationsFragment
import com.benAbdelWahed.responses.add_inc_response.AddIncResponse
import com.benAbdelWahed.responses.haraj_responses.Customer
import com.benAbdelWahed.responses.notification_responses.CountNotifyResponse
import com.benAbdelWahed.utils.*
import com.benAbdelWahed.utils.StaticMembers.*
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Response
import java.util.*


class MainActivity : AppCompatActivity() {


    private lateinit var mainFragment: MainFragment
    private lateinit var moreFragment: MoreFragment
    private lateinit var chatListFragment: ChatListFragment
    private lateinit var notificationsFragment: NotificationsFragment
    private var messagesRef: DatabaseReference? = null
    private var usersRef: DatabaseReference? = null
    private lateinit var valueListener: ValueEventListener

    lateinit var prefManager: PrefManager
    private var myUser: Customer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bid.setOnClickListener {
            startActivity(Intent(this, MazadsActivity::class.java))
        }
        haraj.setOnClickListener {
            startActivity(Intent(this, HarajActivity::class.java))
        }
        mainFragment = MainFragment()
        moreFragment = MoreFragment()
        chatListFragment = ChatListFragment()
        notificationsFragment = NotificationsFragment()

        openFragment(mainFragment, R.id.mainFrameLayout)
        openFragment(moreFragment, R.id.moreLayout)
        openFragment(chatListFragment, R.id.chatListLayout)
        openFragment(notificationsFragment, R.id.notificationsLayout)
        changeMessagesRadioButton(0)
        mainRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.mainButton -> {
                    mainFrameLayout.visibility = VISIBLE
                    moreLayout.visibility = GONE
                    chatListLayout.visibility = GONE
                    notificationsLayout.visibility = GONE
                    ProfileUtil.getInstance(baseContext).getProfile()
                    mainFragment.refresh()
                }
                R.id.notificationsButton -> {
                    mainFrameLayout.visibility = GONE
                    moreLayout.visibility = GONE
                    chatListLayout.visibility = GONE
                    notificationsLayout.visibility = VISIBLE
                    notificationsFragment.refresh()
                    readNotifications()
                }
                R.id.messagesButton -> {
                    mainFrameLayout.visibility = GONE
                    chatListLayout.visibility = VISIBLE
                    moreLayout.visibility = GONE
                    notificationsLayout.visibility = GONE
                    readMessages()
                }
                R.id.moreButton -> {
                    mainFrameLayout.visibility = GONE
                    chatListLayout.visibility = GONE
                    moreLayout.visibility = VISIBLE
                    notificationsLayout.visibility = GONE
                }
            }
        }
        prefManager = PrefManager(this)
        myUser = prefManager.getObject(PROFILE, Customer::class.java) as Customer?
        if (myUser != null) {
            usersRef = FirebaseDatabase.getInstance().reference.child(USERS).child("${myUser!!.id}")
            messagesRef = usersRef!!.child(UNREAD_MESSAGES_COUNT)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_PROFILE_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                mainFragment.refresh()
                moreFragment.refresh()
                notificationsFragment.refresh()
            }
        }
    }

    private fun readMessages() {
        if (messagesRef != null)
            messagesRef!!.removeValue()
    }

    override fun onStart() {
        super.onStart()
        getCountNotifications()
        if (usersRef != null) {
            usersRef!!.child(ACTIVITY).child(IS_ACTIVE).setValue(true)
        }
        if (messagesRef != null) {
            valueListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    changeMessagesRadioButton(dataSnapshot.children.count())
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            }
            messagesRef!!.addValueEventListener(valueListener)
        }
    }

    override fun onPause() {
        super.onPause()
        if (messagesRef != null)
            messagesRef!!.removeEventListener(valueListener)
    }

    override fun onDestroy() {
        if (usersRef != null) {
            usersRef!!.child(ACTIVITY).child(IS_ACTIVE).setValue(false)
            usersRef!!.child(ACTIVITY).child(LAST_ACTIVE_TIME).setValue(getDate())
        }
        super.onDestroy()
    }

    fun changeMessagesRadioButton(i: Int) {
        messagesButton.setCompoundDrawablesRelativeWithIntrinsicBounds(
                null,
                setCheckedSelector(i, R.drawable.ic_message_check, R.drawable.ic_message_uncheck),
                null,
                null)
    }

    fun changeNotificationsRadioButton(i: Int) {
        notificationsButton.setCompoundDrawablesRelativeWithIntrinsicBounds(
                null,
                setCheckedSelector(i, R.drawable.ic_notifications_check, R.drawable.ic_notifications_uncheck),
                null,
                null)
    }

    fun setCheckedSelector(counter: Int, checked: Int, unChecked: Int): StateListDrawable? {
        try {
            val pressed = Converter.convertLayoutToImage(this, counter, checked, checked)
            val normal = Converter.convertLayoutToImage(this, counter, unChecked, unChecked)

            val states = StateListDrawable()
            states.addState(intArrayOf(android.R.attr.state_checked), pressed)
            states.addState(intArrayOf(android.R.attr.state_pressed), pressed)

            states.addState(intArrayOf(android.R.attr.state_checked, android.R.attr.state_enabled), pressed)
            states.addState(intArrayOf(android.R.attr.state_checked, -android.R.attr.state_enabled), pressed)

            states.addState(intArrayOf(), normal)
            return states
        } catch (e: Exception) {
        }
        return null
    }


    fun getCountNotifications() {
        val call = RetrofitModel.getApi(this).countUnReadNotifications
        call.enqueue(object : CallbackRetrofit<CountNotifyResponse>(this) {
            override fun onResponse(call: Call<CountNotifyResponse>, response: Response<CountNotifyResponse>) {
                if (response.body() != null)
                    changeNotificationsRadioButton(response.body()!!.data)
                else
                    changeNotificationsRadioButton(0)
            }

            override fun onFailure(call: Call<CountNotifyResponse>, t: Throwable) {
            }
        })
    }

    fun readNotifications() {
        val call = RetrofitModel.getApi(this).resetCountNuRead()
        call.enqueue(object : CallbackRetrofit<AddIncResponse>(this) {
            override fun onResponse(call: Call<AddIncResponse>, response: Response<AddIncResponse>) {
                if (response.body() != null && response.body()!!.isSuccess)
                    changeNotificationsRadioButton(0)
            }

            override fun onFailure(call: Call<AddIncResponse>, t: Throwable) {
            }
        })
    }

    fun openFragment(f: Fragment, id: Int) {
        val t: FragmentTransaction = supportFragmentManager.beginTransaction()
        t.replace(id, f, "" + id)
        t.addToBackStack(null)
        t.commit()
    }

    override fun onBackPressed() {
        if (mainFrameLayout.visibility == VISIBLE)
            finish()
        else {
            mainRadioGroup.check(R.id.mainButton)
        }
    }
}
