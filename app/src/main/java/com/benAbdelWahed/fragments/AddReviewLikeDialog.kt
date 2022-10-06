package com.benAbdelWahed.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.benAbdelWahed.R
import com.benAbdelWahed.models.ReviewModel
import com.benAbdelWahed.responses.haraj_responses.Customer
import com.benAbdelWahed.utils.StaticMembers
import kotlinx.android.synthetic.main.fg_add_review_like.*
import java.util.*

class AddReviewLikeDialog : DialogFragment() {

    private lateinit var customer: Customer
    private lateinit var myUser: Customer
    private lateinit var onActionListener: OnActionListener

    var ok1 = false
    var ok2 = false
    var ok3 = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        backButton.setOnClickListener { dismiss() }
        confirmationCheckBox.text = StaticMembers.getTextHTML(String.format(Locale.getDefault(), getString(R.string.apply_review_text), myUser.user_name, customer.user_name))
        confirmationCheckBox.setOnCheckedChangeListener { group, isChecked ->
            ok1 = isChecked
        }
        question1Text.text = StaticMembers.getTextHTML(String.format(Locale.getDefault(), getString(R.string.did_bought), customer.user_name))
        question2Text.text = StaticMembers.getTextHTML(String.format(Locale.getDefault(), getString(R.string.did_advice), customer.user_name))

        question1RadioGroup.setOnCheckedChangeListener { group, checkedId ->
            ok2 = (checkedId == R.id.ok1)
        }
        question2RadioGroup.setOnCheckedChangeListener { group, checkedId ->
            ok3 = (checkedId == R.id.ok2)
        }
        send.setOnClickListener {
            addReview()
        }
    }

    private fun addReview() {
        if (!ok1) {
            StaticMembers.toastMessageShort(context, getString(R.string.must_ta3hod))
            return
        }
        if (question1RadioGroup.checkedRadioButtonId == -1) {
            StaticMembers.toastMessageShort(context, getString(R.string.must_choose_bought))
            return
        }

        if (question2RadioGroup.checkedRadioButtonId == -1) {
            StaticMembers.toastMessageShort(context, getString(R.string.must_choose_recommend))
            return
        }

        if (ok1 && StaticMembers.checkTextInputEditText(messageEditText, messageLayout, getString(R.string.req))) {
            val reviewModel = ReviewModel()
            reviewModel.comment = messageEditText.text.toString()
            reviewModel.isDealedWith = ok2
            reviewModel.isLikeHim = ok3
            onActionListener.onConfirm(reviewModel)
            dismiss()
        }
    }

    interface OnActionListener{
        fun onConfirm(reviewModel: ReviewModel)
    }

    companion object {
        fun getInstance(customer: Customer, myUser: Customer, onActionListener: OnActionListener): AddReviewLikeDialog {
            val dialog = AddReviewLikeDialog()
            dialog.customer = customer
            dialog.myUser = myUser
            dialog.onActionListener = onActionListener
            return dialog
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.DialogTrans)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fg_add_review_like, container, false)
    }
}
