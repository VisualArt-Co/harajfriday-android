package com.benAbdelWahed.responses.order

import android.os.Parcelable
import com.benAbdelWahed.responses.address.Address
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
public data class OrderResponse(

	@field:SerializedName("data")
	val data: Data,

	@field:SerializedName("success")
	val success: Boolean,

	@field:SerializedName("message")
	val message: String
) : Parcelable

@Parcelize
public data class Data(

		@field:SerializedName("data")
	val data: List<Order>,

		@field:SerializedName("meta")
	val meta: Meta
) : Parcelable

@Parcelize
public data class Meta(

	@field:SerializedName("per_page")
	val perPage: Int,

	@field:SerializedName("total")
	val total: Int,

	@field:SerializedName("last_page")
	val lastPage: Int,

	@field:SerializedName("hasMorePages")
	val hasMorePages: Boolean,

	@field:SerializedName("current_page")
	val currentPage: Int
) : Parcelable

@Parcelize
public data class Order(

		@field:SerializedName("image")
	val image: String? = null,

		@field:SerializedName("amount")
	val amount: Int? = null,

		@field:SerializedName("images")
	val images: List<String?>? = null,

		@field:SerializedName("quantity")
	val quantity: Int? = null,

		@field:SerializedName("address")
	val address: Address? = null,

		@field:SerializedName("item_price")
	val itemPrice: Int? = null,

		@field:SerializedName("ship_price")
	val shipPrice: Int? = null,

		@field:SerializedName("description")
	val description: String? = null,

		@field:SerializedName("external_id")
	val externalId: String? = null,

		@field:SerializedName("title")
	val title: String? = null,

		@field:SerializedName("order_updated_at")
	val orderUpdatedAt: String? = null,

		@field:SerializedName("order_status")
	val orderStatus: String? = null,

		@field:SerializedName("id")
	val id: Int? = null,

		@field:SerializedName("state")
	val state: String? = null,

		@field:SerializedName("run_status")
	val runStatus: String? = null,

		@field:SerializedName("order_created_at")
	val orderCreatedAt: String? = null,

		@field:SerializedName("payment_method")
	val paymentMethod: String? = null,

		@field:SerializedName("hash")
	val hash: String? = null,

		@field:SerializedName("status")
	val status: Int? = null
) : Parcelable
