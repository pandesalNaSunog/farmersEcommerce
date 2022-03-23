package com.example.adaptertest2

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.net.SocketTimeoutException

class WishListAdapter(private val list: MutableList<WishListItemsItem>, private val clearWish: Button, private val wishIsEmpty: LinearLayout): RecyclerView.Adapter<WishListAdapter.Holder>() {
    class Holder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.wishlitst_item, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val current = list[position]

        holder.itemView.apply {

            val db = UserDatabase(context)
            val token = db.getToken()
            val image = findViewById<ImageView>(R.id.image)
            val price = findViewById<TextView>(R.id.price)
            val name = findViewById<TextView>(R.id.name)
            val remove = findViewById<Button>(R.id.remove)
            Glide.with(context).load("https://yourzaj.xyz/${current.product.image}").into(image)
            price.text = "PHP ${current.product.price}"
            name.text = current.product.name

            remove.setOnClickListener{
                val progressBar = ProgressBar()
                val progress = progressBar.showProgressBar(context, R.layout.loading, "Removing...", R.id.progressText)
                val alerts = RequestAlerts(context)

                val jsonObject = JSONObject()
                jsonObject.put("product_id", current.product_id)
                val request = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())

                CoroutineScope(Dispatchers.Main).launch {
                    val removeToWishListResponse = try{ RetrofitInstance.retro.removeToWishList("Bearer $token", request) }
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
                        if(removeToWishListResponse.code() == 200 && removeToWishListResponse.headers().contains(Pair("content-type","application/json"))){
                            AlertDialog.Builder(context)
                                .setTitle("Success")
                                .setMessage("Product has been removed from your wishlist.")
                                .setPositiveButton("OK", null)
                                .show()
                            removeItem(position)
                        }
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun addItem(item: WishListItemsItem){
        list.add(item)
        notifyItemInserted(list.size - 1)
        clearWish.isVisible = true
        wishIsEmpty.isVisible = false
    }
    fun deleteAll(){
        list.clear()
        notifyDataSetChanged()
        clearWish.isVisible = false
        wishIsEmpty.isVisible = true
    }

    fun removeItem(position: Int){
        list.removeAt(position)
        notifyDataSetChanged()
        clearWish.isVisible = list.size != 0
        wishIsEmpty.isVisible = list.size == 0
    }
}