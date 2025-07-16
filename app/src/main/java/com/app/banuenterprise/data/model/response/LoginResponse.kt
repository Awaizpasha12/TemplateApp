package com.app.banuenterprise.data.model.response

data class LoginResponse(
    val isSuccess : Boolean? = false,
    val token : String?,
    val message : String?,
    val user : User?
)
data class User(
    val roles : ArrayList<String>
)