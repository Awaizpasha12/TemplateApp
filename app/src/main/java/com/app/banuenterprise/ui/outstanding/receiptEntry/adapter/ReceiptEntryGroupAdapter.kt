package com.app.banuenterprise.ui.outstanding.receiptEntry.adapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.banuenterprise.R
import com.app.banuenterprise.data.model.response.BillItem
import com.app.banuenterprise.databinding.ItemReceiptEntryGroupBinding
import com.app.banuenterprise.ui.outstanding.receiptEntry.ReceiptEntryGroupUiModel
import com.app.banuenterprise.utils.simpleadapters.SimpleStringListAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
class ReceiptEntryGroupAdapter(
    private var availableInvoices: List<BillItem>,
    private val onListChanged: () -> Unit
) : RecyclerView.Adapter<ReceiptEntryGroupAdapter.GroupViewHolder>() {

    val items = mutableListOf<ReceiptEntryGroupUiModel>()
    private val alreadyPicked = mutableSetOf<String>()  // Keep track of selected combinations globally
    private var amountWatcher: TextWatcher? = null

    // Set invoice list when customer changes, and reset items
    fun setAvailableInvoices(newList: List<BillItem>) {
        availableInvoices = newList
        clearAll()
        notifyDataSetChanged()
    }

    fun addGroup() {
        items.add(ReceiptEntryGroupUiModel())
        notifyItemInserted(items.size - 1)
        onListChanged()
    }

    fun removeGroup(position: Int) {
        if (position >= 0 && position < items.size) {
            items.removeAt(position)
            notifyItemRemoved(position)
            onListChanged()
        }
    }

    fun clearAll() {
        items.clear()
        notifyDataSetChanged()
        onListChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemReceiptEntryGroupBinding.inflate(inflater, parent, false)
        return GroupViewHolder(binding)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class GroupViewHolder(private val binding: ItemReceiptEntryGroupBinding) : RecyclerView.ViewHolder(binding.root) {

        private var amountWatcher: TextWatcher? = null

        fun bind(entry: ReceiptEntryGroupUiModel) {
            // Set fields or placeholders
            binding.tvInvoiceNumber.text = if (entry.invoiceDisplayText.isBlank()) "Select Invoice" else entry.invoiceDisplayText
            binding.tvBrand.text = if (entry.invoiceNumber.isBlank()) "--" else entry.brand

            // === Safe EditText update ===
            amountWatcher?.let { binding.etAmount.removeTextChangedListener(it) }

            // Set text based on model
            binding.etAmount.setText(
                if (entry.invoiceNumber.isBlank()) ""
                else if (entry.amount == 0.0) "" else entry.amount.toString()
            )
            binding.etAmount.hint = if (entry.invoiceNumber.isBlank()) "Select invoice first" else ""
            binding.etAmount.isEnabled = entry.invoiceNumber.isNotBlank()

            // Create new watcher and add it
            amountWatcher = object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (!binding.etAmount.isFocused) return
                    val newVal = s?.toString()?.toDoubleOrNull()
                    if (entry.invoiceNumber.isNotBlank()) {
                        if (newVal != null && entry.amount != newVal) {
                            entry.amount = newVal
                            onListChanged()
                        } else if (newVal == null && entry.amount != 0.0) {
                            entry.amount = 0.0
                            onListChanged()
                        }
                    }
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            }
            binding.etAmount.addTextChangedListener(amountWatcher)

            val ctx = binding.root.context

            // Filter invoices based on previously selected combinations
            val invoiceChoices = availableInvoices.filter { inv ->
                val invoiceDisplayText = "${inv.billNumber} - ${inv.brand}"
                !alreadyPicked.contains(invoiceDisplayText) || invoiceDisplayText == entry.invoiceDisplayText
            }

            val invoiceChoicesDisplay = invoiceChoices.map { "${it.billNumber} - ${it.brand}" }

            binding.tvInvoiceNumber.setOnClickListener {
                if (invoiceChoicesDisplay.isEmpty()) {
                    Toast.makeText(ctx, "No invoices left to select.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                showSearchableDialog(ctx, "Select Invoice", invoiceChoicesDisplay) { selectedText ->
                    val (selectedInvoiceNumber, selectedBrand) = selectedText.split(" - ")

                    // Find the corresponding BillItem by matching both invoice number and brand
                    val detail = availableInvoices.find { it.billNumber == selectedInvoiceNumber && it.brand == selectedBrand }

                    // Update the entry with the selected values
                    entry.invoiceNumber = selectedInvoiceNumber
                    entry.brand = selectedBrand
                    entry.amount = (detail?.pendingAmount ?: 0.0).toDouble()
                    entry.defaultAmount = (detail?.pendingAmount ?: 0.0).toDouble()
                    entry.billItemId = detail?._id ?: ""
                    entry.invoiceDisplayText = "$selectedInvoiceNumber - $selectedBrand"  // Store for future comparison

                    // Add the selected combination to the set of picked invoices
                    alreadyPicked.add(entry.invoiceDisplayText)

                    // Update the UI
                    binding.tvInvoiceNumber.text = entry.invoiceDisplayText

                    // Notify the adapter to update the view
                    notifyItemChanged(adapterPosition)
                    onListChanged()
                }
            }

            binding.btnRemoveGroup.setOnClickListener {
                removeGroup(adapterPosition)
            }
        }
    }

    // Helper dialog function
    private fun showSearchableDialog(
        context: Context,
        title: String,
        data: List<String>,
        onItemSelected: (String) -> Unit
    ) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.layout_dialog_search_list, null)
        val etSearch = dialogView.findViewById<EditText>(R.id.etSearch)
        val rvList = dialogView.findViewById<RecyclerView>(R.id.rvList)

        lateinit var alertDialog: androidx.appcompat.app.AlertDialog

        val adapter = SimpleStringListAdapter(data) { selected ->
            onItemSelected(selected)
            alertDialog.dismiss()
        }
        rvList.layoutManager = LinearLayoutManager(context)
        rvList.adapter = adapter

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val filtered = data.filter { it.contains(s.toString(), ignoreCase = true) }
                adapter.updateData(filtered)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        alertDialog = MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setView(dialogView)
            .setNegativeButton("Cancel", null)
            .show()
    }
}
