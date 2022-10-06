package com.benAbdelWahed.responses.address

import android.os.Parcel
import android.os.Parcelable

data class Address(
        val addressable_id: Int?,
        val addressable_type: String?,
        var city: String?,
        val country_code: String?,
        val created_at: String?,
        val family_name: String?="_",
        var given_name: String?,
        val id: Int?,
        val is_billing: Boolean?,
        val is_primary: Boolean?,
        val is_shipping: Boolean?,
        var label: String?,
        val organization: String?,
        var postal_code: String?,
        var state: String?,
        var street: String?,
        var phone: String?,
        val updated_at: String?
) : Parcelable {
    constructor() : this(null,null,null,null,null,"_",null,null,null,null,null,null,null,null,null,null,null,null)
    constructor(name: String?, city: String?, state: String?, street: String?, phone: String?, label: String? = null, id: Int? = null) : this(
            null,
            null,
            city,
            null,
            null,
            "_",
            name,
            id,
            null,
            null,
            null,
            label,
            null,
            null,
            state,
            street,
            phone,
            null
    )

    constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
            parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
            parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(addressable_id)
        parcel.writeString(addressable_type)
        parcel.writeString(city)
        parcel.writeString(country_code)
        parcel.writeString(created_at)
        parcel.writeString(family_name)
        parcel.writeString(given_name)
        parcel.writeValue(id)
        parcel.writeValue(is_billing)
        parcel.writeValue(is_primary)
        parcel.writeValue(is_shipping)
        parcel.writeString(label)
        parcel.writeString(organization)
        parcel.writeString(postal_code)
        parcel.writeString(state)
        parcel.writeString(street)
        parcel.writeString(phone)
        parcel.writeString(updated_at)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Address> {
        override fun createFromParcel(parcel: Parcel): Address {
            return Address(parcel)
        }

        override fun newArray(size: Int): Array<Address?> {
            return arrayOfNulls(size)
        }
    }
}