package com.app.banuenterprise.data.model.response

data class InvoicesByDayResponse(
    
    val isSuccess: Boolean,
    val message: String,
    val data: Map<String, List<InvoiceDetail>>
)

data class InvoiceDetail(
    val billNumber: String,
    val brand: String,
    val amount: Double
)

