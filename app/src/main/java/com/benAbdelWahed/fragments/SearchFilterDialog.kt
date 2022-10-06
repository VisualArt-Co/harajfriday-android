package com.benAbdelWahed.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.benAbdelWahed.R
import com.benAbdelWahed.activities.HarajSearchResultsActivity
import com.benAbdelWahed.responses.categories_response.CategoryResponse
import com.benAbdelWahed.utils.CallbackRetrofit
import com.benAbdelWahed.utils.PrefManager
import com.benAbdelWahed.utils.RetrofitModel
import com.benAbdelWahed.utils.StaticMembers
import com.benAbdelWahed.utils.StaticMembers.*
import kotlinx.android.synthetic.main.fg_search_filter.*
import retrofit2.Call
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


class SearchFilterDialog : DialogFragment() {


    private lateinit var yearList: ArrayList<String>
    private var selectedCat: Int = -1
    private var selectedSubCat: Int = -1
    private var carCatIndex: Int = -1
    private var selectedYear: Int = -1
    private var categoryResponse: CategoryResponse? = null
    private lateinit var prefManager: PrefManager
    private lateinit var carCatList: ArrayList<String>
    private lateinit var carSubCatList: ArrayList<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        backButton.setOnClickListener {
            dismiss()
        }
        markaLayout.setOnClickListener {
            showMarkaAlert()
        }
        typeLayout.setOnClickListener {
            if (carSubCatList.isEmpty())
                showMarkaAlert()
            else showModelAlert()
        }
        modelLayout.setOnClickListener {
            showModelYearAlert()
        }
        prefManager = PrefManager.getInstance(context)
        categoryResponse = prefManager.getObject(StaticMembers.CATEGORIES, CategoryResponse::class.java) as CategoryResponse?
        carCatList = ArrayList()
        carSubCatList = ArrayList()
        yearList = ArrayList()
        setupCategories()
        setupYears()
        search.setOnClickListener {
            val intent = Intent(activity, HarajSearchResultsActivity::class.java)
            val s = searchEditText.text.toString()
            if (s.isNotEmpty())
                intent.putExtra(TEXT, s)
            if (selectedCat > -1) {
                val car = categoryResponse!!.data[carCatIndex]
                intent.putExtra(CATEGORY_ID, car.id)
                intent.putExtra(SUBCATEGORY_ID, car.subcategories[selectedCat].id)
                if (selectedSubCat > -1 && car.subcategories[selectedCat].subsubs != null && car.subcategories[selectedCat].subsubs.isNotEmpty())
                    intent.putExtra(SUBSUBCATEGORY_ID, car.subcategories[selectedCat].subsubs[selectedSubCat].id)
            }
            if (selectedYear > -1)
                intent.putExtra(MODEL, yearList[selectedYear])

            activity!!.startActivity(intent)
        }
    }

    private fun setupYears() {
        yearList.clear()
        val thisYear = Calendar.getInstance().get(Calendar.YEAR) + 1
        for (i in thisYear downTo 1970) {
            yearList.add(i.toString())
        }
    }

    private fun showModelYearAlert() {
        val builder = AlertDialog.Builder(activity!!, R.style.myFullscreenAlertDialogStyle)
        val titleView = layoutInflater.inflate(R.layout.custom_title_alert, null)
        val textView = titleView.findViewById<TextView>(R.id.alertTitle)
        val backButton = titleView.findViewById<ImageView>(R.id.backButton)
        textView.setText(R.string.choose_model)
        builder.setCustomTitle(titleView)
        builder.setSingleChoiceItems(yearList.toTypedArray(), selectedYear) { dialog, which ->
            chooseYear(which)
            dialog.dismiss()
        }
        val dialog = builder.create()
        backButton.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun chooseYear(which: Int) {
        selectedYear = which
        if (which < yearList.size) {
            modelText.text = yearList[which]
        }
    }


    fun setupCategories() {
        if (categoryResponse != null && categoryResponse!!.data != null) {
            updateCategoriesView()
        }
        val call = RetrofitModel.getApi(context).allCategories
        call.enqueue(object : CallbackRetrofit<CategoryResponse>(context) {
            override fun onResponse(call: Call<CategoryResponse>, response: Response<CategoryResponse>) {
                val result = response.body()
                if (response.isSuccessful && result != null) {
                    categoryResponse = result
                    updateCategoriesView()
                    prefManager.setObject(StaticMembers.CATEGORIES, categoryResponse)
                } else {
                    StaticMembers.toastMessageShort(context, R.string.connection_error)
                }

            }

            override fun onFailure(call: Call<CategoryResponse>, t: Throwable) {
                super.onFailure(call, t)
                StaticMembers.toastMessageShort(context, R.string.connection_error)
            }
        })
    }

    private fun updateCategoriesView() {
        if (categoryResponse != null) {
            carCatList.clear()
            for (i in 0 until categoryResponse!!.data.size) {
                val category = categoryResponse!!.data[i]
                if ((category.name.contains("سيار") || category.name.contains("car")) && category.subcategories != null) {
                    carCatIndex = i
                    category.subcategories.forEach {
                        carCatList.add(it.name)
                    }
                    break
                }
            }
        }
    }

    private fun chooseCatCar(which: Int) {
        if (categoryResponse != null) {
            if (selectedCat != which) {
                typeText.text = ""
                selectedSubCat = -1
            }
            selectedCat = which
            if (which < carCatList.size) {
                marka.text = carCatList[which]
                carSubCatList.clear()
                val subSubs = categoryResponse!!.data[carCatIndex].subcategories[which].subsubs
                if (subSubs != null)
                    subSubs.forEach {
                        carSubCatList.add(it.name)
                    }
                typeLayout.visibility = if (carSubCatList.isEmpty()) GONE else VISIBLE
            }
        }
    }

    private fun showMarkaAlert() {
        val builder = AlertDialog.Builder(activity!!, R.style.myFullscreenAlertDialogStyle)
        val titleView = layoutInflater.inflate(R.layout.custom_title_alert, null)
        val textView = titleView.findViewById<TextView>(R.id.alertTitle)
        val backButton = titleView.findViewById<ImageView>(R.id.backButton)
        textView.setText(R.string.choose_marka)
        builder.setCustomTitle(titleView)
        builder.setSingleChoiceItems(carCatList.toTypedArray(), selectedCat) { dialog, which ->
            chooseCatCar(which)
            dialog.dismiss()
        }
        val dialog = builder.create()
        backButton.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun showModelAlert() {
        val builder = AlertDialog.Builder(activity!!, R.style.myFullscreenAlertDialogStyle)
        val titleView = layoutInflater.inflate(R.layout.custom_title_alert, null)
        val textView = titleView.findViewById<TextView>(R.id.alertTitle)
        val backButton = titleView.findViewById<ImageView>(R.id.backButton)
        textView.setText(R.string.choose_type)
        builder.setCustomTitle(titleView)
        builder.setSingleChoiceItems(carSubCatList.toTypedArray(), selectedSubCat) { dialog, which ->
            chooseSubCatCar(which)
            dialog.dismiss()
        }
        val dialog = builder.create()
        backButton.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun chooseSubCatCar(which: Int) {
        selectedSubCat = which
        if (which < carSubCatList.size) {
            typeText.text = carSubCatList[which]
        }
    }

    companion object {
        fun getInstance(): SearchFilterDialog {
            return SearchFilterDialog()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.DialogTrans)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fg_search_filter, container, false)
    }

    interface OnActionListener {
        fun onConfirm(filters: HashMap<String, String>)
    }
}
