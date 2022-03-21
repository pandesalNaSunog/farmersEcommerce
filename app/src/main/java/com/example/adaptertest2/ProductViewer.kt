package com.example.adaptertest2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
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

class ProductViewer : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_viewer)

        val imageView = findViewById<ImageView>(R.id.imageView)
        val nameView = findViewById<TextView>(R.id.nameView)
        val priceView = findViewById<TextView>(R.id.priceView)
        val descView = findViewById<TextView>(R.id.descriptionView)
        val addToCart = findViewById<Button>(R.id.addToCart)
        val db = UserDatabase(this)
        val token = db.getToken()

        val image = intent.getStringExtra("image")
        val name = intent.getStringExtra("name")
        val price = intent.getStringExtra("price")
        val description = intent.getStringExtra("desc")
        val id = intent.getIntExtra("id",0)
        val qty = intent.getStringExtra("quantity")

        Glide.with(this).load("https://yourzaj.xyz/$image").into(imageView)
        nameView.text = name
        priceView.text = price
        descView.text = description


        val progressbar = ProgressBar()
        val alerts = RequestAlerts(this)
        addToCart.setOnClickListener {

            val addToCartAlert = AlertDialog.Builder(this)
            val addTOCartAlertView = LayoutInflater.from(this).inflate(R.layout.quantity_selector, null)
            addToCartAlert.setView(addTOCartAlertView)
            addToCartAlert.show()

            val decrease = addTOCartAlertView.findViewById<Button>(R.id.decrease)
            val increase = addTOCartAlertView.findViewById<Button>(R.id.increase)
            val quantityText = addTOCartAlertView.findViewById<TextView>(R.id.quantityText)
            val confirm = addTOCartAlertView.findViewById<Button>(R.id.confirm)
            var quantity = 1
            quantityText.text = quantity.toString()

            decrease.setOnClickListener {
                if(quantity != 1){
                    quantity--
                    quantityText.text = quantity.toString()
                }
            }

            increase.setOnClickListener {
                if(quantity < qty!!.toInt()){
                    quantity++
                    quantityText.text = quantity.toString()
                }
            }

            confirm.setOnClickListener {
                val progress = progressbar.showProgressBar(this,R.layout.loading,"Please Wait...", R.id.progressText)
                val jsonObject = JSONObject()
                jsonObject.put("product_id", id)
                jsonObject.put("quantity", quantity)

                val request = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())

                CoroutineScope(Dispatchers.IO).launch {
                    val addToCartResponse = try{ RetrofitInstance.retro.addToCart("Bearer $token", request) }
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
                        if(addToCartResponse.code() == 200 && addToCartResponse.headers().contains(Pair("content-type","application/json"))){
                            AlertDialog.Builder(this@ProductViewer)
                                .setTitle("Success")
                                .setMessage("Product has been successfully added to cart.")
                                .setPositiveButton("OK", null)
                                .show()
                        }else{
                            AlertDialog.Builder(this@ProductViewer)
                                .setTitle("Error")
                                .setMessage("Something went wrong.")
                                .setPositiveButton("OK", null)
                                .show()
                        }
                    }
                }
            }
        }
    }
}