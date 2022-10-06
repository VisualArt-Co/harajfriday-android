package com.benAbdelWahed.responses.social


import com.google.gson.annotations.SerializedName

data class SocialResponse(
    val `data`: Data,
    val message: String,
    val success: Boolean
)