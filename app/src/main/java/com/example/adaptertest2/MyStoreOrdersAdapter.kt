package com.example.adaptertest2

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.*
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.net.SocketTimeoutException

class MyStoreOrdersAdapter(private val list: MutableList<StoreOrdersItem>): RecyclerView.Adapter<MyStoreOrdersAdapter.Holder>() {
    class Holder(itemView: View): RecyclerView.ViewHolder(itemView)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.my_store_order_item, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val current = list[position]
        val separator = ItemBreakDownSeparator()
        holder.itemView.apply {
            val statusBackground = findViewById<ConstraintLayout>(R.id.orderStatusBackground)
            val db = UserDatabase(context)
            val token = db.getToken()
            val viewReceipt = findViewById<Button>(R.id.viewReceipt)
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
            vatRate.text = "${current.vatRate}%"
            total.text = "PHP ${current.total}"
            status.text = current.status

            if(current.status == "Packaging"){
                statusBackground.setBackgroundColor(resources.getColor(R.color.packaging))
            }else if(current.status == "Delivery"){
                statusBackground.setBackgroundColor(resources.getColor(R.color.teal_200))
            }else{
                statusBackground.setBackgroundColor(resources.getColor(R.color.green))
            }

            viewReceipt.setOnClickListener{
                if(current.pop.isEmpty()){
                    AlertDialog.Builder(context)
                        .setTitle("Empty Proof of Payment")
                        .setMessage("There is no proof of payment uploaded to this particular order.")
                        .setPositiveButton("OK", null)
                        .show()
                }else{
                    val alert = AlertDialog.Builder(context)
                    val alertView = LayoutInflater.from(context).inflate(R.layout.receipt, null)
                    alert.setView(alertView)
                    alert.show()

                    val popImage = alertView.findViewById<ImageView>(R.id.popImage)

                    Glide.with(context).load("https://yourzaj.xyz/${current.pop[0].image}").error(R.drawable.ic_baseline_broken_image_24).into(popImage)
                }

            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun addItem(orders: StoreOrdersItem) {
        list.add(orders)
        notifyItemInserted(list.size - 1)
    }


}