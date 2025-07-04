package com.app.banuenterprise.ui.outstanding.receiptEntry

data class ReceiptEntryGroupUiModel(
    var billItemId : String="",
    var customerName: String = "",
    var invoiceNumber: String = "",
    var brand: String = "",
    var amount: Double = 0.0,
    var defaultAmount: Double = 0.0,
    var invoiceDisplayText: String = ""
)
