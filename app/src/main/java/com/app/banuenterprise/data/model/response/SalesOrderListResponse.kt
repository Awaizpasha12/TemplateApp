package com.app.banuenterprise.data.model.response

data class SalesOrderListResponse(
    val isSuccess: Boolean,
    val data: List<SalesOrder>,
    val message: String?
)

data class SalesOrder(
    val _id: String,
    val invoiceNumber: String,
    val ledgerId: Ledger,
    val companyId: String,
    val orderNumber: String,
    val itemsList: List<SalesOrderItem>,
    val totalAmount: Double,
    val createdAt: String,
    val updatedAt: String,
    val __v: Int
)



data class SalesOrderItem(
    val stockItemId: StockItem,
    val quantity: Double,
    val gstRate: Double,
    val rate: Double,
    val amount: Double,
    val tax: Double,
    val total: Double,
    val _id: String
)


