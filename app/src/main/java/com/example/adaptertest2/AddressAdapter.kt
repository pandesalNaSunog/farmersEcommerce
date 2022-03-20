package com.example.adaptertest2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AddressAdapter(private val list: MutableList<String>): RecyclerView.Adapter<AddressAdapter.Holder>() {
    class Holder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.address_item, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val currItem = list[position]

        holder.itemView.apply {
            val address = findViewById<TextView>(R.id.address)
            address.text = currItem
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun addItem(address: String){
        list.add(address)
        notifyItemInserted(list.size - 1)
    }
}