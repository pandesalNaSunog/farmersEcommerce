package com.example.adaptertest2


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class SellerProductAdapter(private val list: MutableList<ProductItemX>): RecyclerView.Adapter<SellerProductAdapter.Holder>() {
    class Holder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.seller_product_card,parent,false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val current = list[position]
        holder.itemView.apply {
            val price = findViewById<TextView>(R.id.price)
            val image = findViewById<ImageView>(R.id.image)
            val name = findViewById<TextView>(R.id.name)
            val quantity = findViewById<TextView>(R.id.quantity)
            price.text = "PHP ${current.price}"
            Glide.with(context).load("https://yourzaj.xyz/${current.image}").into(image)
            name.text = current.name
            quantity.text = "Qty: ${current.quantity}"
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun addItem(prod: ProductItemX){
        list.add(prod)
        notifyItemInserted(list.size - 1)
    }

    fun getLastItemId(): Int{
        return list[list.size - 1].id!!
    }
}