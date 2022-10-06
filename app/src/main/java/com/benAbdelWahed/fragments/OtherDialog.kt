package com.benAbdelWahed.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.benAbdelWahed.R
import com.benAbdelWahed.adapters.OtherAdapter
import com.benAbdelWahed.responses.other_response.DataItem
import com.benAbdelWahed.responses.other_response.DynamicPagesResponse
import com.benAbdelWahed.responses.settings_response.SettingsData
import com.benAbdelWahed.utils.CallbackRetrofit
import com.benAbdelWahed.utils.PrefManager
import com.benAbdelWahed.utils.RetrofitModel
import com.benAbdelWahed.utils.StaticMembers.DYNAMIC_PAGES
import kotlinx.android.synthetic.main.fg_other.*
import retrofit2.Call
import retrofit2.Response

class OtherDialog : DialogFragment() {
    private lateinit var prefManager: PrefManager
    private lateinit var otherAdapter: OtherAdapter
    private var data: DynamicPagesResponse? = null
    private val list = ArrayList<DataItem>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefManager = PrefManager.getInstance(context)
        backButton!!.setOnClickListener { v -> dismiss() }
        titleText!!.setText(R.string.other)
        data = prefManager.getObject(DYNAMIC_PAGES, DynamicPagesResponse::class.java) as DynamicPagesResponse?
        otherAdapter = OtherAdapter(activity, list)
        recycler.adapter = otherAdapter
        getDynamicData()
    }


    fun updateUI() {
        list.clear()
        if (data != null)
            list.addAll(data!!.data)
        otherAdapter.notifyDataSetChanged()
    }

    fun getDynamicData() {
        updateUI()
        val call = RetrofitModel.getApi(context).dynamicPages
        call.enqueue(object : CallbackRetrofit<DynamicPagesResponse>(context) {
            override fun onResponse(call: Call<DynamicPagesResponse>, response: Response<DynamicPagesResponse>) {
                if (response.isSuccessful) {
                    if (response.body() != null && response.body()!!.data != null) {
                        data = response.body()
                        prefManager.setObject(DYNAMIC_PAGES, data)
                        updateUI()
                    }
                }
            }

            override fun onFailure(call: Call<DynamicPagesResponse>, t: Throwable) {
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.DialogTrans)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fg_other, container, false)
    }
}
