package com.benAbdelWahed.activities

import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import androidx.appcompat.app.AppCompatActivity
import com.benAbdelWahed.R
import com.benAbdelWahed.adapters.ChatRoomAdapter
import com.benAbdelWahed.models.FirebaseBody
import com.benAbdelWahed.responses.chat_responses.Message
import com.benAbdelWahed.responses.chat_responses.UserItem
import com.benAbdelWahed.responses.firebase_responses.FirebaseResponse
import com.benAbdelWahed.responses.haraj_responses.Customer
import com.benAbdelWahed.utils.CallbackRetrofit
import com.benAbdelWahed.utils.PrefManager
import com.benAbdelWahed.utils.RetrofitModel
import com.benAbdelWahed.utils.StaticMembers.*
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_chat_room.*
import retrofit2.Call
import retrofit2.Response


class ChatRoomActivity : AppCompatActivity() {
    private var otherID: Int = 0
    private var myID: Int = 0
    private var conversationKey = ""
    private lateinit var prefManager: PrefManager
    private lateinit var chatRoomAdapter: ChatRoomAdapter
    private lateinit var list: ArrayList<Message>
    private lateinit var otherTokens: ArrayList<String>
    private var otherUser: UserItem? = null
    private lateinit var myUser: Customer
    private lateinit var valueListener: ValueEventListener
    private lateinit var ref: DatabaseReference
    private lateinit var usersRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        prefManager = PrefManager.getInstance(this)
        myUser = PrefManager.getInstance(this).getObject(PROFILE, Customer::class.java) as Customer
        otherID = intent.getIntExtra(OTHER_ID, 0)
        myID = myUser.id
        conversationKey = "${otherID * myID + otherID + myID}"


        image.setOnClickListener {
            val intent = Intent(this, ProductOwnerProfileActivity::class.java)
            intent.putExtra(CUSTOMER_ID, otherID)
            startActivity(intent)
        }
        name.setOnClickListener { image.performClick() }

        list = ArrayList()
        otherTokens = ArrayList()
        chatRoomAdapter = ChatRoomAdapter(this, list)
        recycler.adapter = chatRoomAdapter

        ref = FirebaseDatabase.getInstance().getReference(CONVERSATIONS).child(conversationKey)
        usersRef = FirebaseDatabase.getInstance().reference.child(USERS)

        usersRef.child("$otherID").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                otherUser = dataSnapshot.child(INFO).getValue(UserItem::class.java)
                if (otherUser != null) {
                    name.text = otherUser!!.user_name
                    if (otherUser!!.avatar != null && otherUser!!.avatar!!.isNotEmpty())
                        Glide.with(applicationContext).load(otherUser!!.avatar).centerCrop().placeholder(R.drawable.place_holder_logo).into(image)
                    else
                        image.setImageResource(R.drawable.place_holder_logo)
                    dataSnapshot.child(FIREBASE_TOKENS).children.forEach {
                        otherTokens.add(it.key!!)
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                chatShimmerLayout.stopShimmer()
                chatShimmerLayout.visibility = GONE
            }
        })

        valueListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                usersRef.child("$myID").child(UNREAD_MESSAGES_COUNT).removeValue()
                chatShimmerLayout.stopShimmer()
                chatShimmerLayout.visibility = GONE
                list.clear()
                dataSnapshot.children.forEach {
                    list.add(it.getValue(Message::class.java)!!)
                }
                chatRoomAdapter.notifyDataSetChanged()
                recycler.smoothScrollToPosition(chatRoomAdapter.itemCount)
            }

            override fun onCancelled(p0: DatabaseError) {
                chatShimmerLayout.stopShimmer()
                chatShimmerLayout.visibility = GONE
            }
        }

        ref.addValueEventListener(valueListener)

        send.setOnClickListener {
            if (messageEditText.text.toString().isEmpty()) {
                toastMessageShort(this, R.string.message_empty)
                return@setOnClickListener
            }
            sendMessage()
        }
    }

    fun sendMessage() {
        val message = Message(messageEditText.text.toString(), myID, getDate(), getDate())
        messageEditText.setText("")
        hideKeyboard(this)
        val messageRef = ref.push()
        messageRef.setValue(message)
        sendFirebaseMessage(message.text)
        usersRef.child("$myID").child(MESSAGES).child("$otherID").setValue(message)
        usersRef.child("$otherID").child(MESSAGES).child("$myID").setValue(message)
        if (messageRef.key != null)
            usersRef.child("$otherID").child(UNREAD_MESSAGES_COUNT).child(messageRef.key!!).setValue(0)
    }

    fun sendFirebaseMessage(message: String) {
        if (otherTokens.isNotEmpty()) {
            val call = RetrofitModel.getFirebaseAPI().sendMessage(FirebaseBody(otherTokens, message, myUser.user_name, myID))
            call.enqueue(object : CallbackRetrofit<FirebaseResponse>(this) {
                override fun onResponse(call: Call<FirebaseResponse>, response: Response<FirebaseResponse>) {
                }
            })
        }
    }

    override fun onDestroy() {
        ref.removeEventListener(valueListener)
        super.onDestroy()
    }

}
