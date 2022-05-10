package com.example.adaptertest2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.net.SocketTimeoutException

class MyCart : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_cart)


        val searchText = findViewById<EditText>(R.id.editText)
        val searchButton = findViewById<Button>(R.id.button)

        searchButton.setOnClickListener {
            if(searchText.text.isEmpty()){
                searchText.error = "Please fill out this field"
            }else{
                val search = Search(this)
                search.goToSearchProducts(searchText.text.toString())
            }
        }

        val checkout = findViewById<Button>(R.id.checkout)
        val shopNow = findViewById<Button>(R.id.shopNow)
        val cartIsEmpty = findViewById<LinearLayout>(R.id.cartIsEmpty)
        val cleartCart = findViewById<Button>(R.id.clearCart)
        val buttonGroup = findViewById<LinearLayout>(R.id.buttonGroup)
        val db = UserDatabase(this)
        val user = db.getAll()
        val token = db.getToken()
        val cartRecycler = findViewById<RecyclerView>(R.id.cartRecycler)
        val cartAdapter = CartAdapter(mutableListOf(),buttonGroup, cartIsEmpty)
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
                buttonGroup.isVisible = cart.size != 0
                cartIsEmpty.isVisible = cart.size == 0
            }
        }

        checkout.setOnClickListener {
            val checkoutBottomSheet = BottomSheetDialog(this)
            val checkoutBottomSheetView = LayoutInflater.from(this).inflate(R.layout.checkout, null)
            checkoutBottomSheet.setContentView(checkoutBottomSheetView)
            checkoutBottomSheet.show()

            val address = checkoutBottomSheetView.findViewById<EditText>(R.id.address)
            val remarks = checkoutBottomSheetView.findViewById<EditText>(R.id.remarks)
            val confirm = checkoutBottomSheetView.findViewById<Button>(R.id.confirm)

            address.setText(user?.address)

            confirm.setOnClickListener {

                if(address.text.isEmpty()){
                    address.error = "Please specify your shipping address"
                }else{
                    val bar = ProgressBar()
                    val prog = bar.showProgressBar(this,R.layout.loading, "Please Wait...", R.id.progressText)
                    val alert = RequestAlerts(this)

                    val jsonObject = JSONObject()
                    jsonObject.put("address", address.text.toString())
                    var remarksText = ""
                    if(remarks.text.isNotEmpty()){
                        remarksText = remarks.text.toString()
                    }
                    jsonObject.put("remarks", remarksText)

                    val request = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
                    CoroutineScope(Dispatchers.IO).launch {
                        val checkoutResponse = try{ RetrofitInstance.retro.checkOut("Bearer $token", request) }
                        catch(e: SocketTimeoutException){
                            withContext(Dispatchers.Main){
                                prog.dismiss()
                                alert.showSocketTimeOutAlert()
                            }
                            return@launch
                        }catch(e: Exception){
                            withContext(Dispatchers.Main){
                                prog.dismiss()
                                alert.noInternetAlert()
                            }
                            return@launch
                        }

                        withContext(Dispatchers.Main){
                            prog.dismiss()
                           if(checkoutResponse.code() == 200 && checkoutResponse.headers().contains(Pair("content-type","application/json"))){

                                AlertDialog.Builder(this@MyCart)
                                    .setTitle("Success")
                                    .setMessage("Your order has been submitted.")
                                    .setPositiveButton("OK", null)
                                    .show()
                                checkoutBottomSheet.dismiss()
                                cartAdapter.deleteAll()
                            }else{
                                alert.somethingWentWrongAlert()
                                Log.e("MyCart",checkoutResponse.errorBody()!!.string())
                            }
                        }
                    }
                }
            }
        }

        shopNow.setOnClickListener {
            val intent = Intent(this,Navigation::class.java)
            startActivity(intent)
            finishAffinity()
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