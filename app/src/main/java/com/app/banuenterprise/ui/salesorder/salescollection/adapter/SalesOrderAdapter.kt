package com.app.banuenterprise.ui.salesorder.salescollection.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.banuenterprise.R
import com.app.banuenterprise.data.model.response.CollectionItem
import com.app.banuenterprise.data.model.response.SalesOrder

class SalesOrderAdapter(
    private var salesOrders: List<SalesOrder>
) : RecyclerView.Adapter<SalesOrderAdapter.SalesOrderViewHolder>() {
    private var fullList: List<SalesOrder> = salesOrders.toList()

    class SalesOrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvInvoiceNumber: TextView = view.findViewById(R.id.tvInvoiceNumber)
        val tvInvoiceDate: TextView = view.findViewById(R.id.tvInvoiceDate)
        val tvLedgerName: TextView = view.findViewById(R.id.tvLedgerName)
        val tvTotalAmount: TextView = view.findViewById(R.id.tvTotalAmount)
        val rvInvoiceItems: RecyclerView = view.findViewById(R.id.rvInvoiceItems)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalesOrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sales_order, parent, false)
        return SalesOrderViewHolder(view)
    }

    override fun getItemCount(): Int = salesOrders.size

    override fun onBindViewHolder(holder: SalesOrderViewHolder, position: Int) {
        val order = salesOrders[position]
        holder.tvInvoiceNumber.text = "Invoice No: ${order.invoiceNumber}"
        holder.tvInvoiceDate.text = order.createdAt.substring(0, 10) // YYYY-MM-DD
        holder.tvLedgerName.text = "Ledger: ${order.ledgerId.ledgerName}"
        holder.tvTotalAmount.text = "Total: ₹${order.totalAmount}"

        // Set up the nested RecyclerView for items
        holder.rvInvoiceItems.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.rvInvoiceItems.adapter = SalesOrderItemAdapter(order.itemsList)
    }

    fun updateList(newList: List<SalesOrder>) {
        salesOrders = newList
        fullList = newList.toList()
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        if (query.isBlank()) {
            salesOrders = fullList
        } else {
            val lower = query.lowercase()
            salesOrders = fullList.filter {
                it.invoiceNumber.lowercase().contains(lower) ||
                        it.ledgerId.ledgerName.lowercase().contains(lower)
            }
        }
        notifyDataSetChanged()
    }
    fun getFilteredListSize(): Int = salesOrders.size

}
