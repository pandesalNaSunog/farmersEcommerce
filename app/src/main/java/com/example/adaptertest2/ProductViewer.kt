package com.example.adaptertest2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
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

        val available = findViewById<TextView>(R.id.quantity)
        val imageView = findViewById<ImageView>(R.id.imageView)
        val nameView = findViewById<TextView>(R.id.nameView)
        val priceView = findViewById<TextView>(R.id.priceView)
        val descView = findViewById<TextView>(R.id.descriptionView)
        val addToCart = findViewById<Button>(R.id.addToCart)
        val db = UserDatabase(this)
        val token = db.getToken()
        val buttons = findViewById<LinearLayout>(R.id.linearLayout2)
        buttons.isVisible = false

        val addToWishList = findViewById<Button>(R.id.addToWishList)

        val image = intent.getStringExtra("image")
        val name = intent.getStringExtra("name")
        val price = intent.getStringExtra("price")
        val description = intent.getStringExtra("desc")
        val id = intent.getIntExtra("id",0)
        val qty = intent.getStringExtra("quantity")
        Log.e("id", id.toString())

        Glide.with(this).load("https://yourzaj.xyz/$image").into(imageView)
        nameView.text = name
        priceView.text = price
        descView.text = description
        available.text = "Available: $qty"

        var addToCartValue = "add"
        var wishListValue = "add"
        val progressBar = ProgressBar()
        var progress = progressBar.showProgressBar(this,R.layout.loading,"Loading...",R.id.progressText)
        val alerts = RequestAlerts(this)

        CoroutineScope(Dispatchers.IO).launch {
            val cart = try{ RetrofitInstance.retro.getCartItems("Bearer $token") }
            catch (e: SocketTimeoutException){
                withContext(Dispatchers.Main){
                    progress.dismiss()
                    alerts.showSocketTimeOutAlert()
                }
                return@launch
            }catch (e: Exception){
                withContext(Dispatchers.Main){
                    progress.dismiss()
                    alerts.noInternetAlert()
                }
                return@launch
            }
            withContext(Dispatchers.Main){
                progress.dismiss()
                buttons.isVisible = true
                for(i in cart.indices){
                    if(id == cart[i].product_id){
                        addToCartValue = "view"
                        addToCart.text = "view cart"
                        break
                    }
                }
            }
        }

        val progre = ProgressBar()
        var prog = progre.showProgressBar(this,R.layout.loading,"Loading...",R.id.progressText)
        val alert = RequestAlerts(this)

        CoroutineScope(Dispatchers.IO).launch {
            val wish = try{ RetrofitInstance.retro.getWishList("Bearer $token") }
            catch (e: SocketTimeoutException){
                withContext(Dispatchers.Main){
                    prog.dismiss()
                    alert.showSocketTimeOutAlert()
                }
                return@launch
            }catch (e: Exception){
                withContext(Dispatchers.Main){
                    prog.dismiss()
                    alert.noInternetAlert()
                }
                return@launch
            }
            withContext(Dispatchers.Main){
                prog.dismiss()
                buttons.isVisible = true
                for(i in wish.indices){
                    if(id == wish[i].product_id){
                        wishListValue = "view"
                        addToWishList.text = "view wishlist"
                        break
                    }
                }
            }
        }

        addToWishList.setOnClickListener {
            if(wishListValue == "add") {
                progress = progressBar.showProgressBar(
                    this,
                    R.layout.loading,
                    "Please Wait...",
                    R.id.progressText
                )
                val jsonObject = JSONObject()
                jsonObject.put("product_id", id)
                val request =
                    jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
                CoroutineScope(Dispatchers.IO).launch {
                    val wishListResponse = try {
                        RetrofitInstance.retro.addToWishList("Bearer $token", request)
                    } catch (e: SocketTimeoutException) {
                        withContext(Dispatchers.Main) {
                            progress.dismiss()
                            alerts.showSocketTimeOutAlert()
                        }
                        return@launch
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            progress.dismiss()
                            alerts.noInternetAlert()
                        }
                        return@launch
                    }

                    withContext(Dispatchers.Main) {
                        progress.dismiss()
                        if (wishListResponse.code() == 200 && wishListResponse.headers()
                                .contains(Pair("content-type", "application/json"))
                        ) {
                            AlertDialog.Builder(this@ProductViewer)
                                .setTitle("Success")
                                .setMessage("Product has been added to your wishlist.")
                                .setPositiveButton("OK", null)
                                .show()
                            wishListValue = "view"
                            addToWishList.text = "view wishlist"
                        } else {
                            alerts.somethingWentWrongAlert()
                            Log.e("viewer", wishListResponse.errorBody().toString())
                        }
                    }
                }
            }else{
                val intent = Intent(this, WishList::class.java)
                startActivity(intent)
                finish()
            }
        }
        addToCart.setOnClickListener {
            if(addToCartValue == "add") {

                val addToCartAlert = AlertDialog.Builder(this)
                val addTOCartAlertView =
                    LayoutInflater.from(this).inflate(R.layout.quantity_selector, null)
                addToCartAlert.setView(addTOCartAlertView)
                val alert = addToCartAlert.show()

                val decrease = addTOCartAlertView.findViewById<Button>(R.id.decrease)
                val increase = addTOCartAlertView.findViewById<Button>(R.id.increase)
                val quantityText = addTOCartAlertView.findViewById<TextView>(R.id.quantityText)
                val confirm = addTOCartAlertView.findViewById<Button>(R.id.confirm)
                var quantity = 1
                quantityText.text = quantity.toString()

                decrease.setOnClickListener {
                    if (quantity != 1) {
                        quantity--
                        quantityText.text = quantity.toString()
                    }
                }

                increase.setOnClickListener {
                    if (quantity < qty!!.toInt()) {
                        quantity++
                        quantityText.text = quantity.toString()
                    }
                }

                confirm.setOnClickListener {
                    progress = progressBar.showProgressBar(
                        this,
                        R.layout.loading,
                        "Please Wait...",
                        R.id.progressText
                    )
                    val jsonObject = JSONObject()
                    jsonObject.put("product_id", id)
                    jsonObject.put("quantity", quantity)

                    val request =
                        jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())

                    CoroutineScope(Dispatchers.IO).launch {
                        val addToCartResponse = try {
                            RetrofitInstance.retro.addToCart("Bearer $token", request)
                        } catch (e: SocketTimeoutException) {
                            withContext(Dispatchers.Main) {
                                progress.dismiss()
                                alerts.showSocketTimeOutAlert()
                            }
                            return@launch
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                progress.dismiss()
                                alerts.noInternetAlert()
                            }
                            return@launch
                        }

                        withContext(Dispatchers.Main) {
                            progress.dismiss()
                            if (addToCartResponse.code() == 200 && addToCartResponse.headers()
                                    .contains(Pair("content-type", "application/json"))
                            ) {
                                AlertDialog.Builder(this@ProductViewer)
                                    .setTitle("Success")
                                    .setMessage("Product has been successfully added to cart.")
                                    .setPositiveButton("OK", null)
                                    .show()
                                addToCartValue = "view"
                                addToCart.text = "view cart"
                                alert.dismiss()
                            } else {
                                AlertDialog.Builder(this@ProductViewer)
                                    .setTitle("Error")
                                    .setMessage("Something went wrong.")
                                    .setPositiveButton("OK", null)
                                    .show()
                            }
                        }
                    }
                }
            }else{
                val intent = Intent(this, MyCart::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}