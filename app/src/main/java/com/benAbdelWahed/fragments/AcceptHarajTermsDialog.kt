package com.benAbdelWahed.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.benAbdelWahed.R
import com.benAbdelWahed.activities.AddHarajActivity
import kotlinx.android.synthetic.main.fg_accept_haraj_terms.*

class AcceptHarajTermsDialog : DialogFragment() {

    var ok1 = false
    var ok2 = false
    var ok3 = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.setNavigationOnClickListener { dismiss() }
        term1.setOnCheckedChangeListener { group, checkedId ->
            ok1 = (checkedId == R.id.ok1)
            moveToAddHaraj()
        }
        term2.setOnCheckedChangeListener { group, checkedId ->
            ok2 = (checkedId == R.id.ok2)
            moveToAddHaraj()
        }
        term3.setOnCheckedChangeListener { group, checkedId ->
            ok3 = (checkedId == R.id.yes)
            moveToAddHaraj()
        }
    }

    fun moveToAddHaraj()
    {
        if (ok1 && ok2 && ok3) {
            startActivity(Intent(activity, AddHarajActivity::class.java))
            dismiss()
        }
    }

    companion object {
        fun getInstance(): AcceptHarajTermsDialog {
            val dialog = AcceptHarajTermsDialog()
            return dialog
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.DialogTrans)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fg_accept_haraj_terms, container, false)
    }
}
