package com.example.adaptertest2

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OrderItemsAdapter(private val list: MutableList<String>): RecyclerView.Adapter<OrderItemsAdapter.Holder>() {
    class Holder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.item_breakdown_item, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val current = list[position]
        val separator = ItemBreakDownSeparator()
        holder.itemView.apply {

            Log.e("orderitemsadapter", current)
            val quantityAndName = findViewById<TextView>(R.id.quantityAndName)
            val price = findViewById<TextView>(R.id.price)

            val quantity = separator.getQuantity(current)
            val name = separator.productName(current)
            price.text = "PHP ${separator.getPrice(current)}"

            quantityAndName.text = "${quantity}x $name"
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
    fun addItem(string: String){
        list.add(string)
        notifyItemInserted(list.size - 1)
    }
}