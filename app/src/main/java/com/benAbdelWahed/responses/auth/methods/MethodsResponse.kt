package com.benAbdelWahed.responses.auth.methods

data class MethodsResponse(
    val `data`: List<List<String>>,
    val message: String,
    val success: Boolean
)