package com.benAbdelWahed.ui.address

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.benAbdelWahed.R
import com.benAbdelWahed.responses.address.Address
import com.benAbdelWahed.utils.StaticMembers
import kotlinx.android.synthetic.main.dialog_add_address.*

class AddAddressDialog : DialogFragment() {
    lateinit var onAddressAddedOrEdited: (address: Address) -> Unit
    var address: Address? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.DialogTrans)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_add_address, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBundle()
        okButton.setOnClickListener {
            if (validateEditTexts()) {
                sendAddress()
                dismiss()
            }
        }
    }

    private fun initBundle() {
        address = arguments?.getParcelable(StaticMembers.ADDRESS)
        address?.apply {
            if (id != null && id != 0)
                title.setText(R.string.edit_address)
            nameEditText.setText(given_name)
            cityEditText.setText(city)
            stateEditText.setText(state)
            streetEditText.setText(street)
            phoneEditText.setText(phone)
            addressEditText.setText(label)
            postalCodeEditText.setText(postal_code)
        }
    }

    private fun sendAddress() {
        if (address == null)
            address = Address()
        address?.apply {
            given_name = nameEditText.text.toString()
            city = cityEditText.text.toString()
            state = stateEditText.text.toString()
            street = streetEditText.text.toString()
            phone = phoneEditText.text.toString()
            label = addressEditText.text.toString()
            postal_code = postalCodeEditText.text.toString()
        }
        onAddressAddedOrEdited(address!!)
    }

    private fun validateEditTexts(): Boolean {
        var error = false
        nameLayout.error = null
        cityLayout.error = null
        stateLayout.error = null
        streetLayout.error = null
        phoneLayout.error = null
        postalCodeLayout.error = null

        if (nameEditText.text.isNullOrEmpty()) {
            nameLayout.error = getString(R.string.field_req)
            error = true
        }
        if (cityEditText.text.isNullOrEmpty()) {
            cityLayout.error = getString(R.string.field_req)
            error = true
        }
        if (stateEditText.text.isNullOrEmpty()) {
            stateLayout.error = getString(R.string.field_req)
            error = true
        }
        if (streetEditText.text.isNullOrEmpty()) {
            streetLayout.error = getString(R.string.field_req)
            error = true
        }
        if (phoneEditText.text.isNullOrEmpty()) {
            phoneLayout.error = getString(R.string.field_req)
            error = true
        }
        if (postalCodeEditText.text.isNullOrEmpty()) {
            postalCodeLayout.error = getString(R.string.field_req)
            error = true
        }
        return !error
    }

    companion object {
        private var INSTANCE: AddAddressDialog? = null
        fun getInstance(address: Address?, onAddressAddedOrEdited: (address: Address) -> Unit): AddAddressDialog {
            INSTANCE?.dismiss()
            INSTANCE = AddAddressDialog()
            INSTANCE?.arguments = Bundle().apply {
                putParcelable(StaticMembers.ADDRESS, address)
            }
            INSTANCE?.onAddressAddedOrEdited = onAddressAddedOrEdited
            return INSTANCE!!
        }
    }

}