package com.benAbdelWahed.responses.settings_response.paymentOptions

data class UpgradeToPremium(
    val allowed: List<String>,
    val id: String,
    val name: String
)