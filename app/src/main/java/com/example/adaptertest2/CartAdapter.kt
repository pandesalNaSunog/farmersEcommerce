package com.example.adaptertest2

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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

class CartAdapter(private val list: MutableList<CartItem>, private val clearCart: GridLayout, private val cartIsEmpty: LinearLayout): RecyclerView.Adapter<CartAdapter.Holder>() {
    class Holder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.cart_item, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val current = list[position]
        holder.itemView.apply {
            val db = UserDatabase(context)
            val token = db.getToken()
            val image = findViewById<ImageView>(R.id.image)
            val price = findViewById<TextView>(R.id.price)
            val name = findViewById<TextView>(R.id.name)
            val decrease = findViewById<Button>(R.id.decrease)
            val quantityText = findViewById<TextView>(R.id.quantity)
            val increase = findViewById<Button>(R.id.increase)
            val remove = findViewById<Button>(R.id.remove)

            Glide.with(context).load("https://yourzaj.xyz/${current.product.image}").into(image)
            price.text = "PHP ${current.product.price}"
            name.text = current.product.name
            quantityText.text = "Qty: ${current.quantity}"

            remove.setOnClickListener{
                val progress = ProgressBar()
                val progressBar = progress.showProgressBar(context,R.layout.loading,"Removing...",R.id.progressText)
                val alerts = RequestAlerts(context)

                val jsonObject = JSONObject()
                jsonObject.put("product_id", current.product_id)

                val request = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
                CoroutineScope(Dispatchers.IO).launch {
                    val removeResponse = try{ RetrofitInstance.retro.removeCartItem("Bearer $token",request) }
                    catch(e: SocketTimeoutException){
                        withContext(Dispatchers.Main){
                            progressBar.dismiss()
                            alerts.showSocketTimeOutAlert()
                        }
                        return@launch
                    }catch(e: Exception){
                        withContext(Dispatchers.Main){
                            progressBar.dismiss()
                            alerts.noInternetAlert()
                        }
                        return@launch
                    }

                    withContext(Dispatchers.Main){
                        progressBar.dismiss()
                        if(removeResponse.code() == 200 && removeResponse.headers().contains(Pair("content-type","application/json"))){
                            AlertDialog.Builder(context)
                                .setTitle("Success")
                                .setMessage("Item has been removed from your cart.")
                                .setPositiveButton("OK", null)
                                .show()
                            removeItem(position)
                            if(list.size == 0){
                                clearCart.isVisible = false
                                cartIsEmpty.isVisible = true
                            }else{
                                clearCart.isVisible = true
                                cartIsEmpty.isVisible = false
                            }
                        }else{
                            AlertDialog.Builder(context)
                                .setTitle("error")
                                .setMessage("Something went wrong.")
                                .setPositiveButton("OK", null)
                                .show()
                        }
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun addItem(cart: CartItem){
        list.add(cart)
        notifyItemInserted(list.size - 1)
        clearCart.isVisible = true
        cartIsEmpty.isVisible = false
    }

    fun removeItem(position: Int){
        list.removeAt(position)
        notifyDataSetChanged()
        clearCart.isVisible = list.size != 0
    }

    fun deleteAll(){
        list.clear()
        notifyDataSetChanged()
        clearCart.isVisible = false
        cartIsEmpty.isVisible = true
    }
}