package com.app.banuenterprise.data.model.response

data class StockItem(
    val _id: String?,
    val itemName: String?,
    val brand: String?,
    val units: String?,
    val gstApplicable: String?,
    val gstRate: Int?,
    val mrp: Int?,
    val itemCode: String?,
    val __v: Int?
)
