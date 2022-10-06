package com.benAbdelWahed.responses.auth.wallet

data class PaymentHistory(
        val `data`: List<PaymentHistoryItem>,
        val meta: Meta
)