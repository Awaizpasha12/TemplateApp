package com.app.banuenterprise.ui.salesorder.salescollection.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.banuenterprise.R
import com.app.banuenterprise.data.model.response.SalesOrderItem

class SalesOrderItemAdapter(
    private val items: List<SalesOrderItem>
) : RecyclerView.Adapter<SalesOrderItemAdapter.ItemViewHolder>() {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvItemName: TextView = view.findViewById(R.id.tvItemName)
        val tvQty: TextView = view.findViewById(R.id.tvQty)
        val tvRate: TextView = view.findViewById(R.id.tvRate)
        val tvSubtotal: TextView = view.findViewById(R.id.tvSubtotal)
        val tvGstRate: TextView = view.findViewById(R.id.tvGstRate)
        val tvGstAmount: TextView = view.findViewById(R.id.tvGstAmount)
        val tvTotal: TextView = view.findViewById(R.id.tvTotal)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sales_order_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.tvItemName.text = item.stockItemId.itemName ?: "-"
        holder.tvQty.text = "Qty: ${item.quantity.toInt()}"
        holder.tvRate.text = "Rate: ₹${item.rate.toInt()}"
        holder.tvSubtotal.text = "Subtotal: ₹${item.amount.toInt()}"
        holder.tvGstRate.text = "GST: ${item.gstRate.toInt()}%"
        holder.tvGstAmount.text = "GST Amt: ₹${item.tax.toInt()}"
        holder.tvTotal.text = "Total (incl. GST): ₹${item.total.toInt()}"
    }

}
