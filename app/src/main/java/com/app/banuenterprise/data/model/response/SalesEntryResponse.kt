package com.app.banuenterprise.data.model.response

data class SalesEntryResponse(
    val isSuccess: Boolean,
    val data: SalesEntryData?, // May be null on error
    val message: String?
)

data class SalesEntryData(
    val _id: String?,
    val invoiceNumber: String?,
    val ledgerId: LedgerInResponse?,
    val companyId: String?,
    val orderNumber: String?,
    val itemsList: List<SalesItemInResponse>?,
    val totalAmount: Double?,
    val createdAt: String?,
    val updatedAt: String?,
    val __v: Int?
)

data class LedgerInResponse(
    val _id: String?,
    val ledgerName: String?
)

data class SalesItemInResponse(
    val stockItemId: StockItemInResponse?,
    val quantity: Int?,
    val gstRate: Double?,
    val rate: Double?,
    val amount: Double?,
    val tax: Double?,
    val total: Double?,
    val _id: String?
)

data class StockItemInResponse(
    val _id: String?,
    val itemName: String?,
    val gstRate: Double?
)
