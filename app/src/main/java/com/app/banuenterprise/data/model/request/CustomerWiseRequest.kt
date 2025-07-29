package com.app.banuenterprise.data.model.request

data class CustomerWiseRequest (
    val token: String,
    val customerId : String,
    val day:Int,
    val sendAll : Boolean= false
)