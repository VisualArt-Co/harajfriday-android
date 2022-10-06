package com.benAbdelWahed.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.benAbdelWahed.R
import com.benAbdelWahed.adapters.EditImageAdapter
import com.benAbdelWahed.responses.add_inc_response.AddIncResponse
import com.benAbdelWahed.responses.categories_response.CategoryResponse
import com.benAbdelWahed.responses.cities_response.CitiesResponse
import com.benAbdelWahed.responses.cities_response.City
import com.benAbdelWahed.responses.error_response.ErrorLoginResponse
import com.benAbdelWahed.responses.haraj_responses.DataItem
import com.benAbdelWahed.utils.CallbackRetrofit
import com.benAbdelWahed.utils.PrefManager
import com.benAbdelWahed.utils.RetrofitModel
import com.benAbdelWahed.utils.StaticMembers.*
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.fxn.utility.ImageQuality
import com.fxn.utility.PermUtil
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_edit_haraj.*
import kotlinx.android.synthetic.main.progress_layout.*
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class EditHarajActivity : AppCompatActivity() {

    private var yearModels = ArrayList<String>()
    private var selectedYear: String = ""
    private var categoryResponse: CategoryResponse? = null
    private lateinit var prefManager: PrefManager
    private var countriesResponse: CitiesResponse? = null
    private var citiesResponse: CitiesResponse? = null
    private lateinit var editImageAdapter: EditImageAdapter
    private var countryI: Int = -1
    private var cityI: Int = -1
    private var catI: Int = -1
    private var subCatI: Int = -1
    private var subSubCatI: Int = -1
    private var countries: ArrayList<City>? = null
    private var imagePaths = ArrayList<String>()
    lateinit var options: Options
    var imageLimit = 20
    var isPremium = false
    private lateinit var product: DataItem
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_haraj)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        prefManager = PrefManager.getInstance(this)
        isPremium = prefManager.getBoolean(IS_PREMIUM)
        imageLimit = if (isPremium) 100 else 20
        options = Options.init()
                .setRequestCode(PIX_CODE)                                                 //Request code for activity results
                .setCount(if (imagePaths.size < imageLimit) imageLimit - imagePaths.size else 0)                                       //Number of images to restict selection count
                .setFrontfacing(false)                                                //Front Facing camera on start
                .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)           //Orientation
                .setPath(IMAGES_PATH)!!
        product = intent.getSerializableExtra(PRODUCT) as DataItem
        setupSpinners()
        setupData()
        save.setOnClickListener {
            editProductNow()
        }
    }

    fun setupData() {
        editImageAdapter = EditImageAdapter(this, product.images, imagePaths,imageLimit)
        imageRecycler.adapter = editImageAdapter
        nameEditText.setText(product.title)
        phoneEditText.setText(product.phone)
        description.setText(product.description)
        turnOffComments.isChecked = product.commentable == "0"
    }

    fun setupSpinners() {
        countriesResponse = prefManager.getObject(COUNTRIES, CitiesResponse::class.java) as CitiesResponse?
        citiesResponse = prefManager.getObject(CITIES, CitiesResponse::class.java) as CitiesResponse?
        categoryResponse = prefManager.getObject(CATEGORIES, CategoryResponse::class.java) as CategoryResponse?

        countries = ArrayList()
        var selectedData = ArrayList<String>()
        var adapter = getSpinnerAdapter(this, selectedData)
        countrySpinner.adapter = adapter
        countrySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                hideKeyboard(this@EditHarajActivity)
                if (position > 0) {
                    citySpinner.visibility = View.VISIBLE
                    countryI = position - 1
                    val selectedData = ArrayList<String>()
                    val adapter = getSpinnerAdapter(this@EditHarajActivity, selectedData)
                    citySpinner.adapter = adapter
                    citySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                            if (position > 0) {
                                cityI = position - 1
                            }
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {

                        }
                    }
                    getCities(selectedData, adapter, "" + countries!![countryI].id)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                hideKeyboard(this@EditHarajActivity)
            }
        }
        getCountries(selectedData, adapter)
        selectedData = ArrayList()
        adapter = getSpinnerAdapter(this, selectedData)
        categoriesSpinner.adapter = adapter
        categoriesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                hideKeyboard(this@EditHarajActivity)
                if (position > 0) {
                    subCategoriesSpinner.visibility = View.VISIBLE
                    val p = position - 1
                    if (categoryResponse != null) {
                        catI = p
                        val selectedData = ArrayList<String>()
                        val adapter = getSpinnerAdapter(this@EditHarajActivity, selectedData)
                        subCategoriesSpinner.adapter = adapter
                        subCategoriesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                                if (position > 0) {
                                    if (categoryResponse != null && catI > -1)
                                        subCatI = position - 1
                                    subSubCategoriesSpinner.visibility = View.VISIBLE

                                    val selectedData = ArrayList<String>()
                                    val adapter = getSpinnerAdapter(this@EditHarajActivity, selectedData)
                                    subSubCategoriesSpinner.adapter = adapter
                                    subSubCategoriesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                                            if (position > 0) {
                                                if (categoryResponse != null && catI > -1 && subCatI > -1)
                                                    subSubCatI = position - 1
                                            }
                                        }

                                        override fun onNothingSelected(parent: AdapterView<*>) {

                                        }
                                    }
                                    selectedData.clear()
                                    subSubCatI = -1
                                    selectedData.add(0, getString(R.string.subSubCat))
                                    val subSubList = categoryResponse!!.data[catI]!!.subcategories[subCatI].subsubs
                                    if (catI > -1 && subCatI > -1 && subSubList != null && subSubList.isNotEmpty()) {
                                        for (subSubCat in subSubList) {
                                            selectedData.add(subSubCat.name)
                                        }
                                        adapter.notifyDataSetChanged()

                                        if (product.subsubId != null) {
                                            for (i in 0 until subSubList.size) {
                                                val it = subSubList[i]
                                                if (it.id == product.subsubId.id) {
                                                    subSubCatI = i
                                                    break
                                                }
                                            }
                                            if (subSubCatI > -1)
                                                subSubCategoriesSpinner.setSelection(subSubCatI + 1)
                                        }
                                    } else
                                        subSubCategoriesSpinner.visibility = GONE
                                }
                            }

                            override fun onNothingSelected(parent: AdapterView<*>) {

                            }
                        }
                        selectedData.clear()
                        subCatI = -1
                        selectedData.add(0, getString(R.string.subCat))
                        val subCatList = categoryResponse!!.data[catI]!!.subcategories
                        if (subCatList != null && subCatList.isNotEmpty()) {
                            for (subCat in subCatList) {
                                selectedData.add(subCat.name)
                            }
                            adapter.notifyDataSetChanged()

                            if (product.subcategoryId != null) {
                                for (i in 0 until subCatList.size) {
                                    val it = subCatList[i]
                                    if (it.id == product.subcategoryId.id) {
                                        subCatI = i
                                        break
                                    }
                                }
                                if (subCatI > -1)
                                    subCategoriesSpinner.setSelection(subCatI + 1)
                            }

                        } else
                            subCategoriesSpinner.visibility = GONE

                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                hideKeyboard(this@EditHarajActivity)
            }
        }
        getAllCategories(selectedData, adapter)
        adapter = getSpinnerAdapter(this, yearModels)
        modelSpinner.adapter = adapter
        modelSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (position > 0) {
                    selectedYear = yearModels[position]
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
        getModelYears(adapter)
    }

    private fun getModelYears(adapter: ArrayAdapter<String>) {
        yearModels.add(getString(R.string.choose_model))
        val thisYear = Calendar.getInstance().get(Calendar.YEAR) + 1
        for (i in thisYear downTo 1970) {
            yearModels.add(i.toString())
        }
        var selectedYearIndex = -1
        adapter.notifyDataSetChanged()
        for (i in 0 until yearModels.size) {
            val it = yearModels[i]
            if (it == product.model) {
                selectedYearIndex = i
                break
            }
        }
        if (selectedYearIndex > -1)
            modelSpinner.setSelection(selectedYearIndex)
    }

    private fun editProductNow() {
        checkTextInputEditText(nameEditText, nameLayout, getString(R.string.title_mazad_req))
        checkTextInputEditText(description, descriptionLayout, getString(R.string.desc_mazad_req))
        checkNotNullListSpinner(nestedScrollView, countrySpinner, countries, countryI, getString(R.string.country_req))
        checkNotNullListSpinner(nestedScrollView, citySpinner, citiesResponse!!.data!!, cityI, getString(R.string.city_req))
        checkNotNullListSpinner(nestedScrollView, categoriesSpinner, categoryResponse!!.data, catI, getString(R.string.cat_req))

        if (checkTextInputEditText(nameEditText, nameLayout, getString(R.string.title_mazad_req)) &&
                checkTextInputEditText(description, descriptionLayout, getString(R.string.desc_mazad_req)) &&
                checkNotNullListSpinner(nestedScrollView, countrySpinner, countries, countryI, getString(R.string.country_req)) &&
                checkNotNullListSpinner(nestedScrollView, citySpinner, citiesResponse!!.data!!, cityI, getString(R.string.city_req)) &&
                checkNotNullListSpinner(nestedScrollView, categoriesSpinner, categoryResponse!!.data, catI, getString(R.string.cat_req))
        ) {
            val builder = MultipartBody.Builder()
            builder.setType(MultipartBody.FORM)
            var file: File
            for (img in imagePaths) {
                file = File(img)
                builder.addFormDataPart("images[]", file.name, RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file))
            }

            for (img in editImageAdapter.deletedList) {
                builder.addFormDataPart("deleteImages[]", "$img")
            }
            builder.addFormDataPart("title", nameEditText.text.toString())
            builder.addFormDataPart("description", description.text.toString())
            builder.addFormDataPart("product_id", product.id.toString())
            builder.addFormDataPart("price", "0")
            builder.addFormDataPart("commentable", if (turnOffComments.isChecked && isPremium) "0" else "1")
            if (phoneEditText.text.toString().isNotEmpty())
                builder.addFormDataPart("phone", phoneEditText.text.toString())
            if (selectedYear.isNotEmpty())
                builder.addFormDataPart("model", selectedYear)
            builder.addFormDataPart("city_id", "${countries!![countryI].id}")
            builder.addFormDataPart("state_id", "${citiesResponse!!.data[cityI].id}")
            builder.addFormDataPart(CATEGORY_ID, "${categoryResponse!!.data[catI].id}")
            if (subCatI > -1 && categoryResponse!!.data[catI].subcategories != null) {
                builder.addFormDataPart(SUBCATEGORY_ID, "${categoryResponse!!.data[catI].subcategories[subCatI].id}")
                if (subSubCatI > -1 && categoryResponse!!.data[catI].subcategories[subCatI].subsubs != null)
                    builder.addFormDataPart(SUBSUBCATEGORY_ID, "${categoryResponse!!.data[catI].subcategories[subCatI].subsubs[subSubCatI].id}")
            }
            progress.visibility = View.VISIBLE
            val call = RetrofitModel.getApi(this).editProduct(builder.build())
            call.enqueue(object : CallbackRetrofit<AddIncResponse>(this) {
                override fun onResponse(call: Call<AddIncResponse>, response: Response<AddIncResponse>) {
                    progress.visibility = View.GONE
                    val result = response.body()
                    if (response.isSuccessful && result != null) {
                        toastMessageShort(this@EditHarajActivity, "تم التعديل بنجاح")
                        setResult(Activity.RESULT_OK)
                        onBackPressed()
                    } else {
                        checkError(this@EditHarajActivity, response.errorBody())
                    }
                }

                override fun onFailure(call: Call<AddIncResponse>, t: Throwable) {
                    super.onFailure(call, t)
                    progress.visibility = GONE
                }
            })
        }
    }

    private fun updateCountriesView(selectedData: ArrayList<String>, adapter: ArrayAdapter<String>) {
        progress.visibility = View.GONE
        countries!!.clear()
        selectedData.clear()
        countryI = -1
        selectedData.add(0, getString(R.string.country_r))
        countries!!.addAll(countriesResponse!!.data)
        for (country in countries!!) {
            selectedData.add(country.name)
        }
        adapter.notifyDataSetChanged()
        for (i in 0 until countries!!.size) {
            val it = countries!![i]
            if (it.id == product.city.id) {
                countryI = i
                break
            }
        }
        if (countryI > -1)
            countrySpinner.setSelection(countryI + 1)
    }

    private fun updateCitiesView(selectedData: ArrayList<String>, adapter: ArrayAdapter<String>) {
        selectedData.clear()
        cityI = -1
        selectedData.add(0, getString(R.string.city_r))
        for (city in citiesResponse!!.data) {
            selectedData.add(city.name)
        }
        adapter.notifyDataSetChanged()
        if (selectedData.isNotEmpty())
            citySpinner.visibility = View.VISIBLE
        for (i in 0 until citiesResponse!!.data!!.size) {
            val it = citiesResponse!!.data!![i]
            if (it.id == product.state.id) {
                cityI = i
                break
            }
        }
        if (cityI > -1)
            citySpinner.setSelection(cityI + 1)
    }

    private fun updateCategoriesView(selectedData: ArrayList<String>, adapter: ArrayAdapter<String>) {
        selectedData.clear()
        catI = -1
        selectedData.add(0, getString(R.string.category_r))
        for (cat in categoryResponse!!.data) {
            selectedData.add(cat.name)
        }
        adapter.notifyDataSetChanged()
        for (i in 0 until categoryResponse!!.data!!.size) {
            val it = categoryResponse!!.data!![i]
            if (it.id == product.category.id) {
                catI = i
                break
            }
        }
        if (catI > -1)
            categoriesSpinner.setSelection(catI + 1)
    }

    private fun getCountries(selectedData: ArrayList<String>, adapter: ArrayAdapter<String>) {
        progress.visibility = View.VISIBLE
        selectedData.clear()
        countryI = -1
        selectedData.add(0, getString(R.string.country_r))
        adapter.notifyDataSetChanged()
        if (countriesResponse != null && countriesResponse!!.data != null) {
            updateCountriesView(selectedData, adapter)
        }
        val call = RetrofitModel.getApi(this).cities()
        call.enqueue(object : CallbackRetrofit<CitiesResponse>(this) {
            override fun onResponse(call: Call<CitiesResponse>, response: Response<CitiesResponse>) {
                val result = response.body()
                if (response.isSuccessful && result != null) {
                    countriesResponse = result
                    updateCountriesView(selectedData, adapter)
                    prefManager.setObject(COUNTRIES, countriesResponse)
                } else {
                    try {
                        var errorCitiesResponse: ErrorLoginResponse? = null
                        if (response.errorBody() != null) {
                            errorCitiesResponse = GsonBuilder().create().fromJson<Any>(response.errorBody()!!.string(), ErrorLoginResponse::class.java) as ErrorLoginResponse?
                            if (errorCitiesResponse != null) {
                                toastMessageShort(baseContext, errorCitiesResponse.message)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        toastMessageShort(baseContext, R.string.connection_error)
                    }

                }
            }

            override fun onFailure(call: Call<CitiesResponse>, t: Throwable) {
                super.onFailure(call, t)
                progress.visibility = View.GONE

            }
        })
    }

    private fun getCities(selectedData: ArrayList<String>, adapter: ArrayAdapter<String>, countryId: String) {
        if (citiesResponse != null && citiesResponse!!.data != null) {
            updateCitiesView(selectedData, adapter)
        }
        val call = RetrofitModel.getApi(this).states(countryId)
        call.enqueue(object : CallbackRetrofit<CitiesResponse>(this) {
            override fun onResponse(call: Call<CitiesResponse>, response: Response<CitiesResponse>) {
                val result = response.body()
                if (response.isSuccessful && result != null) {
                    citiesResponse = result
                    updateCitiesView(selectedData, adapter)
                    prefManager.setObject(CITIES, citiesResponse)
                } else {
                    try {
                        var errorCitiesResponse: ErrorLoginResponse? = null
                        if (response.errorBody() != null) {
                            errorCitiesResponse = GsonBuilder().create().fromJson<Any>(response.errorBody()!!.string(), ErrorLoginResponse::class.java) as ErrorLoginResponse?
                            if (errorCitiesResponse != null) {
                                toastMessageShort(baseContext, errorCitiesResponse.message)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        toastMessageShort(baseContext, R.string.connection_error)
                    }

                }
            }

            override fun onFailure(call: Call<CitiesResponse>, t: Throwable) {
                super.onFailure(call, t)
                toastMessageShort(baseContext, R.string.connection_error)
            }
        })
    }

    fun getAllCategories(selectedData: ArrayList<String>, adapter: ArrayAdapter<String>) {
        if (categoryResponse != null && categoryResponse!!.data != null) {
            updateCategoriesView(selectedData, adapter)
        }
        val call = RetrofitModel.getApi(this).allCategories
        call.enqueue(object : CallbackRetrofit<CategoryResponse>(this) {
            override fun onResponse(call: Call<CategoryResponse>, response: Response<CategoryResponse>) {
                val result = response.body()
                if (response.isSuccessful && result != null) {
                    categoryResponse = result
                    updateCategoriesView(selectedData, adapter)
                    prefManager.setObject(CATEGORIES, categoryResponse)
                } else {
                    toastMessageShort(baseContext, R.string.connection_error)
                }

            }

            override fun onFailure(call: Call<CategoryResponse>, t: Throwable) {
                super.onFailure(call, t)
                toastMessageShort(baseContext, R.string.connection_error)
            }
        })
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        when (requestCode) {
            PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Pix.start(this@EditHarajActivity, options)
                } else {
                    toastMessageShort(this, getString(R.string.permission_image_picker))
                }
                return
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PIX_CODE -> {
                    if (data!!.getStringArrayListExtra(Pix.IMAGE_RESULTS) != null) {
                        imagePaths.addAll(data.getStringArrayListExtra(Pix.IMAGE_RESULTS))
                        editImageAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }
}

