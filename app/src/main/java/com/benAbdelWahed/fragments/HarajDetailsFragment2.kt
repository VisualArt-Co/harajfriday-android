package com.benAbdelWahed.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.benAbdelWahed.R
import com.benAbdelWahed.activities.ChatRoomActivity
import com.benAbdelWahed.activities.EditHarajActivity
import com.benAbdelWahed.activities.HarajSearchResultsActivity
import com.benAbdelWahed.activities.ProductOwnerProfileActivity
import com.benAbdelWahed.adapters.HarajAdSimilerAdapter
import com.benAbdelWahed.adapters.HarajCommentsAdapter
import com.benAbdelWahed.adapters.HarajImagesAdapter
import com.benAbdelWahed.responses.add_inc_response.AddIncResponse
import com.benAbdelWahed.responses.haraj_responses.*
import com.benAbdelWahed.responses.login_response.LoginResponse
import com.benAbdelWahed.utils.CallbackRetrofit
import com.benAbdelWahed.utils.PrefManager
import com.benAbdelWahed.utils.RetrofitModel
import com.benAbdelWahed.utils.StaticMembers.*
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fg_haraj_details.*
import kotlinx.android.synthetic.main.progress_layout.*
import retrofit2.Call
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class HarajDetailsFragment2 : DialogFragment() {

    companion object {
        fun newInstance(product: DataItem): HarajDetailsFragment2 {
            val args = Bundle()
            args.putSerializable(PRODUCT, product)
            val harajDetailsFragment = HarajDetailsFragment2()
            harajDetailsFragment.arguments = args
            return harajDetailsFragment
        }

        fun newInstance(harajId: String): HarajDetailsFragment2 {
            val args = Bundle()
            args.putString(HARAJ_ID, harajId)
            val harajDetailsFragment = HarajDetailsFragment2()
            harajDetailsFragment.arguments = args
            return harajDetailsFragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.DialogTransFadeFast)
    }

    private var myId: Int = -1
    private lateinit var prefManager: PrefManager
    lateinit var harajImagesAdapter: HarajImagesAdapter
    lateinit var harajCommentsAdapter: HarajCommentsAdapter
    lateinit var harajAdSimilerAdapter: HarajAdSimilerAdapter
    var imagesList: ArrayList<ImagesItem> = ArrayList()
    var commentsList: ArrayList<Comment> = ArrayList()
    var adSmallList: ArrayList<DataItem> = ArrayList()
    var product: DataItem? = null
    var productId = ""
    var myUserProfile: Customer? = null
    var number: String? = null

    private lateinit var ref: DatabaseReference
    private lateinit var valueEventListener: ValueEventListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fg_haraj_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefManager = PrefManager.getInstance(activity)
        myUserProfile = PrefManager.getInstance(activity).getObject(PROFILE, Customer::class.java) as Customer?
        myUserProfile?.let {
            myId = it.id
        }
        if (arguments != null) {
            product = arguments!!.getSerializable(PRODUCT) as DataItem?
            if (product != null) {
                productId = "${product!!.id}"
            } else {
//                if (arguments!!.getString(HARAJ_ID) != null)
                productId = arguments!!.getString(HARAJ_ID) as String
                progress.visibility = VISIBLE
            }
        }
        updateUI()
        ref = FirebaseDatabase.getInstance().getReference(PRODUCTS).child(productId)
    }

    override fun onStart() {
        super.onStart()
        valueEventListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                toastMessageShort(activity, R.string.connection_error)
            }

            override fun onDataChange(p0: DataSnapshot) {
                refreshProduct()
            }
        }
        ref.addValueEventListener(valueEventListener)
    }

    override fun onPause() {
        super.onPause()
        ref.removeEventListener(valueEventListener)
    }

    private fun updateUI() {
        if (product != null) {

            if (myUserProfile != null)
                messageUser.visibility = if (product!!.customer.id == myUserProfile!!.id) View.INVISIBLE else VISIBLE
            messageUser.setOnClickListener {
                if (myUserProfile != null) {
                    val intent = Intent(activity, ChatRoomActivity::class.java)
                    intent.putExtra(OTHER_ID, product!!.customer.id)
                    startActivity(intent)
                } else showLoginDialog(activity)
            }

            reviewsView.text = String.format(Locale.getDefault(), getString(R.string.s_ratings), product!!.customer!!.countOfLike)
            reviewsView.setOnClickListener {
                ReviewsDialog.getInstance(product!!.customer!!, object : ReviewsDialog.OnActionListener {
                    override fun onConfirm() {
                        refreshProduct()
                    }
                }).show(activity!!.supportFragmentManager, "ReviewsDialog")
            }
            deleteProduct.setOnClickListener {
                val alertDialog = AlertDialog.Builder(activity!!)
                alertDialog.setTitle(activity!!.getString(R.string.sure_delete_product))
                alertDialog.setPositiveButton(activity!!.getString(R.string.delete)) { dialog, which -> deleteProductNow() }
                alertDialog.setNegativeButton(activity!!.getString(R.string.cancel), null)
                alertDialog.show()
            }
            editProduct.setOnClickListener {
                val intent = Intent(activity, EditHarajActivity::class.java)
                intent.putExtra(PRODUCT, product)
                startActivity(intent)
            }
            reportImageView.visibility = if (myId == product!!.customer!!.id) GONE else VISIBLE
            ownerLayout.visibility = if (myId == product!!.customer!!.id) VISIBLE else GONE
            number = product!!.phone
            name.text = product!!.title
            description.text = product!!.description
            productIdTextView.text = "#${product!!.id}"
            if (product!!.createdAt != null && product!!.createdAt.isNotEmpty()) {
                val dateString = getDateFromBackendForHaraj(product!!.createdAt)
                dateText.text = String.format(Locale.getDefault(), "%s %s", if (dateString.contains("الآن")) "" else "قبل", dateString)
            }
            userName.text = product!!.customer.user_name
            verifyImage.visibility = if (product!!.customer!!.isPremium) VISIBLE else GONE
            userName.setOnClickListener {
                val intent = Intent(activity, ProductOwnerProfileActivity::class.java)
                product!!.customer.products = null
                intent.putExtra(CUSTOMER_ID, product!!.customer.id)
                activity!!.startActivityForResult(intent, EDIT_CODE)
            }
            city.text = product!!.state.name
            city.setOnClickListener {
                val intent = Intent(activity, HarajSearchResultsActivity::class.java)
                intent.putExtra(CITY, product!!.city.id)
                intent.putExtra(STATE, product!!.state.id)
                intent.putExtra(STATE_NAME, product!!.state.name)
                startActivity(intent)
            }
            wishListCheckBox.isChecked = product!!.isProductFavoirte
            wishListCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
                if (buttonView.isPressed) {
                    addFavorite(product!!.id, wishListCheckBox)
                }
            }

            followComments.setText(if (product!!.isProductFollow) R.string.remove_follow else R.string.follow_comments)
            followComments.setOnClickListener {
                addFollow(product!!.id)
            }

            harajImagesAdapter = HarajImagesAdapter(activity, imagesList)
            harajCommentsAdapter = HarajCommentsAdapter(activity, commentsList, product!!.customer.id)
            harajAdSimilerAdapter = HarajAdSimilerAdapter(requireActivity(), adSmallList){
                refreshProduct()
            }
            imagesRecycler.adapter = harajImagesAdapter
            commentsRecycler.adapter = harajCommentsAdapter
            adsRecycler.adapter = harajAdSimilerAdapter
            commentsList.clear()
            commentsList.addAll(product!!.comments)
            harajCommentsAdapter.notifyDataSetChanged()
            imagesList.clear()
            imagesList.addAll(product!!.images)
            harajImagesAdapter.notifyDataSetChanged()
            adSmallList.clear()
            if (product!!.relatedProducts != null)
                adSmallList.addAll(product!!.relatedProducts)
            relatedAds.visibility = if (adSmallList.isEmpty()) GONE else VISIBLE
            harajAdSimilerAdapter.notifyDataSetChanged()
            if (number != null && number!!.isNotEmpty()) {
                whatsappText.visibility = VISIBLE
                phoneText.visibility = VISIBLE
            }
            whatsappText.setOnClickListener {
                val url = "https://api.whatsapp.com/send?phone=+966$number"
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(i)
            }
            phoneText.text = number
            phoneText.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:+966$number")
                startActivity(intent)
            }

            reportImageView.setOnClickListener {
                val alertDialog = AlertDialog.Builder(activity!!)
                alertDialog.setTitle(getString(R.string.report_product))
                val view = LayoutInflater.from(activity).inflate(R.layout.fg_report_text, null, false)
                val editText = view.findViewById<TextInputEditText>(R.id.editText)
                val textInputLayout = view.findViewById<TextInputLayout>(R.id.textInputLayout)
                alertDialog.setView(view)
                alertDialog.setPositiveButton(getString(R.string.report)) { _, _ -> sendReport(editText, textInputLayout) }
                alertDialog.setNegativeButton(getString(R.string.cancel), null)
                alertDialog.show()
            }

            val isCommentable = "0" == product!!.commentable
            turnedOffComments.visibility = if (isCommentable) VISIBLE else GONE
            addComment.visibility = if (isCommentable) GONE else VISIBLE
            addComment.setOnClickListener {
                val alertDialog = AlertDialog.Builder(activity!!)
                val input = EditText(activity)
                alertDialog.setTitle(getString(R.string.add_comment))
                input.setHint(R.string.add_comment)
                val lp = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT)
                input.layoutParams = lp
                alertDialog.setView(input) // uncomment this line
                alertDialog.setPositiveButton(getString(R.string.ok)) { dialog, which -> addCommentNow(input.text.toString()) }
                alertDialog.setNegativeButton(getString(R.string.cancel), null)
                alertDialog.show()
            }
        }
    }

    private fun refreshProduct() {
        val call = RetrofitModel.getApi(activity).getProductDetails(productId)
        call.enqueue(object : CallbackRetrofit<ProductDetailsResponse>(activity) {
            override fun onResponse(call: Call<ProductDetailsResponse>, response: Response<ProductDetailsResponse>) {
                if (context != null) {
                    val result = response.body()
                    progress.visibility = GONE
                    if (response.isSuccessful && result != null) {
                        product = response.body()!!.data
                        arguments!!.putSerializable(PRODUCT, product)
                        updateUI()
                    } else {
                        checkError(activity, response.errorBody())
                    }

                }
            }

            override fun onFailure(call: Call<ProductDetailsResponse>, t: Throwable) {
                super.onFailure(call, t)
                if (context != null)
                    progress.visibility = GONE
            }
        })
    }


    private fun deleteProductNow() {
        val call = RetrofitModel.getApi(activity).removeProduct(productId.toInt())
        call.enqueue(object : CallbackRetrofit<AddIncResponse>(activity) {
            override fun onResponse(call: Call<AddIncResponse>, response: Response<AddIncResponse>) {
                val result = response.body()
                if (activity != null && response.isSuccessful && result != null) {
                    toastMessageShort(activity, R.string.success_delete_product)
                    activity!!.setResult(Activity.RESULT_OK)
                    activity!!.finish()
                } else {
                    checkError(activity, response.errorBody())
                }

            }
        })
    }

    private fun sendReport(editText: TextInputEditText, textInputLayout: TextInputLayout) {
        if (checkTextInputEditText(editText, textInputLayout, activity!!.getString(R.string.problem_req))) {
            val call = RetrofitModel.getApi(activity).reportProduct(product!!.id, editText.text.toString())
            call.enqueue(object : CallbackRetrofit<AddIncResponse>(activity) {
                override fun onResponse(call: Call<AddIncResponse>, response: Response<AddIncResponse>) {
                    val result = response.body()
                    if (response.isSuccessful && result != null) {
                        activity!!.setResult(Activity.RESULT_OK)
                        toastMessageShort(activity, R.string.success_report_product)
                    } else {
                        checkError(activity, response.errorBody())
                    }

                }
            })
        }
    }

    private fun addFavorite(id: Int, checkBox: CheckBox) {
        val call = if (!checkBox.isChecked) RetrofitModel.getApi(activity).removeFavorite(id) else RetrofitModel.getApi(activity).addFavorite(id)
        call.enqueue(object : CallbackRetrofit<AddIncResponse>(activity) {
            override fun onResponse(call: Call<AddIncResponse>, response: Response<AddIncResponse>) {
                val result = response.body()
                if (response.isSuccessful && result != null) {
                    product!!.isProductFavoirte = checkBox.isChecked
                    activity!!.setResult(Activity.RESULT_OK)
                    toastMessageShort(activity, R.string.success)
                } else {
                    checkBox.isChecked = !checkBox.isChecked
                    checkError(activity, response.errorBody())
                }

            }

            override fun onFailure(call: Call<AddIncResponse>, t: Throwable) {
                super.onFailure(call, t)
                checkBox.isChecked = !checkBox.isChecked
            }
        })
    }

    private fun addFollow(id: Int) {
        val call = if (product!!.isProductFollow) RetrofitModel.getApi(activity).removeFollow(id) else RetrofitModel.getApi(activity).addFollow(id)
        call.enqueue(object : CallbackRetrofit<AddIncResponse>(activity) {
            override fun onResponse(call: Call<AddIncResponse>, response: Response<AddIncResponse>) {
                val result = response.body()
                if (response.isSuccessful && result != null) {
                    product!!.isProductFollow = !product!!.isProductFollow
                    followComments.setText(if (product!!.isProductFollow) R.string.remove_follow else R.string.follow_comments)
                    activity!!.setResult(Activity.RESULT_OK)
                    toastMessageShort(activity, R.string.success)
                } else {
                    followComments.setText(if (!product!!.isProductFollow) R.string.remove_follow else R.string.follow_comments)
                    checkError(activity, response.errorBody())
                }

            }

            override fun onFailure(call: Call<AddIncResponse>, t: Throwable) {
                super.onFailure(call, t)
                followComments.setText(if (!product!!.isProductFollow) R.string.remove_follow else R.string.follow_comments)
            }
        })
    }

    private fun addCommentNow(comment: String) {
        if (comment.isEmpty()) {
            toastMessageShort(context, getString(R.string.please_add_comment))
            return
        }
        val call = RetrofitModel.getApi(context).addComment(product!!.customer.id, product!!.id, comment)
        call.enqueue(object : CallbackRetrofit<LoginResponse>(context) {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                progress.visibility = View.GONE
                val result = response.body()
                if (response.isSuccessful && result != null) {
                    refreshProduct()
                    activity!!.setResult(Activity.RESULT_OK)
                    toastMessageShort(context, getString(R.string.done_add_comment))
                } else {
                    checkError(activity, response.errorBody())
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                super.onFailure(call, t)
                progress.visibility = View.GONE
            }
        })
    }


}
