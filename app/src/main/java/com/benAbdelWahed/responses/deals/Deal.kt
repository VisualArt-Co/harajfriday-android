package com.benAbdelWahed.responses.deals

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Deal(
        val accessAlert: String?,
        val city_id: String,
        val created_at: String,
        val description: String,
        val end_date_time: String,
        val id: Int,
        val images: ArrayList<String>?,
        val is_to: ArrayList<String>?,
        val max_allowed_to_premium: String,
        val min_allowed_to_all: String,
        val payment_method_allowed: ArrayList<String>?,
        val price: String,
        val quantity: String,
        val run_status: String,
        val should_purchased_by_wallet: String,
        val start_date_time: String,
        val state_id: Int,
        val can_access: Boolean,
        val status: String,
        val title: String,
        val updated_at: String,
        val countOrders: Int,
        val items_in_stock: Int,
        val MaxQuantityForThisUser: Int,
        val MinQuantityForThisUser: Int,
        @field:SerializedName("payment_info")
        val paymentInfo: PaymentInfo
) : Parcelable