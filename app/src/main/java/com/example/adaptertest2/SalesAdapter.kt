package com.example.adaptertest2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog

class SalesAdapter(private val list: MutableList<SalesDetailsItem>): RecyclerView.Adapter<SalesAdapter.Holder>() {
    class Holder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.sales_item, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val current = list[position]
        holder.itemView.apply {

            val image = findViewById<ImageView>(R.id.image)
            val price = findViewById<TextView>(R.id.price)
            val name = findViewById<TextView>(R.id.name)
            val total = findViewById<TextView>(R.id.total)
            val quantity = findViewById<TextView>(R.id.quantity)

            Glide.with(context).load("https://yourzaj.xyz/${current.product.image}").into(image)
            price.text = "Base Price: PHP ${current.price}"
            name.text = current.product.name
            total.text = "Total Price: PHP ${current.total}"
            quantity.text ="Qty: ${current.quantity}"


        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun addItem(item: SalesDetailsItem){
        list.add(item)
        notifyItemInserted(list.size - 1)
    }
}