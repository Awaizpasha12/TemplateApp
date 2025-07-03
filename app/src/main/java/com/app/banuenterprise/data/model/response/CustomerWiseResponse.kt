package com.app.banuenterprise.data.model.response
data class CustomerWiseResponse(
    val isSuccess: Boolean,
    val message: String,
    val invoices: List<BillItem>?
)
data class BillItem(
    val billNumber: String,
    val billDate: String,
    val route: String,
    val customerName: String,
    val pendingAmount: Int,
    val netValue : Int,
    val brand: String,
    val creditDays: Int? = 0,
    val _id : String,
)
