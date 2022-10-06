package com.benAbdelWahed.responses.firebase_responses


import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("message_id")
    val messageId: String
)