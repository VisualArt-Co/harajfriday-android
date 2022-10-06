package com.benAbdelWahed.responses.settings_response.paymentOptions

data class PaymentOptionsResponse(
    val payment_locations: PaymentLocations,
    val payment_options: List<PaymentOption>
)