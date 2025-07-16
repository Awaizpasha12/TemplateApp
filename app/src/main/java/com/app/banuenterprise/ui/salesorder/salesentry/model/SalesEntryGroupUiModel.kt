package com.app.banuenterprise.ui.salesorder.salesentry.model

data class SalesEntryGroupUiModel(
    var itemId: String? = null,
    var itemName: String? = null,
    var brand: String? = null,
    var displayText: String? = null,
    var units: String? = null,
    var gstApplicable: String? = null,
    var gstRate: Double = 0.0,
    var quantity: Int = 1,
    var rate: Double = 0.0,
    var amount: Double = 0.0,
    var cgstPercent: Double = 0.0,
    var sgstPercent: Double = 0.0,
    var cgstValue: Double = 0.0,
    var sgstValue: Double = 0.0,
    var groupTotal: Double = 0.0
)
