package com.app.banuenterprise.ui.outstanding.ledger.adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.banuenterprise.data.model.response.LedgerItems
import com.app.banuenterprise.databinding.ItemCollectionBinding

class LedgerListAdapter(
    private var items: List<LedgerItems>
) : RecyclerView.Adapter<LedgerListAdapter.CollectionViewHolder>() {
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
                        it.amount.toString().contains(lower)
            }
        }
        notifyDataSetChanged()
    }
    fun getFilteredListSize(): Int = items.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionViewHolder {
        val binding = ItemCollectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CollectionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CollectionViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class CollectionViewHolder(private val binding: ItemCollectionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LedgerItems) {
            binding.tvCustomerName.text = item.customerName
            binding.tvAmount.text = "₹%.2f".format(item.amount)
        }
    }
}
