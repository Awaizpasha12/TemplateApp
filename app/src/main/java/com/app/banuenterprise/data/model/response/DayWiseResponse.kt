package com.app.banuenterprise.data.model.response

data class DayWiseResponse(
    
    val isSuccess: Boolean,
    val message: String,
    val dayName : String?,
    val customers: List<CustomerTotal>?
)

data class CustomerTotal(
    val customerName: String,
    val totalPendingAmount: Double,
    val route : String?,
    val customerId : String?
)
