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
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import org.json.JSONTokener
import retrofit2.HttpException
import java.net.SocketTimeoutException

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = UserDatabase(this)
        if(db.getSize() > 0){
            startActivity(Intent(this, Navigation::class.java))
            finishAffinity()
        }

        val fillOutError = "Please fill out this field"
        val login = findViewById<Button>(R.id.login)
        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
        val signup = findViewById<Button>(R.id.signup)




        login.setOnClickListener{
            if(email.text.isEmpty()){
                email.error = fillOutError
            }else if(!Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()){
                email.error = "Please enter a valid email address."
            }else if(password.text.isEmpty()){
                password.error = fillOutError
            }else{
                val jsonObject = JSONObject()
                jsonObject.put("email", email.text.toString())
                jsonObject.put("password", password.text.toString())

                val request = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())

                val progressBar = ProgressBar()
                val progress = progressBar.showProgressBar(this,R.layout.loading,"Logging in",R.id.progressText)
                CoroutineScope(Dispatchers.IO).launch {
                    val loginResponse = try{ RetrofitInstance.retro.login(request) }
                    catch(e: HttpException){
                        withContext(Dispatchers.Main){
                            progress.dismiss()
                            AlertDialog.Builder(this@MainActivity)
                                .setTitle("Error")
                                .setMessage("Account not found.")
                                .setPositiveButton("OK", null)
                                .show()
                        }
                        return@launch
                    }catch(e: SocketTimeoutException){
                        withContext(Dispatchers.Main){
                            progress.dismiss()
                            AlertDialog.Builder(this@MainActivity)
                                .setTitle("Error")
                                .setMessage("Connection Time Out")
                                .setPositiveButton("OK", null)
                                .show()
                        }
                        return@launch
                    }catch(e: Exception){
                        withContext(Dispatchers.Main){
                            progress.dismiss()
                            AlertDialog.Builder(this@MainActivity)
                                .setTitle("Error")
                                .setMessage("No Internet Connection")
                                .setPositiveButton("OK", null)
                                .show()
                            Log.e("main", e.toString())
                        }
                        return@launch
                    }
                    withContext(Dispatchers.Main){
                        progress.dismiss()
                        if(loginResponse.code() == 200 && loginResponse.headers().contains(Pair("content-type","application/json"))){

                            val gson = GsonBuilder().setPrettyPrinting().create()
                            val json = gson.toJson(JsonParser.parseString(loginResponse.body()?.string()))
                            val loginResponseObject = JSONTokener(json).nextValue() as JSONObject


                            val token = loginResponseObject.getString("token")
                            val user = loginResponseObject.getJSONObject("user")


                            val name: String? = user.getString("name")
                            val type: String? = user.getString("type")
                            val storeName = try{
                                user.getString("store_name")
                            }catch(e: Exception){
                                ""
                            }
                            val emailData: String? = user.getString("email")
                            val phoneData: String? = user.getString("phone")
                            val address = try{
                                user.getString("address")
                            }catch(e: Exception){
                                ""
                            }
                            val farmerId = try{
                                user.getString("farmers_cooperative_id")
                            }catch(e: Exception){
                                ""
                            }
                            val coordinates = try{
                                user.getString("coordinates")
                            }catch(e: Exception){
                                ""
                            }

                            val approvedAsSellerAt = try{
                                user.getString("approved_as_store_owner_at")
                            }catch(e: Exception){
                                ""
                            }
                            Log.e("MainActivity", approvedAsSellerAt)
                            val id: Int = user.getInt("id")

                            val userData = UserXX(address,approvedAsSellerAt,coordinates,null,emailData,null,farmerId, id,null, name,phoneData,null,storeName,type,null)
                            db.addItem(userData, token)
                            val intent = Intent(this@MainActivity, Navigation::class.java)
                            startActivity(intent)
                            finishAffinity()
                        }else{
                            AlertDialog.Builder(this@MainActivity)
                                .setTitle("Error")
                                .setMessage("Account not found.")
                                .setPositiveButton("OK", null)
                                .show()
                        }
                    }
                }
            }
        }

        signup.setOnClickListener{
            val intent = Intent(this,SelectUserType::class.java)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}