package com.app.banuenterprise.data.model.request

data class ReceiptEntryRequest(
    val billItems: List<BillItemRequest>,
    val mode: String,
    val collectedDate: String,
    val proofUrl: String,
    val remarks: String,
    val token: String
)

data class BillItemRequest(
    val billItemId: String,
    val amount: Double
)

