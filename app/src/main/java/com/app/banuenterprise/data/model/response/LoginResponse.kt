package com.app.banuenterprise.data.model.response

data class LoginResponse(
    val statusCode : Int,
    val isSuccess : Boolean,
    val apiKey : String,
    val message : String,
)