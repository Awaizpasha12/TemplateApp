package com.app.banuenterprise.data.model.request

data class SalesEntryRequest(
    val token: String,
    val ledgerId: String,
    val itemsList: List<ItemEntry>,
    val billDate: String
)
data class ItemEntry(
    val stockItemId: String,
    val quantity: Int,
    val rate: Double
)
