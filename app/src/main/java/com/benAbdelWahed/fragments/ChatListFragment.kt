package com.benAbdelWahed.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.benAbdelWahed.R
import com.benAbdelWahed.activities.LoginActivity
import com.benAbdelWahed.adapters.ChatListAdapter
import com.benAbdelWahed.responses.chat_responses.Message
import com.benAbdelWahed.responses.chat_responses.UserItem
import com.benAbdelWahed.responses.haraj_responses.Customer
import com.benAbdelWahed.utils.PrefManager
import com.benAbdelWahed.utils.StaticMembers.*
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_chat_list.*
import kotlinx.android.synthetic.main.progress_layout.*

class ChatListFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_chat_list, container, false)
    }


    private var valueListener: ValueEventListener? = null
    private lateinit var ref: DatabaseReference

    lateinit var chatListAdapter: ChatListAdapter
    lateinit var prefManager: PrefManager
    private var myUser: Customer? = null

    var list = ArrayList<UserItem>()
    var userId: String = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefManager = PrefManager(context)
        myUser = prefManager.getObject(PROFILE, Customer::class.java) as Customer?
        chatListAdapter = ChatListAdapter(activity, list)
        recycler.adapter = chatListAdapter
        ref = FirebaseDatabase.getInstance().reference.child(USERS)

    }

    override fun onStart() {
        super.onStart()
        if (myUser != null) {
            progress.visibility = VISIBLE
            errorCard.visibility = GONE
            login.visibility = GONE
            userId = "${myUser!!.id}"
            valueListener = object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    progress.visibility = GONE
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    progress.visibility = GONE
                    list.clear()
                    val iterable = dataSnapshot.child(userId).child(MESSAGES).children
                    iterable.forEach {
                        val message = it.getValue(Message::class.java)
                        if (message != null) {
                            val u = dataSnapshot.child(it.key!!).child(INFO).getValue(UserItem::class.java)
                            if (u != null) {
                                u.lastMessage = message
                                list.add(u)
                            }
                        }
                    }
                    list.sortWith(kotlin.Comparator { t, t2 ->
                        val dateT = parseDate(t.lastMessage!!.createdAt)
                        val dateT2 = parseDate(t2.lastMessage!!.createdAt)
                        return@Comparator when {
                            dateT.after(dateT2) -> -1
                            dateT.before(dateT2) -> 1
                            else -> 0
                        }
                    })
                    chatListAdapter.notifyDataSetChanged()
                    errorCard.visibility = if (list.isEmpty()) VISIBLE else GONE
                    errorMessage.text = getString(R.string.no_messages)
                }
            }
            ref.addValueEventListener(valueListener!!)
        } else {
            errorCard.visibility = VISIBLE
            login.visibility = VISIBLE
            errorMessage.text = getString(R.string.need_login)
            login.setOnClickListener {
                startActivityOverAll(activity, LoginActivity::class.java)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (valueListener != null)
            ref.removeEventListener(valueListener!!)

    }
}