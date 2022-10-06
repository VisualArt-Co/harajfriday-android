package com.benAbdelWahed.responses.payment

data class BuyResponse(
        val `data`: Data,
        val message: String,
        val success: Boolean
)