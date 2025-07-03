package com.app.banuenterprise.ui.outstanding.invoicenumberentry.adapter
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
import com.app.banuenterprise.databinding.ItemInvoiceEntryGroupBinding
import com.app.banuenterprise.ui.outstanding.invoicenumberentry.InvoiceEntryGroupUiModel
import com.app.banuenterprise.utils.simpleadapters.SimpleStringListAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class InvoiceEntryGroupAdapter(
    private var availableInvoices: List<BillItem>,
    private val onListChanged: () -> Unit
) : RecyclerView.Adapter<InvoiceEntryGroupAdapter.GroupViewHolder>() {

    val items = mutableListOf<InvoiceEntryGroupUiModel>()
    private val alreadyPicked = mutableSetOf<String>()

    fun setAvailableInvoices(newList: List<BillItem>) {
        availableInvoices = newList
        clearAll()
        notifyDataSetChanged()
    }

    fun addGroup() {
        items.add(InvoiceEntryGroupUiModel())
        notifyItemInserted(items.size - 1)
        onListChanged()
    }

    fun removeGroup(position: Int) {
        if (position in items.indices) {
            alreadyPicked.remove(items[position].invoiceDisplayText)
            items.removeAt(position)
            notifyItemRemoved(position)
            onListChanged()
        }
    }

    fun clearAll() {
        items.clear()
        alreadyPicked.clear()
        notifyDataSetChanged()
        onListChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemInvoiceEntryGroupBinding.inflate(inflater, parent, false)
        return GroupViewHolder(binding)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class GroupViewHolder(private val binding: ItemInvoiceEntryGroupBinding) : RecyclerView.ViewHolder(binding.root) {
        private var amountWatcher: TextWatcher? = null

        fun bind(entry: InvoiceEntryGroupUiModel) {
            val ctx = binding.root.context

            binding.tvInvoiceSelector.text = if (entry.invoiceDisplayText.isBlank()) "Select Invoice" else entry.invoiceDisplayText
            binding.tvCustomerName.text = entry.customerName.ifBlank { "--" }
            binding.tvBrand.text = entry.brand.ifBlank { "--" }

            // Amount field setup
            amountWatcher?.let { binding.etAmount.removeTextChangedListener(it) }

            binding.etAmount.setText(
                if (entry.invoiceNumber.isBlank()) ""
                else if (entry.amount == 0.0) "" else entry.amount.toString()
            )
            binding.etAmount.hint = if (entry.invoiceNumber.isBlank()) "Select invoice first" else ""
            binding.etAmount.isEnabled = entry.invoiceNumber.isNotBlank()

            amountWatcher = object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (!binding.etAmount.isFocused) return
                    val newVal = s?.toString()?.toDoubleOrNull()
                    if (entry.invoiceNumber.isNotBlank()) {
                        entry.amount = newVal ?: 0.0
                        onListChanged()
                    }
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            }
            binding.etAmount.addTextChangedListener(amountWatcher)

            // Setup Invoice Selection
            val invoiceChoices = availableInvoices.filter { inv ->
                val label = "${inv.billNumber} - ${inv.brand}"
                !alreadyPicked.contains(label) || label == entry.invoiceDisplayText
            }

            val displayList = invoiceChoices.map { "${it.billNumber} - ${it.brand}" }

            binding.tvInvoiceSelector.setOnClickListener {
                if (displayList.isEmpty()) {
                    Toast.makeText(ctx, "No invoices left", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                showSearchableDialog(ctx, "Select Invoice", displayList) { selectedText ->
                    val item = availableInvoices.find {
                        "${it.billNumber} - ${it.brand}" == selectedText
                    }

                    if (item != null) {
                        entry.invoiceNumber = item.billNumber
                        entry.brand = item.brand
                        entry.customerName = item.customerName
                        entry.amount = item.pendingAmount.toDouble()
                        entry.defaultAmount = item.pendingAmount.toDouble()
                        entry.billItemId = item._id
                        entry.invoiceDisplayText = selectedText

                        alreadyPicked.add(selectedText)

                        notifyItemChanged(adapterPosition)
                        onListChanged()
                    }

                }
            }

            binding.btnRemoveGroup.setOnClickListener {
                removeGroup(adapterPosition)
            }
        }
    }

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
