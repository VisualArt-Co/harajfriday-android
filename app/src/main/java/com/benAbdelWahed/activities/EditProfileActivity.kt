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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.benAbdelWahed.R
import com.benAbdelWahed.responses.add_inc_response.AddIncResponse
import com.benAbdelWahed.responses.chat_responses.UserItem
import com.benAbdelWahed.responses.cities_response.CitiesResponse
import com.benAbdelWahed.responses.cities_response.City
import com.benAbdelWahed.responses.customer_response.ProfileResponse
import com.benAbdelWahed.responses.error_response.ErrorLoginResponse
import com.benAbdelWahed.responses.haraj_responses.Customer
import com.benAbdelWahed.utils.CallbackRetrofit
import com.benAbdelWahed.utils.PrefManager
import com.benAbdelWahed.utils.RetrofitModel
import com.benAbdelWahed.utils.StaticMembers.*
import com.bumptech.glide.Glide
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.fxn.utility.ImageQuality
import com.fxn.utility.PermUtil
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_chat_room.*
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.progress_layout.*
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File

class EditProfileActivity : AppCompatActivity() {
    private lateinit var instanceId: String
    private lateinit var prefManager: PrefManager
    private var countriesResponse: CitiesResponse? = null
    private var citiesResponse: CitiesResponse? = null
    private var selectedPhoto: String? = ""
    private var countryI: Int = -1
    private var cityI: Int = -1
    private var countries: ArrayList<City>? = null
    private var params: HashMap<String, String>? = null
    private lateinit var myUser: Customer
    private val options = Options.init()
            .setRequestCode(PIX_CODE)                                                 //Request code for activity results
            .setCount(1)                                                         //Number of images to restict selection count
            .setFrontfacing(true)                                                //Front Facing camera on start

            .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)           //Orientaion
            .setPath(IMAGES_PATH)!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        backButton.setOnClickListener {
            onBackPressed()
        }
        prefManager = PrefManager.getInstance(this)
        myUser = PrefManager.getInstance(this).getObject(PROFILE, Customer::class.java) as Customer
        if (myUser.image != null && myUser.image.isNotEmpty())
            Glide.with(applicationContext).load(myUser.image).centerCrop().placeholder(R.drawable.place_holder_logo).into(userPhoto)
        else
            userPhoto.setImageResource(R.drawable.place_holder_logo)

        editPassword.setOnClickListener {
            startActivity(Intent(this, ChangePasswordActivity::class.java))
        }
        nameEditText.setText(myUser.fullName)
        phoneEditText.setText(myUser.phone)
        emailEditText.setText(myUser.email)
        usernameText.text = myUser.user_name
        //usernameText.visibility =  if (myUser.isPremium) VISIBLE else GONE

        params = HashMap()
        countries = ArrayList()
        selectPhotoLayout.setOnClickListener {
            Pix.start(this, options)
        }
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener {
            if (it.isSuccessful && it.result != null) {
                instanceId = it.result!!.token
            }
        }
        val selectedData = ArrayList<String>()
        val adapter = getSpinnerAdapter(this, selectedData)
        countriesResponse = prefManager.getObject(COUNTRIES, CitiesResponse::class.java) as CitiesResponse?
        citiesResponse = prefManager.getObject(CITIES, CitiesResponse::class.java) as CitiesResponse?
        citySpinner.visibility = GONE
        countrySpinner.adapter = adapter
        countrySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                hideKeyboard(this@EditProfileActivity)
                if (position > 0) {
                    citySpinner.visibility = VISIBLE
                    val p = position - 1
                    countryI = p
                    val selectedData = ArrayList<String>()
                    val adapter = getSpinnerAdapter(this@EditProfileActivity, selectedData)
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
                    getCities(selectedData, adapter, "" + countries!![p].id)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                hideKeyboard(this@EditProfileActivity)
            }
        }
        getCountries(selectedData, adapter)
        save.setOnClickListener {
            save.isClickable = false
            showAlert()
        }
    }


    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.alert)
        builder.setMessage(getString(R.string.alert_name_tri))
        builder.setPositiveButton(getString(R.string.ok)) { dialog, which -> editProfile() }
        builder.setNegativeButton(getString(R.string.cancel), null)
        builder.show()
    }

    private fun editProfile() {
        checkTextInputEditText(nameEditText, nameLayout, getString(R.string.name_req))
        checkTextInputEditText(phoneEditText, phoneLayout, getString(R.string.phone_req))
        checkTextInputEditText(emailEditText, emailLayout, getString(R.string.email_req))
        checkNotNullListSpinner(nestedScrollView, countrySpinner, countries, countryI, getString(R.string.country_req))
        checkNotNullListSpinner(nestedScrollView, citySpinner, citiesResponse!!.data!!, cityI, getString(R.string.city_req))
        if (
                checkTextInputEditText(nameEditText, nameLayout, getString(R.string.name_req)) &&
                checkTextInputEditText(phoneEditText, phoneLayout, getString(R.string.name_req)) &&
                checkTextInputEditText(emailEditText, emailLayout, getString(R.string.email_req)) &&
                checkNotNullListSpinner(nestedScrollView, citySpinner, citiesResponse!!.data!!, cityI, getString(R.string.city_req)) &&
                checkNotNullListSpinner(nestedScrollView, countrySpinner, countries, countryI, getString(R.string.country_req))
        ) {
            val builder = MultipartBody.Builder()
            builder.setType(MultipartBody.FORM)
            if (selectedPhoto != null && selectedPhoto!!.isNotEmpty()) {
                val file = File(selectedPhoto)
                builder.addFormDataPart("image", file.name, RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file))
            }
            builder.addFormDataPart("email", emailEditText.text.toString())
            builder.addFormDataPart("user_name", usernameText.text.toString())
            builder.addFormDataPart("full_name", nameEditText.text.toString())
            builder.addFormDataPart("phone", phoneEditText.text.toString())
            builder.addFormDataPart("city_id", "${countries!![countryI].id}")
            builder.addFormDataPart("state_id", "${citiesResponse!!.data[cityI].id}")
            progress.visibility = VISIBLE
            val call = RetrofitModel.getApi(this).editProfile(builder.build())
            call.enqueue(object : CallbackRetrofit<AddIncResponse>(this) {
                override fun onResponse(call: Call<AddIncResponse>, response: Response<AddIncResponse>) {
                    progress.visibility = GONE
                    val result = response.body()
                    if (response.isSuccessful && result != null) {
                        getProfile()
                        setResult(Activity.RESULT_OK)
                    } else {
                        save.isClickable = true
                        progress.visibility = GONE
                        try {
                            var errorResponse: ErrorLoginResponse? = null
                            if (response.errorBody() != null) {
                                errorResponse = GsonBuilder().create().fromJson<Any>(response.errorBody()!!.string(), ErrorLoginResponse::class.java) as ErrorLoginResponse?
                                if (errorResponse != null && errorResponse.message != null) {
                                    toastMessageShort(baseContext, errorResponse.message)
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            toastMessageShort(baseContext, R.string.connection_error)
                        }

                    }
                }

                override fun onFailure(call: Call<AddIncResponse>, t: Throwable) {
                    super.onFailure(call, t)
                    progress.visibility = GONE
                    save.isClickable = true
                }
            })
        }
    }


    private fun getProfile() {
        progress.visibility = VISIBLE
        val call = RetrofitModel.getApi(this).profile
        call.enqueue(object : CallbackRetrofit<ProfileResponse>(this) {
            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                progress.visibility = GONE
                val result = response.body()
                if (response.isSuccessful && result != null) {
                    PrefManager.getInstance(baseContext).setObject(PROFILE, result.data)
                    val user = UserItem(result.data.fullName, result.data.user_name, result.data.id, result.data.image, null)
                    FirebaseDatabase.getInstance().reference.child(USERS).child("${user.id}").child(INFO).setValue(user).addOnCompleteListener {
                        progress.visibility = GONE
                        toastMessageShort(baseContext, R.string.saved)
                        finish()
                    }

                } else {
                    progress.visibility = GONE
                    try {
                        var errorLoginResponse: ErrorLoginResponse? = null
                        if (response.errorBody() != null) {
                            errorLoginResponse = GsonBuilder().create().fromJson<Any>(response.errorBody()!!.string(), ErrorLoginResponse::class.java) as ErrorLoginResponse?
                            if (errorLoginResponse != null) {
                                toastMessageShort(baseContext, errorLoginResponse.message)
                                if (errorLoginResponse.data.password != null && errorLoginResponse.data.password.isNotEmpty()) {
                                    toastMessageShort(baseContext, errorLoginResponse.data.password[0])
                                }
                                if (errorLoginResponse.data.phone != null && errorLoginResponse.data.phone.isNotEmpty()) {
                                    toastMessageShort(baseContext, errorLoginResponse.data.phone[0])
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        toastMessageShort(baseContext, R.string.connection_error)
                    }
                    finish()
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                super.onFailure(call, t)
                progress.visibility = GONE
                finish()
            }
        })

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
        prefManager.setObject(COUNTRIES, countriesResponse)
        for (i in 0 until countries!!.size) {
            val it = countries!![i]
            if ("${it.id}" == myUser.cityId) {
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
            citySpinner.visibility = VISIBLE
        prefManager.setObject(CITIES, citiesResponse)
        for (i in 0 until citiesResponse!!.data!!.size) {
            val it = citiesResponse!!.data!![i]
            if ("${it.id}" == myUser.stateId) {
                cityI = i
                break
            }
        }
        if (cityI > -1)
            citySpinner.setSelection(cityI + 1)
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
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PIX_CODE -> {
                    if (data!!.getStringArrayListExtra(Pix.IMAGE_RESULTS) != null) {
                        val list = data.getStringArrayListExtra(Pix.IMAGE_RESULTS)
                        Picasso.get().load("file://" + list!![0])
                                .placeholder(R.drawable.place_holder_logo).fit().centerCrop().into(userPhoto)
                        selectedPhoto = list[0]
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        when (requestCode) {
            PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Pix.start(this, options)
                } else {
                    toastMessageLong(this, getString(R.string.approve_to_open_gallery))
                }
                return
            }
        }
    }

}
