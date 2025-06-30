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
import com.app.banuenterprise.data.model.response.InvoiceDetail
import com.app.banuenterprise.databinding.ItemReceiptEntryGroupBinding
import com.app.banuenterprise.ui.outstanding.receiptEntry.ReceiptEntryGroupUiModel
import com.app.banuenterprise.utils.simpleadapters.SimpleStringListAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ReceiptEntryGroupAdapter(
    private var availableInvoices: List<InvoiceDetail>,
    private val onListChanged: () -> Unit
) : RecyclerView.Adapter<ReceiptEntryGroupAdapter.GroupViewHolder>() {

    val items = mutableListOf<ReceiptEntryGroupUiModel>()
    private var amountWatcher: TextWatcher? = null

    // Set invoice list when customer changes, and reset items
    fun setAvailableInvoices(newList: List<InvoiceDetail>) {
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

        // Track TextWatcher per holder
        private var amountWatcher: TextWatcher? = null

        fun bind(entry: ReceiptEntryGroupUiModel) {
            // Set fields or placeholders
            binding.tvInvoiceNumber.text = if (entry.invoiceNumber.isBlank()) "Select Invoice" else entry.invoiceNumber
            binding.tvBrand.text = if (entry.invoiceNumber.isBlank()) "--" else entry.brand

            // === Safe EditText update ===
            // 1. Remove previous watcher if exists
            amountWatcher?.let { binding.etAmount.removeTextChangedListener(it) }

            // 2. Set text based on model
            binding.etAmount.setText(
                if (entry.invoiceNumber.isBlank()) ""
                else if (entry.amount == 0.0) "" else entry.amount.toString()
            )
            binding.etAmount.hint = if (entry.invoiceNumber.isBlank()) "Select invoice first" else ""
            binding.etAmount.isEnabled = entry.invoiceNumber.isNotBlank()

            // 3. Create new watcher and add it
            amountWatcher = object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    // Only update on user edit (avoid after programmatic setText)
                    if (!binding.etAmount.isFocused) return

                    val newVal = s?.toString()?.toDoubleOrNull()
                    if (entry.invoiceNumber.isNotBlank()) {
                        if (newVal != null && entry.amount != newVal) {
                            entry.amount = newVal
                            onListChanged()
                        }
                        // If cleared, set to 0.0
                        else if (newVal == null && entry.amount != 0.0) {
                            entry.amount = 0.0
                            onListChanged()
                        }
                    }
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            }
            binding.etAmount.addTextChangedListener(amountWatcher)

            // ===============================

            val ctx = binding.root.context

            // Only show invoices not already picked in other groups, except for this group
            val alreadyPicked = items.mapNotNull { it.invoiceNumber }.filter { it.isNotBlank() }
            val invoiceChoices = availableInvoices.filter { inv -> inv.billNumber !in alreadyPicked || inv.billNumber == entry.invoiceNumber }
            val invoiceNumbers = invoiceChoices.map { it.billNumber }

            binding.tvInvoiceNumber.setOnClickListener {
                if (invoiceNumbers.isEmpty()) {
                    Toast.makeText(ctx, "No invoices left to select.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                showSearchableDialog(ctx, "Select Invoice", invoiceNumbers) { selectedNo ->
                    val detail = availableInvoices.find { it.billNumber == selectedNo }
                    entry.invoiceNumber = selectedNo
                    entry.brand = detail?.brand ?: ""
                    entry.amount = detail?.amount ?: 0.0
                    entry.defaultAmount = detail?.amount ?: 0.0
                    notifyItemChanged(adapterPosition)
                    onListChanged()
                    // DO NOT call onListChanged() here; TextWatcher will handle it after setText()
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
