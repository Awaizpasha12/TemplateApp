package com.app.banuenterprise.data.model.response

data class SalesSettingsResponse(
    val isSuccess: Boolean?,
    val message: String?,
    val data: DataResponse?
)

data class DataResponse(
    val ledgers: List<Ledger>?,
    val stockItems: List<StockItem>?
)


