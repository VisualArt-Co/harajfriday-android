package com.benAbdelWahed.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.benAbdelWahed.R
import com.benAbdelWahed.adapters.AddImageAdapter
import com.benAbdelWahed.responses.add_inc_response.AddIncResponse
import com.benAbdelWahed.responses.categories_response.CategoryResponse
import com.benAbdelWahed.responses.cities_response.CitiesResponse
import com.benAbdelWahed.responses.cities_response.City
import com.benAbdelWahed.responses.error_response.ErrorLoginResponse
import com.benAbdelWahed.utils.CallbackRetrofit
import com.benAbdelWahed.utils.PrefManager
import com.benAbdelWahed.utils.RetrofitModel
import com.benAbdelWahed.utils.StaticMembers.*
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.fxn.utility.PermUtil
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_add_haraj.*
import kotlinx.android.synthetic.main.progress_layout.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class AddHarajActivity : AppCompatActivity() {

    private var selectedYear: String = ""
    private var categoryResponse: CategoryResponse? = null
    private lateinit var prefManager: PrefManager
    private var countriesResponse: CitiesResponse? = null
    private var citiesResponse: CitiesResponse? = null
    private lateinit var addAdapter: AddImageAdapter
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_haraj)
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

        countriesResponse = prefManager.getObject(COUNTRIES, CitiesResponse::class.java) as CitiesResponse?
        citiesResponse = prefManager.getObject(CITIES, CitiesResponse::class.java) as CitiesResponse?
        categoryResponse = prefManager.getObject(CATEGORIES, CategoryResponse::class.java) as CategoryResponse?


        countries = ArrayList()
        var selectedData = ArrayList<String>()
        var adapter = getSpinnerAdapter(this, selectedData)
        countrySpinner.adapter = adapter
        countrySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                hideKeyboard(this@AddHarajActivity)
                if (position > 0) {
                    citySpinner.visibility = VISIBLE
                    val p = position - 1
                    if (countriesResponse != null)
                        countryI = countriesResponse!!.data[p].id
                    val selectedData = ArrayList<String>()
                    val adapter = getSpinnerAdapter(this@AddHarajActivity, selectedData)
                    citySpinner.adapter = adapter
                    citySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                            if (position > 0) {
                                if (citiesResponse != null)
                                    cityI = citiesResponse!!.data[position - 1].id
                            }
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {

                        }
                    }
                    getCities(selectedData, adapter, "" + countries!![p].id)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                hideKeyboard(this@AddHarajActivity)
            }
        }
        getCountries(selectedData, adapter)
        selectedData = ArrayList()
        adapter = getSpinnerAdapter(this, selectedData)
        categoriesSpinner.adapter = adapter
        categoriesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                hideKeyboard(this@AddHarajActivity)
                if (position > 0) {
                    subCategoriesSpinner.visibility = VISIBLE
                    modelSpinner.visibility = when {
                        position != 1 ->  GONE
                        else ->  VISIBLE
                    }
                    val p = position - 1
                    if (categoryResponse != null) {
                        catI = p
                        val selectedData = ArrayList<String>()
                        val adapter = getSpinnerAdapter(this@AddHarajActivity, selectedData)
                        subCategoriesSpinner.adapter = adapter
                        subCategoriesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                                if (position > 0) {
                                    if (categoryResponse != null && catI > -1)
                                        subCatI = position - 1
                                    subSubCategoriesSpinner.visibility = VISIBLE

                                    val selectedData = ArrayList<String>()
                                    val adapter = getSpinnerAdapter(this@AddHarajActivity, selectedData)
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
                                    if (catI > -1 && subCatI > -1 && categoryResponse!!.data[catI]!!.subcategories[subCatI].subsubs != null && categoryResponse!!.data[catI]!!.subcategories[subCatI].subsubs.isNotEmpty()) {
                                        for (subSubCat in categoryResponse!!.data[catI]!!.subcategories[subCatI].subsubs) {
                                            selectedData.add(subSubCat.name)
                                        }
                                        adapter.notifyDataSetChanged()
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
                        if (categoryResponse!!.data[catI]!!.subcategories != null && categoryResponse!!.data[catI]!!.subcategories.isNotEmpty()) {
                            for (subCat in categoryResponse!!.data[catI]!!.subcategories) {
                                selectedData.add(subCat.name)
                            }
                            adapter.notifyDataSetChanged()
                        } else
                            subCategoriesSpinner.visibility = GONE

                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                hideKeyboard(this@AddHarajActivity)
            }
        }
        getAllCategories(selectedData, adapter)
        selectedData = ArrayList()
        adapter = getSpinnerAdapter(this, selectedData)
        modelSpinner.adapter = adapter
        modelSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (position > 0) {
                    selectedYear = selectedData[position]
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        turnOffComments.visibility = if (isPremium) VISIBLE else GONE
        getModelYears(selectedData, adapter)
        addAdapter = AddImageAdapter(this, imagePaths, imageLimit)
        imageRecycler.adapter = addAdapter
        addMazad.setOnClickListener {
            addNewHaraj()
        }
    }

    private fun getModelYears(selectedData: ArrayList<String>, adapter: ArrayAdapter<String>) {
        selectedData.add(getString(R.string.choose_model))
        val thisYear = Calendar.getInstance().get(Calendar.YEAR) + 1
        for (i in thisYear downTo 1970) {
            selectedData.add(i.toString())
        }
        adapter.notifyDataSetChanged()
    }

    private fun addNewHaraj() {
        checkTextInputEditText(mazadTitle, mazadTitleLayout, getString(R.string.title_mazad_req))
        checkTextInputEditText(description, descriptionLayout, getString(R.string.desc_mazad_req))
        checkNotNullListSpinner(nestedScrollView, countrySpinner, countries, countryI, getString(R.string.country_req))
        checkNotNullListSpinner(nestedScrollView, citySpinner, citiesResponse!!.data!!, cityI, getString(R.string.city_req))
        checkNotNullListSpinner(nestedScrollView, categoriesSpinner, categoryResponse!!.data, catI, getString(R.string.cat_req))
        if (checkTextInputEditText(mazadTitle, mazadTitleLayout, getString(R.string.title_mazad_req)) &&
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
            builder.addFormDataPart("title", mazadTitle.text.toString())
            builder.addFormDataPart("description", description.text.toString())
            builder.addFormDataPart("price", "0")
            builder.addFormDataPart("commentable", if (turnOffComments.isChecked && isPremium) "0" else "1")
            if (phoneEditText.text.toString().isNotEmpty())
                builder.addFormDataPart("phone", phoneEditText.text.toString())
            if (selectedYear.isNotEmpty())
                builder.addFormDataPart("model", selectedYear)
            builder.addFormDataPart("city_id", "$countryI")
            if (cityI > -1)
                builder.addFormDataPart("state_id", "$cityI")
            builder.addFormDataPart(CATEGORY_ID, "${categoryResponse!!.data[catI].id}")
            if (subCatI > -1 && categoryResponse!!.data[catI].subcategories != null) {
                builder.addFormDataPart(SUBCATEGORY_ID, "${categoryResponse!!.data[catI].subcategories[subCatI].id}")
                if (subSubCatI > -1 && categoryResponse!!.data[catI].subcategories[subCatI].subsubs != null)
                    builder.addFormDataPart(SUBSUBCATEGORY_ID, "${categoryResponse!!.data[catI].subcategories[subCatI].subsubs[subSubCatI].id}")
            }
            progress.visibility = VISIBLE
            val call = RetrofitModel.getApi(this).addHaraj(builder.build())
            call.enqueue(object : CallbackRetrofit<AddIncResponse>(this) {
                override fun onResponse(call: Call<AddIncResponse>, response: Response<AddIncResponse>) {
                    progress.visibility = GONE
                    val result = response.body()
                    if (response.isSuccessful && result != null) {
                        toastMessageShort(this@AddHarajActivity, R.string.done_add_product)
                        onBackPressed()
                    } else {
                        checkError(this@AddHarajActivity, response.errorBody())
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
        progress.visibility = GONE
        countries!!.clear()
        selectedData.clear()
        countryI = -1
        selectedData.add(0, getString(R.string.country_r))
        countries!!.addAll(countriesResponse!!.data)
        for (country in countries!!) {
            selectedData.add(country.name)
        }
        adapter.notifyDataSetChanged()
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
            citySpinner.visibility = VISIBLE
    }

    private fun updateCategoriesView(selectedData: ArrayList<String>, adapter: ArrayAdapter<String>) {
        selectedData.clear()
        catI = -1
        selectedData.add(0, getString(R.string.category_r))
        for (cat in categoryResponse!!.data) {
            selectedData.add(cat.name)
        }
        adapter.notifyDataSetChanged()
    }

    private fun getCountries(selectedData: ArrayList<String>, adapter: ArrayAdapter<String>) {
        progress.visibility = VISIBLE
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
                progress.visibility = GONE

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
                    Pix.start(this@AddHarajActivity, options)
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
                        addAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }
}
