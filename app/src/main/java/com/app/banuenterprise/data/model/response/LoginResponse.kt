package com.app.banuenterprise.data.model.response

data class LoginResponse(
    val status : Int,
    val isSuccess : Boolean,
    val apikey : String,
    val message : String,
)