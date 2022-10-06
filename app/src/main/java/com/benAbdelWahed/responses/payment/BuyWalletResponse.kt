package com.benAbdelWahed.responses.payment

data class BuyWalletResponse(
        val `data`: ArrayList<String>,
        val message: String,
        val success: Boolean
)