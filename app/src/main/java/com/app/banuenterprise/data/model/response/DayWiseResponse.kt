package com.app.banuenterprise.data.model.response

data class DayWiseResponse(
    val status: Int,
    val isSuccess: Boolean,
    val message: String,
    val data: List<CustomerTotal>?
)

data class CustomerTotal(
    val customer: String,
    val total: Int,
    val route : String?,
)
