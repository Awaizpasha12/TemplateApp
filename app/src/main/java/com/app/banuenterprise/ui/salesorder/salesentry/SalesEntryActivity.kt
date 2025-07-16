package com.app.banuenterprise.ui.salesorder.salesentry


import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.banuenterprise.data.model.request.ItemEntry
import com.app.banuenterprise.data.model.request.SalesEntryRequest
import com.app.banuenterprise.databinding.ActivitySalesEntryBinding
import com.app.banuenterprise.ui.salesorder.salesentry.adapter.SalesEntryGroupAdapter
import com.app.banuenterprise.ui.salesorder.salesentry.model.SalesEntryGroupUiModel
import com.app.banuenterprise.data.model.response.Ledger
import com.app.banuenterprise.data.model.response.SalesEntryData
import com.app.banuenterprise.data.model.response.StockItem
import com.app.banuenterprise.utils.SessionUtils
import com.app.banuenterprise.utils.SupportMethods
import com.app.banuenterprise.utils.simpleadapters.SimpleStringListAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class SalesEntryActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySalesEntryBinding
    private val viewModel: SalesEntryViewModel by viewModels()

    // Adapter and group list
    private lateinit var adapter: SalesEntryGroupAdapter
    private val groupList = mutableListOf<SalesEntryGroupUiModel>()

    // For Ledger selection
    private var ledgerList: List<Ledger> = emptyList()
    private var selectedLedger: Ledger? = null

    // For item selection (future step)
    private var stockItems: List<StockItem> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySalesEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupListeners()

        // Fetch ledgers/stockItems from ViewModel/cache
        val apiKey = SessionUtils.getApiKey(applicationContext)
        viewModel.fetchSalesSettings(apiKey, applicationContext, this, forceRefresh = false)

        // Observe for data updates
        viewModel.salesSettings.observe(this) { salesSettingsResponse ->
            salesSettingsResponse?.data?.let { data ->
                ledgerList = data.ledgers ?: emptyList()
                stockItems = data.stockItems ?: emptyList()
            }
        }
        // 1. Submit Button Click
        binding.btnSubmit.setOnClickListener {
            // Validation
            if (selectedLedger == null) {
                Toast.makeText(this, "Please select a ledger.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (groupList.isEmpty()) {
                Toast.makeText(this, "Please add at least one item.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            groupList.forEachIndexed { idx, group ->
                if (group.itemId.isNullOrBlank()) {
                    Toast.makeText(this, "Select item in row ${idx + 1}", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (group.quantity <= 0) {
                    Toast.makeText(this, "Quantity should be > 0 in row ${idx + 1}", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (group.rate <= 0) {
                    Toast.makeText(this, "Rate should be > 0 in row ${idx + 1}", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            // Build items list for request
            val itemsList = groupList.map { group ->
                ItemEntry(
                    stockItemId = group.itemId ?: "",
                    quantity = group.quantity,
                    rate = group.rate
                )
            }
            val token = SessionUtils.getApiKey(applicationContext)
            val ledgerId = selectedLedger?._id ?: ""
//            val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
//                .format(Date())
            val date = SupportMethods.getCurrentDateFormatted()
            val request = SalesEntryRequest(
                token = token,
                ledgerId = ledgerId,
                itemsList = itemsList,
                date = date
            )

            // Call ViewModel function with loading
            viewModel.submitSalesEntry(request, this)
        }

// 2. Observe API Result
        viewModel.salesEntryResult.observe(this) { response ->
            if (response == null) return@observe

            if (response.isSuccess && response.data != null) {
                // Build WhatsApp message text
                val data = response.data
                val invoiceText = buildSalesEntryShareText(data)

                AlertDialog.Builder(this)
                    .setTitle("Submitted")
                    .setMessage("Do you want to share this sales entry?")
                    .setPositiveButton("Yes") { dialog, _ ->
                        // Share via WhatsApp
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, invoiceText)
                            type = "text/plain"
                            setPackage("com.whatsapp")
                        }
                        try {
                            startActivity(sendIntent)
                        } catch (e: Exception) {
                            Toast.makeText(this, "WhatsApp not found!", Toast.LENGTH_SHORT).show()
                        }
                        dialog.dismiss()
                        // Optionally finish after share
                        finish()
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                        finish()
                    }
                    .setCancelable(false)
                    .show()

            } else {
                Toast.makeText(this, response.message ?: "Failed to save sales entry", Toast.LENGTH_LONG).show()

            }
        }



    }
    private fun buildSalesEntryShareText(data: SalesEntryData): String {
        val sb = StringBuilder()
        sb.append("Invoice: ${data.invoiceNumber}\n")
        sb.append("Order No: ${data.orderNumber ?: ""}\n")
        sb.append("Party: ${data.ledgerId?.ledgerName ?: ""}\n")
        sb.append("Date: ${data.createdAt?.substring(0,10) ?: ""}\n")
        sb.append("\nItems:\n")
        data.itemsList?.forEach { item ->
            sb.append("- ${item.stockItemId?.itemName ?: ""} (${item.quantity} x ₹${item.rate}) = ₹${item.amount}\n")
            sb.append("  GST: ${item.gstRate}%  Total: ₹${item.total}\n")
        }
        sb.append("\nTotal Amount: ₹${data.totalAmount}\n")
        return sb.toString()
    }

    private fun setupRecyclerView() {
        adapter = SalesEntryGroupAdapter(
            groupList,
            onItemSelectClick = { position ->
                showSearchableItemDialog(position) { selectedStockItem ->
                    groupList[position].apply {
                        itemId = selectedStockItem._id
                        itemName = selectedStockItem.itemName
                        brand = selectedStockItem.brand
                        displayText = "${selectedStockItem.itemName} - ${selectedStockItem.brand}"
                        units = selectedStockItem.units
                        gstApplicable = selectedStockItem.gstApplicable
                        gstRate = selectedStockItem.gstRate?.toDouble() ?: 0.0
                        cgstPercent = gstRate / 2
                        sgstPercent = gstRate / 2
                        rate = selectedStockItem.mrp?.toDouble() ?: 0.0
                    }
                    adapter.recalculateGroup(groupList[position]) // call your calculation
                    adapter.notifyItemChanged(position)
                    updateOverallTotal() // in your activity
                }
            },
            onRemoveClick = { position ->
                groupList.removeAt(position)
                adapter.notifyItemRemoved(position)
                updateOverallTotal()
            },
            onValueChanged = {
                adapter.notifyDataSetChanged()
                updateOverallTotal()
            }
        )
        binding.rvGroups.adapter = adapter
        binding.rvGroups.layoutManager = LinearLayoutManager(this)

        // Add initial group if list is empty
        if (groupList.isEmpty()) {
            groupList.add(SalesEntryGroupUiModel())
            adapter.notifyDataSetChanged()
        }
    }

    private fun setupListeners() {
        // Refresh button
        binding.btnRefresh.setOnClickListener {
            val apiKey = SessionUtils.getApiKey(applicationContext)
            viewModel.fetchSalesSettings(apiKey, applicationContext, this, forceRefresh = true)
        }



// On ledger select
        binding.tvSelectLedger.setOnClickListener {
            // Prepare map: ledgerId -> ledgerName
            val ledgerIdLedgerName = ledgerList.associate { it._id to it.ledgerName }
            val ledgerNameLedgerId = ledgerList.associate { it.ledgerName to it._id }
            val ledgerNamesList = ledgerNameLedgerId.keys.toList()
            showSearchableDialog(
                title = "Select Ledger",
                data = ledgerNamesList
            ) { selectedLedgerName ->
                val selectedLedgerId = ledgerNameLedgerId[selectedLedgerName]

                if (selectedLedgerId != null) {
                    selectedLedger = ledgerList.firstOrNull { it._id == selectedLedgerId }
                    binding.tvSelectLedger.text = selectedLedgerName
                    // Optionally, do more with the selected ledger (load ledger details, etc.)
                }
            }
        }

        // Add Group button
        binding.btnAddGroup.setOnClickListener {
            groupList.add(SalesEntryGroupUiModel())
            adapter.notifyItemInserted(groupList.size - 1)
            binding.rvGroups.scrollToPosition(groupList.size - 1)
            updateOverallTotal()
        }

        // Submit button
        binding.btnSubmit.setOnClickListener {
            // Validate ledger selected
            if (selectedLedger == null) {
                Toast.makeText(this, "Please select a ledger", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Optionally validate group entries
            // TODO: Submission logic here
            Toast.makeText(this, "Ready to submit!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateOverallTotal() {
        val total = groupList.sumOf { it.groupTotal }
        binding.tvOverallTotal.text = "Total: ₹${String.format("%.2f", total)}"
    }

    private fun showSearchableDialog(
        title: String,
        data: List<String>,
        onItemSelected: (String) -> Unit
    ) {
        val ctx = this
        val dialogView = LayoutInflater.from(ctx).inflate(com.app.banuenterprise.R.layout.layout_dialog_search_list, null)
        val etSearch = dialogView.findViewById<EditText>(com.app.banuenterprise.R.id.etSearch)
        val rvList = dialogView.findViewById<RecyclerView>(com.app.banuenterprise.R.id.rvList)

        lateinit var alertDialog: androidx.appcompat.app.AlertDialog

        val adapter = SimpleStringListAdapter(data) { selected ->
            onItemSelected(selected)
            alertDialog.dismiss()
        }
        rvList.layoutManager = LinearLayoutManager(ctx)
        rvList.adapter = adapter

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val filtered = data.filter { it.contains(s.toString(), ignoreCase = true) }
                adapter.updateData(filtered)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        alertDialog = MaterialAlertDialogBuilder(ctx)
            .setTitle(title)
            .setView(dialogView)
            .setNegativeButton("Cancel", null)
            .show()
    }
    private fun showSearchableItemDialog(
        position: Int,
        onSelected: (stockItem: StockItem) -> Unit
    ) {
        // Create a map for display string to StockItem
        val itemMap = stockItems.associateBy { "${it.itemName} - ${it.brand}" }
        val itemNamesList = itemMap.keys.toList()

        showSearchableDialog(
            title = "Select Item",
            data = itemNamesList
        ) { selectedItemName ->
            val selectedItem = itemMap[selectedItemName]
            if (selectedItem != null) {
                // Prevent duplicate item-brand
                val duplicate = groupList.anyIndexed { idx, group ->
                    idx != position && group.itemId == selectedItem._id && group.brand == selectedItem.brand
                }
                if (duplicate) {
                    Toast.makeText(this, "This item-brand is already added!", Toast.LENGTH_SHORT).show()
                } else {
                    onSelected(selectedItem)
                }
            }
        }
    }

    // Helper for indexed any (if using Kotlin <1.7)
    inline fun <T> List<T>.anyIndexed(predicate: (Int, T) -> Boolean): Boolean {
        for (i in indices) if (predicate(i, get(i))) return true
        return false
    }

}

