package com.app.banuenterprise.utils.simpleadapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.banuenterprise.databinding.ItemSimpleListBinding

class SimpleStringListAdapter(
    private var items: List<String>,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<SimpleStringListAdapter.ViewHolder>() {

    fun updateData(newItems: List<String>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemSimpleListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(text: String) {
            binding.tvText.text = text
            binding.root.setOnClickListener { onClick(text) }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSimpleListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])
    override fun getItemCount() = items.size
}
