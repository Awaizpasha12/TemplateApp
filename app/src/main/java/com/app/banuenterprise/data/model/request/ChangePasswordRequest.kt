package com.app.banuenterprise.data.model.request

data class ChangePasswordRequest(
    val apiKey : String,
    val newPassword: String
)
