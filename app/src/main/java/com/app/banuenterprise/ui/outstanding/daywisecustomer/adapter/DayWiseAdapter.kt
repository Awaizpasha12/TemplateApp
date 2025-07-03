package com.app.banuenterprise.ui.outstanding.daywisecustomer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.banuenterprise.data.model.response.CustomerTotal
import com.app.banuenterprise.databinding.ItemDaywiseCustomerBinding

class DayWiseAdapter(
    private val originalList: List<CustomerTotal>,
    private val onItemClick: (CustomerTotal) -> Unit
) : RecyclerView.Adapter<DayWiseAdapter.DayWiseViewHolder>() {

    private var filteredList: List<CustomerTotal> = originalList.toList()

    inner class DayWiseViewHolder(val binding: ItemDaywiseCustomerBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayWiseViewHolder {
        val binding = ItemDaywiseCustomerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DayWiseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DayWiseViewHolder, position: Int) {
        val item = filteredList[position]
        holder.binding.tvCustomerName.text = item.customerName
        holder.binding.tvCustomerTotal.text = "Total: ₹${item.totalPendingAmount}"
        holder.binding.tvRoute.text = "Route: ${item.route ?: "-"}"
        holder.binding.root.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = filteredList.size

    fun filter(query: String) {
        filteredList = if (query.isBlank()) {
            originalList
        } else {
            originalList.filter {
                it.customerName.contains(query, ignoreCase = true) ||
                        (it.route?.contains(query, ignoreCase = true) ?: false) ||
                        it.totalPendingAmount.toString().contains(query)
            }
        }
        notifyDataSetChanged()
    }
    fun getFilteredListSize(): Int = filteredList.size

}
