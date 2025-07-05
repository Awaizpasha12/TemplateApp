package com.app.banuenterprise.data.model.request

data class InvoiceGivenForOnlinePaymentRequest(
    val token: String,
    val billItems: List<InvoiceItem>,
)

data class InvoiceItem(
    val billItemId: String,
    val igopDate : String,
)