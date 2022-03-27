package com.example.adaptertest2

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
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
            vatRate.text = "PHP ${current.vatRate}"
            total.text = "PHP ${current.total}"
            status.text = current.status

            mark.isVisible = current.status != "Completed"

            if(current.status == "Packaging"){
                statusBackground.setBackgroundColor(resources.getColor(R.color.packaging))
            }else if(current.status == "Deliver"){
                statusBackground.setBackgroundColor(resources.getColor(R.color.teal_200))
            }else{
                statusBackground.setBackgroundColor(resources.getColor(R.color.green))
            }

            mark.setOnClickListener{
                AlertDialog.Builder(context)
                    .setTitle("Mark as Received")
                    .setMessage("Mark this item as received?")
                    .setPositiveButton("YES"){_,_->
                        val progressBar = ProgressBar()
                        val progress = progressBar.showProgressBar(context, R.layout.loading, "Marking...", R.id.progressText)
                        val alerts = RequestAlerts(context)

                        val jsonObject = JSONObject()
                        jsonObject.put("order_id", current.id)

                        val request = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
                        CoroutineScope(Dispatchers.IO).launch {
                            val markAsCompleteResponse = try{ RetrofitInstance.retro.markOrderAsCompleted("Bearer $token",request) }
                            catch(e: SocketTimeoutException){
                                withContext(Dispatchers.Main){
                                    progress.dismiss()
                                    alerts.showSocketTimeOutAlert()
                                }
                                return@launch
                            }catch(e: Exception){
                                withContext(Dispatchers.Main){
                                    progress.dismiss()
                                    alerts.noInternetAlert()
                                }
                                return@launch
                            }

                            withContext(Dispatchers.Main){
                                progress.dismiss()
                                if(markAsCompleteResponse.code() == 200 && markAsCompleteResponse.headers().contains(Pair("content-type","application/json"))){
                                    mark.isVisible = false
                                    status.text = "Completed"
                                    statusBackground.setBackgroundColor(resources.getColor(R.color.green))
                                }else{
                                    AlertDialog.Builder(context)
                                        .setTitle("Error")
                                        .setMessage("Something went wrong.")
                                        .setPositiveButton("OK", null)
                                        .show()
                                }
                            }
                        }
                    }
                    .setNegativeButton("NO", null)
                    .show()
            }
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