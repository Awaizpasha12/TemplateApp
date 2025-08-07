package com.app.banuenterprise.ui.outstanding.ledger.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.banuenterprise.data.model.response.LedgerItems
import com.app.banuenterprise.databinding.ItemCollectionNewBinding

class LedgerListAdapter(
    private var items: List<LedgerItems>
) : RecyclerView.Adapter<LedgerListAdapter.LedgerViewHolder>() {

    private var fullList: List<LedgerItems> = items.toList()

    fun updateList(newItems: List<LedgerItems>) {
        items = newItems
        fullList = newItems.toList()
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        if (query.isBlank()) {
            items = fullList
        } else {
            val lower = query.lowercase()
            items = fullList.filter {
                it.customerName.lowercase().contains(lower) ||
                        it.totalOutstanding.toString().contains(lower) ||
                        it.brandWise.any { brand -> brand.brandName.lowercase().contains(lower) }
            }
        }
        notifyDataSetChanged()
    }

    fun getFilteredListSize(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LedgerViewHolder {
        val binding = ItemCollectionNewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LedgerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LedgerViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class LedgerViewHolder(private val binding: ItemCollectionNewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LedgerItems) {
            binding.tvCustomerName.text = item.customerName
            binding.tvAmount.text = "Total: ₹%.2f".format(item.totalOutstanding)

            // Clear any previous brandwise views
            binding.brandWiseContainer.removeAllViews()

            // Add each brand as a new TextView
            item.brandWise.forEach { brand ->
                val tv = android.widget.TextView(binding.root.context).apply {
                    text = "\u20B9 %.2f • %s".format(brand.outstanding, brand.brandName)
                    setTextColor(android.graphics.Color.parseColor("#424242"))
                    textSize = 14f
                    setPadding(0, 2, 0, 2)
                }
                binding.brandWiseContainer.addView(tv)
            }
        }
    }
}
