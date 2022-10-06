package com.benAbdelWahed.responses.auth.wallet

data class Data(
    val payment_history: PaymentHistory,
    val payment_information: PaymentInformation
)