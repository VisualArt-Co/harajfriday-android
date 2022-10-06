package com.benAbdelWahed.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.viewpager2.widget.ViewPager2
import com.benAbdelWahed.R
import com.benAbdelWahed.adapters.PagerAdapter
import com.benAbdelWahed.responses.haraj_responses.DataItem
import com.benAbdelWahed.utils.PrefManager
import com.benAbdelWahed.utils.StaticMembers
import kotlinx.android.synthetic.main.activity_haraj_details.*
import java.util.*
import kotlin.collections.ArrayList

class HarajAllDetailsFragment : DialogFragment() {

    private lateinit var pagerAdapter: PagerAdapter
    private var harajList = ArrayList<DataItem>()
    var selectedProductID = ""
    private lateinit var prefManager: PrefManager
    var position: Int = 0
    var harajId: String = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.setNavigationOnClickListener {
            dismiss()
        }
        shareImageView.setOnClickListener {
            val shareBody = String.format(Locale.US, "%s%s", StaticMembers.getBaseURL(), selectedProductID)
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name)
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
            startActivity(
                    Intent.createChooser(
                            sharingIntent,
                            resources.getString(R.string.share_using)
                    )
            )
        }
        pagerAdapter = PagerAdapter(requireActivity())
        if (harajList.isNotEmpty()) {
            for (i in harajList) {
                pagerAdapter.addFragment(HarajDetailsFragment.newInstance(i))
            }
            selectedProductID = harajList[position].id.toString()
        } else {
            if (!selectedProductID.isNullOrEmpty()) {
                pagerAdapter.addFragment(HarajDetailsFragment.newInstance(selectedProductID))
            }
        }
        pager.adapter = pagerAdapter
        pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val id = (pagerAdapter.getItem(position) as HarajDetailsFragment).productId
                if (id.isNotEmpty())
                    selectedProductID = id
            }
        })
        pager.setCurrentItem(position, false)
        pager.offscreenPageLimit = 2

        pagerAdapter.notifyDataSetChanged()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.DialogTransFadeFast)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_haraj_details, container, false)
    }


    var onEdit: () -> Unit = {}

    companion object {
        fun getInstance(harajList: ArrayList<DataItem>, position: Int, onEdit: () -> Unit): HarajAllDetailsFragment {
            val dialog = HarajAllDetailsFragment()
            dialog.harajList = harajList
            dialog.position = position
            dialog.onEdit = onEdit
            return dialog
        }

        fun getInstance(selectedProductID: String): HarajAllDetailsFragment {
            val dialog = HarajAllDetailsFragment()
            dialog.selectedProductID = selectedProductID
            return dialog
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK)
            if (requestCode == StaticMembers.EDIT_CODE)
                onEdit.invoke()
    }
}
