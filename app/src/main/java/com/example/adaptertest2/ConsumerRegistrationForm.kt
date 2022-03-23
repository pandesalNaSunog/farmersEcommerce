package com.example.adaptertest2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.net.SocketTimeoutException
import kotlin.math.log

class ConsumerRegistrationForm : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consumer_registration_form)

        val filloutError = "Please fillout this field"
        val emailError = "Please enter a valid email"
        val passwordMismatch = "Password mismatch"
        val name = findViewById<EditText>(R.id.name)
        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
        val confirmPassword = findViewById<EditText>(R.id.confirmPassword)
        val address = findViewById<EditText>(R.id.address)
        val contact = findViewById<EditText>(R.id.contact)
        val signup = findViewById<Button>(R.id.signup)
        val type = intent.getStringExtra("type")
        signup.setOnClickListener {
            if(name.text.isEmpty()){
                name.error = filloutError
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
            }else if(address.text.isEmpty()){
                address.error = filloutError
            }else if(contact.text.isEmpty()){
                contact.error = filloutError
            }else{

                val progressBar = ProgressBar()
                val alerts = RequestAlerts(this)
                val progress = progressBar.showProgressBar(this,R.layout.loading,"Please Wait...", R.id.progressText)
                val jsonObject = JSONObject()
                jsonObject.put("name", name.text.toString())
                jsonObject.put("email", email.text.toString())
                jsonObject.put("password", password.text.toString())
                jsonObject.put("type", type)
                jsonObject.put("address", address.text.toString())
                jsonObject.put("phone", contact.text.toString())

                val request = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())

                Log.e("jakljaf", jsonObject.toString())
                CoroutineScope(Dispatchers.IO).launch {
                    val registrationResponse = try{ RetrofitInstance.retro.register(request) }
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
                        if(registrationResponse.code() == 200 && registrationResponse.headers().contains(Pair("content-type","application/json"))){
                            val gson = GsonBuilder().setPrettyPrinting().create()
                            val json = gson.toJson(JsonParser.parseString(registrationResponse.body()?.string()))
                            Log.e("reg", json)

                            val intent = Intent(this@ConsumerRegistrationForm, VerificationCode::class.java)
                            intent.putExtra("contact", contact.text.toString())
                            startActivity(intent)
                            finish()
                        }else{
                            AlertDialog.Builder(this@ConsumerRegistrationForm)
                                .setTitle("Error")
                                .setMessage("Something went wrong.")
                                .setPositiveButton("OK", null)
                                .show()
                        }
                    }
                }
            }
        }
    }
}