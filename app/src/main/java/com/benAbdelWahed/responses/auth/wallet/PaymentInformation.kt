package com.benAbdelWahed.responses.auth.wallet

data class PaymentInformation(
    val pending_balance: Int,
    val total_blance: Int,
    val usable_balance: Int
)