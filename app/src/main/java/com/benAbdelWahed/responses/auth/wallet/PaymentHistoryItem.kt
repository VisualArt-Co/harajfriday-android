package com.benAbdelWahed.responses.auth.wallet

data class PaymentHistoryItem(
    val amount: Int,
    val description: String,
    val id: Int,
    val is_pending: Int,
    val last_pending_date: String,
    val created_at: String,
    val updated_at: String,
    val status: Int
)