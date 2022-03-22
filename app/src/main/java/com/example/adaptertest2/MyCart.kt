package com.example.adaptertest2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException

class MyCart : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_cart)
        val db = UserDatabase(this)
        val token = db.getToken()
        val cartRecycler = findViewById<RecyclerView>(R.id.cartRecycler)
        val cartAdapter = CartAdapter(mutableListOf())
        cartRecycler.adapter = cartAdapter
        cartRecycler.layoutManager = LinearLayoutManager(this)

        val progressBar = ProgressBar()
        val progress = progressBar.showProgressBar(this,R.layout.loading,"Loading...",R.id.progressText)
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
                for(i in cart.indices){
                    cartAdapter.addItem(cart[i])
                }
            }
        }
    }
}