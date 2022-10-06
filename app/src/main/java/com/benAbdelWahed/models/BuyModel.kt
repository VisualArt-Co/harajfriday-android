package com.benAbdelWahed.models

import android.os.Parcel
import android.os.Parcelable

data class BuyModel(val payment_method: String?, var quantity: Int = 0, var address: Int = 0) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readInt(),
            parcel.readInt())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(payment_method)
        parcel.writeInt(quantity)
        parcel.writeInt(address)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BuyModel> {
        override fun createFromParcel(parcel: Parcel): BuyModel {
            return BuyModel(parcel)
        }

        override fun newArray(size: Int): Array<BuyModel?> {
            return arrayOfNulls(size)
        }
    }
}