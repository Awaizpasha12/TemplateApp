package com.app.banuenterprise.data.model.response

data class LedgerResponse(
    val success: Boolean,
    val data: List<LedgerItems>
)

data class LedgerItems(
    val customerName: String,
    val customerId: String,
    val totalOutstanding: Double,
    val brandWise: List<BrandWise>
)

data class BrandWise(
    val brandName: String,
    val outstanding: Double
)
