package com.benAbdelWahed.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.benAbdelWahed.R
import com.benAbdelWahed.responses.cities_response.CitiesResponse
import com.benAbdelWahed.responses.cities_response.City
import com.benAbdelWahed.responses.error_response.ErrorLoginResponse
import com.benAbdelWahed.utils.CallbackRetrofit
import com.benAbdelWahed.utils.PrefManager
import com.benAbdelWahed.utils.RetrofitModel
import com.benAbdelWahed.utils.StaticMembers
import com.benAbdelWahed.utils.StaticMembers.CITY
import com.benAbdelWahed.utils.StaticMembers.STATE
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_add_haraj.citySpinner
import kotlinx.android.synthetic.main.activity_add_haraj.countrySpinner
import kotlinx.android.synthetic.main.fg_country_filter.*
import kotlinx.android.synthetic.main.progress_layout.*
import retrofit2.Call
import retrofit2.Response

class CountryFilterDialog : DialogFragment() {


    private var selectedCityName: String = ""
    private lateinit var prefManager: PrefManager
    private var countriesResponse: CitiesResponse? = null
    private var citiesResponse: CitiesResponse? = null

    private var countryI: Int = -1
    private var cityI: Int = -1
    private var countries: ArrayList<City>? = null
    var filters: HashMap<String, String> = HashMap()
    var onActionListener: OnActionListener? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cancel.setOnClickListener {
            filters.remove(CITY)
            filters.remove(STATE)
            selectedCityName = ""
            dismiss()
        }
        prefManager = PrefManager.getInstance(context)
        countries = ArrayList()
        var selectedData = ArrayList<String>()
        selectedData.add(0, getString(R.string.city_r))
        var adapter = StaticMembers.getSpinnerAdapter(context, selectedData)
        citySpinner.adapter = adapter
        selectedData = ArrayList()
        adapter = StaticMembers.getSpinnerAdapter(context, selectedData)
        countriesResponse = prefManager.getObject(StaticMembers.COUNTRIES, CitiesResponse::class.java) as CitiesResponse?
        citiesResponse = prefManager.getObject(StaticMembers.CITIES, CitiesResponse::class.java) as CitiesResponse?
        countrySpinner.adapter = adapter
        countrySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (position > 0) {
                    citySpinner.visibility = View.VISIBLE
                    countryI = position - 1
                    cityI = -1
                    val selectedData = ArrayList<String>()
                    val adapter = StaticMembers.getSpinnerAdapter(context, selectedData)
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
            }
        }
        getCountries(selectedData, adapter)

        ok.setOnClickListener {
            if (countriesResponse != null && countryI > -1 && cityI > -1) {
                filters[CITY] = "${countriesResponse!!.data[countryI].id}"
                if (citiesResponse != null)
                    filters[STATE] = "${citiesResponse!!.data[cityI].id}"
            }
            selectedCityName = if (cityI > -1) citiesResponse!!.data[cityI].name else ""
            dismiss()
        }
        delete.setOnClickListener {
            countrySpinner.setSelection(0)
            citySpinner.setSelection(0)
            countryI = -1
            cityI = -1
            citySpinner.visibility = GONE
            filters.remove(CITY)
            filters.remove(STATE)
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        onActionListener!!.onConfirm(filters, selectedCityName)
        super.onDismiss(dialog)
    }

    private fun updateCountriesView(selectedData: ArrayList<String>, adapter: ArrayAdapter<String>) {
        if (context != null) {
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
            prefManager.setObject(StaticMembers.COUNTRIES, countriesResponse)

            for (i in 0 until countries!!.size) {
                val it = countries!![i]
                if ("${it.id}" == filters[CITY]) {
                    countryI = i
                    break
                }
            }
            if (countryI > -1)
                countrySpinner.setSelection(countryI + 1)
        }
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
        prefManager.setObject(StaticMembers.CITIES, citiesResponse)

        for (i in 0 until citiesResponse!!.data!!.size) {
            val it = citiesResponse!!.data!![i]
            if ("${it.id}" == filters[STATE]) {
                cityI = i
                break
            }
        }
        if (cityI > -1)
            citySpinner.setSelection(cityI + 1)
    }

    private fun getCountries(selectedData: ArrayList<String>, adapter: ArrayAdapter<String>) {
        progress.visibility = View.VISIBLE
        selectedData.clear()
        countryI = -1
        selectedData.add(0, getString(R.string.country_r))
        adapter.notifyDataSetChanged()
        if (countriesResponse != null && countriesResponse!!.data != null) {
            updateCountriesView(selectedData, adapter)
        } else {
            val call = RetrofitModel.getApi(context).cities()
            call.enqueue(object : CallbackRetrofit<CitiesResponse>(context) {
                override fun onResponse(call: Call<CitiesResponse>, response: Response<CitiesResponse>) {
                    if (context != null) {
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
                                        StaticMembers.toastMessageShort(context, errorCitiesResponse.message)
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                StaticMembers.toastMessageShort(context, R.string.connection_error)
                            }

                        }
                    }
                }

                override fun onFailure(call: Call<CitiesResponse>, t: Throwable) {
                    super.onFailure(call, t)
                    if (context != null)
                        progress.visibility = GONE

                }
            })
        }
    }

    private fun getCities(selectedData: ArrayList<String>, adapter: ArrayAdapter<String>, countryId: String) {
        if (citiesResponse != null && citiesResponse!!.data != null) {
            updateCitiesView(selectedData, adapter)
        }
        val call = RetrofitModel.getApi(context).states(countryId)
        call.enqueue(object : CallbackRetrofit<CitiesResponse>(context) {
            override fun onResponse(call: Call<CitiesResponse>, response: Response<CitiesResponse>) {
                if (context != null) {
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
                                    StaticMembers.toastMessageShort(context, errorCitiesResponse.message)
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            StaticMembers.toastMessageShort(context, R.string.connection_error)
                        }

                    }
                }
            }
        })
    }

    companion object {
        fun getInstance(filters: HashMap<String, String>, onActionListener: OnActionListener): CountryFilterDialog {
            val dialog = CountryFilterDialog()
            dialog.filters = filters
            dialog.onActionListener = onActionListener
            return dialog
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.DialogTrans)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fg_country_filter, container, false)
    }

    interface OnActionListener {
        fun onConfirm(filters: HashMap<String, String>, stateName: String)
    }
}
