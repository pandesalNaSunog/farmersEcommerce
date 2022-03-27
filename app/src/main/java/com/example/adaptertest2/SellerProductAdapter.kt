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
            price.text = "PHP ${current.price}"
            Glide.with(context).load("https://yourzaj.xyz/${current.image}").into(image)
            name.text = current.name
            quantity.text = "Qty: ${current.quantity}"

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
                        for(i in feedbackResponse.feedbacks.indices){
                            feedbackAdapter.addItem(feedbackResponse.feedbacks[i])
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