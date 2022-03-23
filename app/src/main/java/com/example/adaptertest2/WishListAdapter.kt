package com.example.adaptertest2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class WishListAdapter(private val list: MutableList<WishListItemsItem>): RecyclerView.Adapter<WishListAdapter.Holder>() {
    class Holder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.wishlitst_item, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val current = list[position]

        holder.itemView.apply {
            val image = findViewById<ImageView>(R.id.image)
            val price = findViewById<TextView>(R.id.price)
            val name = findViewById<TextView>(R.id.name)
            val remove = findViewById<Button>(R.id.remove)

            Glide.with(context).load("https://yourzaj.xyz/${current.product.image}").into(image)
            price.text = "PHP ${current.product.price}"
            name.text = current.product.name
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}