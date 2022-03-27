package com.example.adaptertest2

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity

class Search (private val context: Context){

    fun goToSearchProducts(keyword: String){
        val intent = Intent(context, SearchedProducts::class.java)
        intent.putExtra("keyword", keyword)
        startActivity(context,intent, null)
    }
}