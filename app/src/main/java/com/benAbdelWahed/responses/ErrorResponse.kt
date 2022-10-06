package com.benAbdelWahed.responses


data class ErrorResponse(
        val success: Boolean,
        val error_code: String?,
        val message: String?,
        val data: String? = null
)