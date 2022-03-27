package com.example.adaptertest2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException

class SearchedProducts : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_searched_products)


        val searchKeyword = findViewById<TextView>(R.id.searchKeyword)
        val keyword = intent.getStringExtra("keyword")

        searchKeyword.text = "Search Keyword: \"$keyword\""

        val productRecycler = findViewById<RecyclerView>(R.id.productRecycler)
        val productAdapter = ProductAdapter(mutableListOf())
        productRecycler.adapter = productAdapter
        productRecycler.layoutManager = GridLayoutManager(this,2)

        val progressBar = ProgressBar()
        val progress = progressBar.showProgressBar(this,R.layout.loading,"Loading...", R.id.progressText)
        val alerts = RequestAlerts(this)

        CoroutineScope(Dispatchers.IO).launch {
            val searchedProducts = try{ RetrofitInstance.retro.searchProducts(keyword!!) }
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
                for(i in searchedProducts.indices){
                    productAdapter.addItem(searchedProducts[i])
                }
            }
        }
    }
}