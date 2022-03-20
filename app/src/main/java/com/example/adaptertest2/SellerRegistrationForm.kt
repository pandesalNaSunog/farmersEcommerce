package com.example.adaptertest2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Retrofit

class SellerRegistrationForm : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seller_registration_form)

        val filloutError = "Please fillout this field"
        val emailError = "Please enter a valid email"
        val passwordMismatch = "Password mismatch"
        val storeName = findViewById<EditText>(R.id.storeName)
        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
        val confirmPassword = findViewById<EditText>(R.id.confirmPassword)
        val coopId = findViewById<EditText>(R.id.coopId)
        val contact = findViewById<EditText>(R.id.contact)
        val signup = findViewById<Button>(R.id.signup)
        val type = intent.getSerializableExtra("type")
        val name = findViewById<EditText>(R.id.name)
        signup.setOnClickListener {
            if(storeName.text.isEmpty()){
                storeName.error = filloutError
            }else if(email.text.isEmpty()){
                email.error = filloutError
            }else if(!Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()){
                email.error = emailError
            }else if(password.text.isEmpty()){
                password.error = filloutError
            }else if(confirmPassword.text.isEmpty()){
                confirmPassword.error = filloutError
            }else if(password.text.toString() != confirmPassword.text.toString()){
                password.error = passwordMismatch
            }else if(coopId.text.isEmpty()){
                coopId.error = filloutError
            }else if(contact.text.isEmpty()){
                contact.error = filloutError
            }else if(name.text.isEmpty()){
                name.error = filloutError
            }else{
                val intent = Intent(this, StoreLocation::class.java)
                intent.putExtra("storeName", storeName.text.toString())
                intent.putExtra("email", email.text.toString())
                intent.putExtra("password", password.text.toString())
                intent.putExtra("type", type)
                intent.putExtra("coopId", coopId.text.toString())
                intent.putExtra("contact", contact.text.toString())
                intent.putExtra("name", name.text.toString())
                startActivity(intent)
                finish()
            }
        }
    }
}