package com.example.adaptertest2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog

class CategoryAdapter(private val list: MutableList<CategoryX>): RecyclerView.Adapter<CategoryAdapter.Holder>() {
    class Holder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.store_item, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val current = list[position]

        holder.itemView.apply {
            val category = findViewById<TextView>(R.id.storeName)
            category.text = current.category
        }
    }
    override fun getItemCount(): Int {
        return list.size
    }
    fun addItem(category: CategoryX){
        list.add(category)
        notifyItemInserted(list.size - 1)
    }
}