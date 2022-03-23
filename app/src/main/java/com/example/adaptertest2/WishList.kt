package com.example.adaptertest2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class WishList : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wish_list)

        val wishRecycler = findViewById<RecyclerView>(R.id.wishRecycler)
        val wishAdapter = WishListAdapter(mutableListOf())
        wishRecycler.adapter = wishAdapter
        wishRecycler.layoutManager = LinearLayoutManager(this)
    }
}