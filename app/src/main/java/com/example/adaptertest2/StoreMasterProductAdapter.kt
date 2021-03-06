package com.example.adaptertest2

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class StoreMasterProductAdapter(private val list: MutableList<StoreMasterProductItem>): RecyclerView.Adapter<StoreMasterProductAdapter.Holder>() {
    class Holder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.product_card,parent,false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val current = list[position]
        holder.itemView.apply {
            val db = UserDatabase(context)
            val user = db.getAll()
            val owned = findViewById<TextView>(R.id.owned)
            val price = findViewById<TextView>(R.id.price)
            val image = findViewById<ImageView>(R.id.image)
            val name = findViewById<TextView>(R.id.name)
            val card = findViewById<CardView>(R.id.productCard)
            price.text = "PHP ${current.price}"
            Glide.with(context).load("https://yourzaj.xyz/${current.image}").into(image)
            name.text = current.name
            owned.isVisible = user?.id == current.store_owner?.id
            Log.e("Product Adapter", "user_id = ${user?.id}, store owner id = ${current.store_owner}")
            card.setOnClickListener {
                val intent = Intent(context,ProductViewer::class.java)
                intent.putExtra("image", current.image)
                intent.putExtra("name", current.name)
                intent.putExtra("price", "PHP ${current.price}")
                intent.putExtra("desc", current.description)
                intent.putExtra("id", current.id)
                intent.putExtra("quantity", current.quantity)
                intent.putExtra("store_owner_id", current.store_owner.id)
                startActivity(context,intent,null)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun addItem(prod: StoreMasterProductItem){
        list.add(prod)
        notifyItemInserted(list.size - 1)
    }
}