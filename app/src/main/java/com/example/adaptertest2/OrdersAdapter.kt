package com.example.adaptertest2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class OrdersAdapter(private val list: MutableList<OrdersItem>): RecyclerView.Adapter<OrdersAdapter.Holder>() {
    class Holder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.order_item, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val current = list[position]
        val separator = ItemBreakDownSeparator()
        holder.itemView.apply {
            val address = findViewById<TextView>(R.id.address)
            val remarks = findViewById<TextView>(R.id.remarks)
            val subtotal = findViewById<TextView>(R.id.subtotal)
            val vatDue = findViewById<TextView>(R.id.vatDueValue)
            val vatRate = findViewById<TextView>(R.id.vatRateValue)
            val total = findViewById<TextView>(R.id.totalValue)
            val status = findViewById<TextView>(R.id.status)
            val itemRecycler = findViewById<RecyclerView>(R.id.itemRecycler)
            val itemAdapter = OrderItemsAdapter(mutableListOf())
            itemRecycler.adapter = itemAdapter
            itemRecycler.layoutManager = LinearLayoutManager(context)

            val listOfItems = separator.separateBySlashN(current.itemsBreakdown)

            for(i in listOfItems.indices){
                if(listOfItems[i] != "") {
                    itemAdapter.addItem(listOfItems[i])
                }
            }
            address.text = current.address
            remarks.text = current.remarks
            subtotal.text = "PHP ${current.subTotal}"
            vatDue.text = "PHP ${current.vatDue}"
            vatRate.text = "PHP ${current.vatRate}"
            total.text = "PHP ${current.total}"
            status.text = current.status
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun addItem(orders: OrdersItem){
        list.add(orders)
        notifyItemInserted(list.size - 1)
    }
}