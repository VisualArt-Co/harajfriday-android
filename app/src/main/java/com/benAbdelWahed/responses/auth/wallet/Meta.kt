package com.benAbdelWahed.responses.auth.wallet

data class Meta(
    val current_page: Int,
    val hasMorePages: Boolean,
    val last_page: Int,
    val per_page: Int,
    val total: Int
)