package com.benAbdelWahed.responses.firebase_responses


import com.google.gson.annotations.SerializedName

data class FirebaseResponse(
    @SerializedName("canonical_ids")
    val canonicalIds: Int,
    val failure: Int,
    @SerializedName("multicast_id")
    val multicastId: Long,
    val results: List<Result>,
    val success: Int
)