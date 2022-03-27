package com.example.adaptertest2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class Navigation : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)
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

        val nav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val navController = findNavController(R.id.fragmentContainerView)

        nav.setupWithNavController(navController)
    }
}