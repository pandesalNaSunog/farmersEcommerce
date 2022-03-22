package com.example.adaptertest2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import okhttp3.internal.http.RetryAndFollowUpInterceptor
import java.net.SocketTimeoutException

class MyCart : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_cart)

        val cleartCart = findViewById<Button>(R.id.clearCart)
        val db = UserDatabase(this)
        val token = db.getToken()
        val cartRecycler = findViewById<RecyclerView>(R.id.cartRecycler)
        val cartAdapter = CartAdapter(mutableListOf())
        cartRecycler.adapter = cartAdapter
        cartRecycler.layoutManager = LinearLayoutManager(this)

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
                for(i in cart.indices){
                    cartAdapter.addItem(cart[i])
                }
                cleartCart.isVisible = cart.size != 0
            }
        }

        cleartCart.setOnClickListener {

            AlertDialog.Builder(this)
                .setTitle("Clear Cart")
                .setMessage("Are your sure you want to clear your cart?")
                .setPositiveButton("YES"){_,_->
                    progress = progressBar.showProgressBar(this,R.layout.loading,"Clearing...", R.id.progressText)
                    CoroutineScope(Dispatchers.IO).launch {
                        val clearCartResponse = try{ RetrofitInstance.retro.clearCart("Bearer $token") }
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
                            if(clearCartResponse.code() == 200 && clearCartResponse.headers().contains(Pair("content-type","application/json"))){
                                AlertDialog.Builder(this@MyCart)
                                    .setTitle("SUccess")
                                    .setMessage("Your cart is now empty.")
                                    .setPositiveButton("Ok", null)
                                    .show()

                                cartAdapter.deleteAll()
                            }else{
                                alerts.somethingWentWrongAlert()
                            }
                        }
                    }
                }.setNegativeButton("NO", null)
                .show()
        }
    }
}