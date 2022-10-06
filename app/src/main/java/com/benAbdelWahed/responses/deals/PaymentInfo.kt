package com.benAbdelWahed.responses.deals

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaymentInfo(

	@field:SerializedName("descount")
	val descount: Int,

	@field:SerializedName("total")
	val total: Int,

	@field:SerializedName("ship_price")
	val shipPrice: Int
) : Parcelable
