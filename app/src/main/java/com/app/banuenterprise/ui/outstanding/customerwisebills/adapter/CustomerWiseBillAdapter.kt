package com.app.banuenterprise.ui.outstanding.customerwisebills.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.banuenterprise.data.model.response.BillItem
import com.app.banuenterprise.databinding.ItemCustomerWiseBillBinding

class CustomerWiseBillAdapter(
    private val originalList: List<BillItem>
) : RecyclerView.Adapter<CustomerWiseBillAdapter.BillViewHolder>() {

    private var filteredList: List<BillItem> = originalList.toList()

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
//        holder.binding.tvSalesman.text = "Salesman: ${item.salesman}"
        holder.binding.tvRoute.text = "Route: ${item.route}"
        holder.binding.tvCustomerName.text = "Customer: ${item.customerName}"
        holder.binding.tvNetValue.text = "Net Value: ₹${item.netValue}"
        holder.binding.tvPendingAmount.text = "Pending Amount: ${item.pendingAmount}"
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
                        it.customerName.contains(query, ignoreCase = true) ||
                        it.route.contains(query, ignoreCase = true) ||
//                        it.salesman.contains(query, ignoreCase = true) ||
                        it.brand.contains(query, ignoreCase = true) ||
//                        it.day.contains(query, ignoreCase = true) ||
                        it.billDate.contains(query, ignoreCase = true) ||
                        it.pendingAmount.toString().contains(query) ||
                        it.creditDays.toString().contains(query)
            }
        }
        notifyDataSetChanged()
    }

    fun getFilteredListSize(): Int = filteredList.size
}
