package com.app.banuenterprise.data.model.response
data class CustomerWiseResponse(
    val status: Int,
    val isSuccess: Boolean,
    val message: String,
    val data: List<CustomerData>?
)
data class CustomerData(
    val billNumber: String,
    val billDate: String,
    val salesman: String,
    val route: String,
    val customer: String,
    val netValue: Int,
    val day: String,
    val brand: String,
    val creditDays: Int
)
