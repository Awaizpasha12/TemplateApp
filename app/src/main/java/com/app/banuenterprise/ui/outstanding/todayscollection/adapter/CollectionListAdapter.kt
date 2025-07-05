package com.app.banuenterprise.ui.outstanding.todayscollection

import android.app.Dialog
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.banuenterprise.R
import com.app.banuenterprise.data.model.response.CollectionItem
import com.app.banuenterprise.databinding.ItemCollectionBinding
import com.bumptech.glide.Glide

class CollectionListAdapter(
    private var items: List<CollectionItem>
) : RecyclerView.Adapter<CollectionListAdapter.CollectionViewHolder>() {
    private var fullList: List<CollectionItem> = items.toList()

    fun updateList(newItems: List<CollectionItem>) {
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
                        it.brand.lowercase().contains(lower) ||
                        it.amount.toString().contains(lower) ||
                        it.mode.lowercase().contains(lower)
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
        fun bind(item: CollectionItem) {
            binding.tvCustomerName.text = item.customerName
            binding.tvAmount.text = "₹%.2f".format(item.amount)
            binding.tvStatus.text = item.status.replaceFirstChar { it.uppercase() }

            // Status tag coloring (dynamic)
            when (item.status.lowercase()) {
                "pending" -> binding.tvStatus.setBackgroundResource(R.drawable.bg_status_tag)
                "approved" -> {
                    binding.tvStatus.setBackgroundResource(R.drawable.bg_status_tag)
                    binding.tvStatus.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.success_green))
                }
                "rejected" -> {
                    binding.tvStatus.setBackgroundResource(R.drawable.bg_status_tag)
                    binding.tvStatus.setBackgroundColor(Color.RED)
                }
                else -> binding.tvStatus.setBackgroundResource(R.drawable.bg_status_tag)
            }

            binding.tvBillNumber.text = "Bill No: ${item.billNumber}"
            binding.tvBrand.text = "Brand: ${item.brand}"
            binding.tvRoute.text = "Route: ${item.route}"
            binding.tvMode.text = item.mode.replaceFirstChar { it.uppercase() }
            binding.tvRemarks.text = "Remarks: ${item.remarks}"

            // Load proof image with Glide
            Glide.with(binding.ivProof)
                .load(item.proofUrl)
                .placeholder(R.drawable.ic_photo)
                .error(R.drawable.ic_photo)
                .into(binding.ivProof)

            // On click: expand image in a dialog
            binding.ivProof.setOnClickListener {
                showProofDialog(item.proofUrl)
            }
        }

        private fun showProofDialog(imageUrl: String) {
            val context = binding.root.context
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_image_preview)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            val imageView = dialog.findViewById<ImageView>(R.id.imagePreview)
            Glide.with(imageView)
                .load(imageUrl)
                .placeholder(R.drawable.ic_photo)
                .error(R.drawable.ic_photo)
                .into(imageView)

            imageView.setOnClickListener { dialog.dismiss() }
            dialog.show()
        }
    }
}
