package com.app.banuenterprise.data.model.response

data class CollectionListResponse(
    val success: Boolean,
    val data: List<CollectionItem>
)

data class CollectionItem(
    val _id: String,
    val amount: Double,
    val mode: String,
    val proofUrl: String,
    val remarks: String,
    val status: String,
    val customerName: String,
    val billNumber: String,
    val brand: String,
    val route: String,
    val billItemId: String,
    val collectedDate: String
)
