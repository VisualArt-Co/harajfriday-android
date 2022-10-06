package com.benAbdelWahed.models

import android.os.Parcel
import android.os.Parcelable

data class Method(val key: String?, var image: Int = 0, var textRes: Int = 0) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readInt(),
            parcel.readInt()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(key)
        parcel.writeInt(image)
        parcel.writeInt(textRes)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Method> {
        override fun createFromParcel(parcel: Parcel): Method {
            return Method(parcel)
        }

        override fun newArray(size: Int): Array<Method?> {
            return arrayOfNulls(size)
        }
    }
}