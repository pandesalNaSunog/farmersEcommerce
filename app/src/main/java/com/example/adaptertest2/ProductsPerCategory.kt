package com.example.adaptertest2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ProductsPerCategory : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products_per_category)

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

        val title = intent.getStringExtra("title")
        val titleText = findViewById<TextView>(R.id.textView7)
        val bundle = intent.extras
        val productList = bundle?.getSerializable("productList") as List<StoreMasterProductItem>
        val productRecycler = findViewById<RecyclerView>(R.id.productRecycler)
        val productAdapter = StoreMasterProductAdapter(mutableListOf())
        productRecycler.adapter = productAdapter
        productRecycler.layoutManager = GridLayoutManager(this,2)
        titleText.text = title

        for(i in productList.indices){
            productAdapter.addItem(productList[i])
        }
    }
}