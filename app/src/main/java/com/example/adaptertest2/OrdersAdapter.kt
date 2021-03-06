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
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.*
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.net.SocketTimeoutException

class OrdersAdapter(private val list: MutableList<OrdersItem>): RecyclerView.Adapter<OrdersAdapter.Holder>() {
    class Holder(itemView: View): RecyclerView.ViewHolder(itemView)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.order_item, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val current = list[position]
        val separator = ItemBreakDownSeparator()
        holder.itemView.apply {
            val statusBackground = findViewById<ConstraintLayout>(R.id.orderStatusBackground)
            val db = UserDatabase(context)
            val token = db.getToken()
            val mark = findViewById<Button>(R.id.markAsReceived)
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

            mark.isVisible = current.status != "Completed"

            if(current.status == "Packaging"){
                statusBackground.setBackgroundColor(resources.getColor(R.color.packaging))
            }else if(current.status == "Delivery"){
                statusBackground.setBackgroundColor(resources.getColor(R.color.teal_200))
            }else{
                statusBackground.setBackgroundColor(resources.getColor(R.color.green))
            }

            mark.setOnClickListener{
                AlertDialog.Builder(context)
                    .setTitle("Mark as Received")
                    .setMessage("To mark this order as received, you should upload any proof of transaction.")
                    .setPositiveButton("OK") { _, _ ->



                        val intent = Intent(context,ImageCapture::class.java)
                        intent.putExtra("order_id", current.id)
                        startActivity(context,intent,null)
                    }
                    .show()
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun addItem(orders: OrdersItem) {
        list.add(orders)
        notifyItemInserted(list.size - 1)
    }


}