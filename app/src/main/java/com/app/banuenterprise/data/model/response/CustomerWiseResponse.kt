package com.app.banuenterprise.data.model.response
data class CustomerWiseResponse(
    
    val isSuccess: Boolean,
    val message: String,
    val invoices: List<CustomerData>?
)
data class CustomerData(
    val billNumber: String,
    val billDate: String,
//    val salesman: String,
    val route: String,
    val customerName: String,
    val pendingAmount: Int,
//    val day: String,
    val netValue : Int,
    val brand: String,
    val creditDays: Int? = 0
)
