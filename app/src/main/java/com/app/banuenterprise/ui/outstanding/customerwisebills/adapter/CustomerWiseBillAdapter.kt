package com.app.banuenterprise.ui.outstanding.customerwisebills.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.banuenterprise.data.model.response.CustomerData
import com.app.banuenterprise.databinding.ItemCustomerWiseBillBinding

class CustomerWiseBillAdapter(
    private val originalList: List<CustomerData>
) : RecyclerView.Adapter<CustomerWiseBillAdapter.BillViewHolder>() {

    private var filteredList: List<CustomerData> = originalList.toList()

    inner class BillViewHolder(val binding: ItemCustomerWiseBillBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillViewHolder {
        val binding = ItemCustomerWiseBillBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BillViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BillViewHolder, position: Int) {
        val item = filteredList[position]
        holder.binding.tvBillNumber.text = "Bill Number: ${item.billNumber}"
        holder.binding.tvBillDate.text = "Date: ${item.billDate}"
        holder.binding.tvSalesman.text = "Salesman: ${item.salesman}"
        holder.binding.tvRoute.text = "Route: ${item.route}"
        holder.binding.tvCustomerName.text = "Customer: ${item.customer}"
        holder.binding.tvNetValue.text = "Net Value: ₹${item.netValue}"
        holder.binding.tvDay.text = "Day: ${item.day}"
        holder.binding.tvBrand.text = "Brand: ${item.brand}"
        holder.binding.tvCreditDays.text = "Credit Days: ${item.creditDays}"
    }

    override fun getItemCount(): Int = filteredList.size

    fun filter(query: String) {
        filteredList = if (query.isBlank()) {
            originalList
        } else {
            originalList.filter {
                it.billNumber.contains(query, ignoreCase = true) ||
                        it.customer.contains(query, ignoreCase = true) ||
                        it.route.contains(query, ignoreCase = true) ||
                        it.salesman.contains(query, ignoreCase = true) ||
                        it.brand.contains(query, ignoreCase = true) ||
                        it.day.contains(query, ignoreCase = true) ||
                        it.billDate.contains(query, ignoreCase = true) ||
                        it.netValue.toString().contains(query) ||
                        it.creditDays.toString().contains(query)
            }
        }
        notifyDataSetChanged()
    }

    fun getFilteredListSize(): Int = filteredList.size
}
