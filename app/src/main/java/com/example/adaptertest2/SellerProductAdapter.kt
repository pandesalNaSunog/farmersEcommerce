package com.example.adaptertest2


import android.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.net.SocketTimeoutException

class SellerProductAdapter(private val list: MutableList<ProductItemX>): RecyclerView.Adapter<SellerProductAdapter.Holder>() {
    class Holder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.seller_product_card,parent,false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val current = list[position]
        holder.itemView.apply {
            val db = UserDatabase(context)
            val token = db.getToken()
            val user = db.getAll()
            val viewFeedBacks = findViewById<Button>(R.id.viewFeedBacks)
            val price = findViewById<TextView>(R.id.price)
            val image = findViewById<ImageView>(R.id.image)
            val name = findViewById<TextView>(R.id.name)
            val quantity = findViewById<TextView>(R.id.quantity)
            val updateQuantity = findViewById<Button>(R.id.updateQuantity)
            val updatePrice = findViewById<Button>(R.id.updatePrice)
            price.text = "PHP ${current.price}"
            Glide.with(context).load("https://yourzaj.xyz/${current.image}").into(image)
            name.text = current.name
            quantity.text = "Qty: ${current.quantity}"

            updatePrice.setOnClickListener {
                val updatePriceAlert = AlertDialog.Builder(context)
                val updatePriceAlertView = LayoutInflater.from(context).inflate(R.layout.update_product_price, null)
                updatePriceAlert.setView(updatePriceAlertView)
                val updatePriceAlertShow = updatePriceAlert.show()

                val newPrice = updatePriceAlertView.findViewById<EditText>(R.id.newPrice)
                val confirm = updatePriceAlertView.findViewById<Button>(R.id.confirm)

                confirm.setOnClickListener {
                    Log.e("jkladjfaf", current.id.toString())
                    if(newPrice.text.isEmpty()){
                        newPrice.error = "Please fill out this field"
                    }else{
                        AlertDialog.Builder(context)
                            .setTitle("Confirm")
                            .setMessage("Change ${current.name}'s price to PHP ${newPrice.text}?")
                            .setPositiveButton("YES") {_,_->
                                val progressBar = ProgressBar()
                                val alerts = RequestAlerts(context)

                                val progress = progressBar.showProgressBar(context, R.layout.loading, "Please Wait...", R.id.progressText)

                                val jsonObject = JSONObject()
                                jsonObject.put("product_id", current.id)
                                jsonObject.put("new_price", newPrice.text.toString())
                                val request = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
                                CoroutineScope(Dispatchers.IO).launch {
                                    val updatePriceResponse = try{ RetrofitInstance.retro.updatePrice("Bearer $token", request) }
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
                                        if(updatePriceResponse.isSuccessful){
                                            price.text = "PHP ${newPrice.text}"
                                            AlertDialog.Builder(context)
                                                .setTitle("Success")
                                                .setMessage("Product price updated successfully.")
                                                .setPositiveButton("OK", null)
                                                .show()
                                            updatePriceAlertShow.dismiss()
                                        }else{
                                            Log.e("SellerProductAdapter", updatePriceResponse.errorBody()!!.string())
                                            AlertDialog.Builder(context)
                                                .setTitle("Error")
                                                .setMessage("Something went wrong.")
                                                .setPositiveButton("OK", null)
                                                .show()
                                            updatePriceAlertShow.dismiss()
                                        }
                                    }
                                }
                            }.setNegativeButton("NO", null)
                            .show()

                    }
                }
            }

            updateQuantity.setOnClickListener {
                var quantityValue = 0
                val alert = AlertDialog.Builder(context)
                val alertView = LayoutInflater.from(context).inflate(R.layout.quantity_selector, null)
                alert.setView(alertView)
                val show = alert.show()

                val increase = alertView.findViewById<Button>(R.id.increase)
                val decrese = alertView.findViewById<Button>(R.id.decrease)
                val quantityText = alertView.findViewById<TextView>(R.id.quantityText)
                val confirm = alertView.findViewById<Button>(R.id.confirm)
                quantityValue = current.quantity!!.toInt()
                quantityText.text = current.quantity

                increase.setOnClickListener {
                    quantityValue++
                    quantityText.text = quantityValue.toString()
                }
                decrese.setOnClickListener {
                    if(quantityValue != 1) {
                        quantityValue--
                        quantityText.text = quantityValue.toString()
                    }
                }

                confirm.setOnClickListener {
                    val progressBar = ProgressBar()
                    val progress = progressBar.showProgressBar(context,R.layout.loading,"Updating...",R.id.progressText)
                    val alerts = RequestAlerts(context)

                    val jsonObject = JSONObject()
                    jsonObject.put("product_id", current.id)
                    jsonObject.put("quantity", quantityText.text.toString())

                    val request = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())

                    CoroutineScope(Dispatchers.IO).launch {
                        val updateResponse = try{ RetrofitInstance.retro.updateQuantity("Bearer $token",request) }
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
                            if(updateResponse.code() == 200){
                                AlertDialog.Builder(context)
                                    .setTitle("Success")
                                    .setMessage("Product quantity successfully updated.")
                                    .setPositiveButton("OK", null)
                                    .show()
                                show.dismiss()
                                quantity.text = "Qty: ${quantityText.text.toString()}"
                            }else{
                                show.dismiss()
                                alerts.somethingWentWrongAlert()
                            }
                        }
                    }
                }
            }

            viewFeedBacks.setOnClickListener {
                val feedbackSheet = BottomSheetDialog(context)
                val feedbackView = LayoutInflater.from(context).inflate(R.layout.feedbak_container, null)
                feedbackSheet.setContentView(feedbackView)
                feedbackSheet.show()
                val ratingGrid = feedbackView.findViewById<GridLayout>(R.id.ratingGrid)
                if(user?.type == "seller"){
                    ratingGrid.isVisible = false
                }
                val postComment = feedbackView.findViewById<Button>(R.id.postComment)
                val writeComment = feedbackView.findViewById<EditText>(R.id.writeComment)
                val feedbackRecycler = feedbackView.findViewById<RecyclerView>(R.id.feedbackRecycler)
                val feedbackAdapter = FeedBackAdapter(mutableListOf())
                feedbackRecycler.adapter = feedbackAdapter
                feedbackRecycler.layoutManager = LinearLayoutManager(context)

                val noFeedbacks = feedbackView.findViewById<LinearLayout>(R.id.noFeedBacks)

                var progressBar = ProgressBar()
                var progress = progressBar.showProgressBar(context,R.layout.loading, "Loading", R.id.progressText)
                var alerts = RequestAlerts(context)

                CoroutineScope(Dispatchers.IO).launch {
                    val feedbackResponse = try{ RetrofitInstance.retro.getFeedBack(current.id!!) }
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
                            Log.e("sellerproductadapter", e.toString())
                        }
                        return@launch
                    }

                    withContext(Dispatchers.Main){
                        progress.dismiss()
                        if(feedbackResponse.feedbacks.isNotEmpty()){
                            noFeedbacks.isVisible = false
                            for(i in feedbackResponse.feedbacks.indices){
                                feedbackAdapter.addItem(feedbackResponse.feedbacks[i])
                            }
                        }else{
                            noFeedbacks.isVisible = true
                        }

                    }
                }

                postComment.setOnClickListener {
                    if(writeComment.text.isEmpty()){
                        writeComment.error = "Please write a comment."
                    }else{
                        val jsonObject = JSONObject()
                        jsonObject.put("product_id", current.id)
                        jsonObject.put("star", 5)
                        jsonObject.put("message", writeComment.text.toString())

                        val request = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())

                        progressBar = ProgressBar()
                        progress = progressBar.showProgressBar(context,R.layout.loading, "Posting...", R.id.progressText)
                        alerts = RequestAlerts(context)

                        CoroutineScope(Dispatchers.IO).launch {
                            val postCommentResponse = try{ RetrofitInstance.retro.writeFeedBack("Bearer $token",request) }
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
                                if((postCommentResponse.code() == 200 || postCommentResponse.code() == 201) && postCommentResponse.headers().contains(Pair("content-type","application/json"))){
                                    AlertDialog.Builder(context)
                                        .setTitle("Success")
                                        .setMessage("Your comment has been posted.")
                                        .setPositiveButton("OK", null)
                                        .show()
                                    feedbackSheet.dismiss()
                                }else{
                                    AlertDialog.Builder(context)
                                        .setTitle("Error")
                                        .setMessage("Something went wrong.")
                                        .setPositiveButton("OK", null)
                                        .show()
                                    feedbackSheet.dismiss()
                                }
                            }
                        }
                    }
                }
            }
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