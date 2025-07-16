package com.app.banuenterprise.ui.salesorder.salesentry.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.banuenterprise.R
import com.app.banuenterprise.ui.salesorder.salesentry.model.SalesEntryGroupUiModel

class SalesEntryGroupAdapter(
    private val groupList: MutableList<SalesEntryGroupUiModel>,
    private val onItemSelectClick: (position: Int) -> Unit,
    private val onRemoveClick: (position: Int) -> Unit,
    private val onValueChanged: () -> Unit
) : RecyclerView.Adapter<SalesEntryGroupAdapter.GroupViewHolder>() {

    inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvItemName: TextView = itemView.findViewById(R.id.tvItemName)
        val tvBrand: TextView = itemView.findViewById(R.id.tvBrand)
        val etQuantity: EditText = itemView.findViewById(R.id.etQuantity)
        val etRate: EditText = itemView.findViewById(R.id.etRate)
        val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        val tvCgst: TextView = itemView.findViewById(R.id.tvCgst)
        val tvSgst: TextView = itemView.findViewById(R.id.tvSgst)
        val tvGroupTotal: TextView = itemView.findViewById(R.id.tvGroupTotal)
        val btnRemoveGroup: ImageButton = itemView.findViewById(R.id.btnRemoveGroup)

        var quantityWatcher: TextWatcher? = null
        var rateWatcher: TextWatcher? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sales_entry_group, parent, false)
        return GroupViewHolder(view)
    }

    override fun getItemCount(): Int = groupList.size

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val group = groupList[position]

        // Remove previous watchers before updating text
        holder.etQuantity.removeTextChangedListener(holder.quantityWatcher)
        holder.etRate.removeTextChangedListener(holder.rateWatcher)

        // Item Name and Brand
        holder.tvItemName.text = group.displayText ?: "Select Item"
        holder.tvBrand.text = group.brand ?: "Brand"

        // Quantity and Rate (setText)
        holder.etQuantity.setText(group.quantity.toString())
        holder.etQuantity.setSelection(holder.etQuantity.text.length)
        holder.etRate.setText(if (group.rate == 0.0) "" else group.rate.toString())
        holder.etRate.setSelection(holder.etRate.text.length)

        // Amount, CGST, SGST, Group Total
        holder.tvAmount.text = "Amount: ₹${group.amount.format2()}"
        holder.tvCgst.text = "CGST: ${group.cgstPercent.format1()}% (₹${group.cgstValue.format2()})"
        holder.tvSgst.text = "SGST: ${group.sgstPercent.format1()}% (₹${group.sgstValue.format2()})"
        holder.tvGroupTotal.text = "Total: ₹${group.groupTotal.format2()}"

        // Item selection click
        holder.tvItemName.setOnClickListener {
            onItemSelectClick(position)
        }

        // Add Quantity TextWatcher
        holder.quantityWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val qty = s?.toString()?.toIntOrNull() ?: 1
                if (group.quantity != qty) {
                    group.quantity = qty
                    recalculateGroup(group)
                    onValueChanged()
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        holder.etQuantity.addTextChangedListener(holder.quantityWatcher)

        // Add Rate TextWatcher
        holder.rateWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val rate = s?.toString()?.toDoubleOrNull() ?: 0.0
                if (group.rate != rate) {
                    group.rate = rate
                    recalculateGroup(group)
                    onValueChanged()
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        holder.etRate.addTextChangedListener(holder.rateWatcher)

        // Remove group button
        holder.btnRemoveGroup.visibility = if (groupList.size > 1) View.VISIBLE else View.GONE
        holder.btnRemoveGroup.setOnClickListener {
            onRemoveClick(position)
        }
    }


    // Extension functions for number formatting
    private fun Double.format2(): String = String.format("%.2f", this)
    private fun Double.format1(): String = String.format("%.1f", this)
    fun recalculateGroup(group: SalesEntryGroupUiModel) {
        group.amount = group.quantity * group.rate
        group.cgstValue = group.amount * (group.cgstPercent / 100.0)
        group.sgstValue = group.amount * (group.sgstPercent / 100.0)
        group.groupTotal = group.amount + group.cgstValue + group.sgstValue
    }
}
