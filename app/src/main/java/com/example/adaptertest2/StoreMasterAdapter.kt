package com.example.adaptertest2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog

class StoreMasterAdapter(private val list: MutableList<StoreMasterItemX>): RecyclerView.Adapter<StoreMasterAdapter.Holder>() {
    class Holder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.store_item, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val current = list[position]

        holder.itemView.apply {
            val storeName = findViewById<TextView>(R.id.storeName)
            val storeCard = findViewById<CardView>(R.id.storeCard)
            storeName.text = current.store_name
//            storeCard.setOnClickListener{
//                val categoryBottomSheet = BottomSheetDialog(context)
//                val categoryBottomSheetView = LayoutInflater.from(context).inflate(R.layout.category_bottomsheet, null)
//                categoryBottomSheet.setContentView(categoryBottomSheetView)
//                categoryBottomSheet.show()
//
//                val categoryRecycler = findViewById<RecyclerView>(R.id.categoryRecycler)
//                val categoryAdapter = CategoryAdapter(mutableListOf())
//                categoryRecycler.adapter = categoryAdapter
//                categoryRecycler.layoutManager = LinearLayoutManager(context)
//            }
        }
    }
    override fun getItemCount(): Int {
        return list.size
    }
    fun addItem(store: StoreMasterItemX){
        list.add(store)
        notifyItemInserted(list.size - 1)
    }
}