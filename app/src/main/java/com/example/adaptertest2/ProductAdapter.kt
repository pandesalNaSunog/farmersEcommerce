package com.example.adaptertest2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ProductAdapter(private val list: MutableList<ProductsItem>): RecyclerView.Adapter<ProductAdapter.Holder>() {
    class Holder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.product_card,parent,false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val current = list[position]
        holder.itemView.apply {
            val price = findViewById<TextView>(R.id.price)
            val image = findViewById<ImageView>(R.id.image)
            val name = findViewById<TextView>(R.id.name)

            price.text = "PHP ${current.price}"
            Glide.with(context).load("https://yourzaj.xyz/${current.image}").into(image)
            name.text = current.name
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun addItem(prod: ProductsItem){
        list.add(prod)
        notifyItemInserted(list.size - 1)
    }
}