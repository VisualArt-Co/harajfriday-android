package com.benAbdelWahed.responses.settings_response.paymentOptions

import android.os.Parcel
import android.os.Parcelable

data class PaymentOption(
    val id: String,
    val name: String
):Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString()?:"",
            parcel.readString()?:"") {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PaymentOption> {
        override fun createFromParcel(parcel: Parcel): PaymentOption {
            return PaymentOption(parcel)
        }

        override fun newArray(size: Int): Array<PaymentOption?> {
            return arrayOfNulls(size)
        }
    }
}