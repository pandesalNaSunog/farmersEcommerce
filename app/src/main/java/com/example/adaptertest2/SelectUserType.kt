package com.example.adaptertest2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class SelectUserType : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_user_type)

        val consumer = findViewById<Button>(R.id.consumer)
        val seller = findViewById<Button>(R.id.seller)
        val login = findViewById<Button>(R.id.login)

        consumer.setOnClickListener{
            setUserType("consumer")
        }
        seller.setOnClickListener{
            setUserType("seller")
        }

        login.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish( )
        }
    }

    private fun setUserType(type: String){
        if(type == "seller"){
            val intent = Intent(this, SellerRegistrationForm::class.java)
            intent.putExtra("type", type)
            startActivity(intent)
        }else{
            val intent = Intent(this, ConsumerRegistrationForm::class.java)
            intent.putExtra("type", type)
            startActivity(intent)
        }


    }
}