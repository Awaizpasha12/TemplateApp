package com.app.banuenterprise.data.model.response

data class LedgerResponse (
    val success: Boolean,
    val data: List<LedgerItems>
)
data class LedgerItems(
    val _id: String,
    val amount: Double,
    val customerName: String,
)