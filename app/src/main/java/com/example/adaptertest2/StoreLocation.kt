package com.example.adaptertest2

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
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

class StoreLocation : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store_location)

        val name = intent.getStringExtra("name")
        val storeName = intent.getStringExtra("storeName")
        val email = intent.getStringExtra("email")
        val password = intent.getStringExtra("password")
        val type = intent.getStringExtra("type")
        val coopId = intent.getStringExtra("coopId")
        val contact = intent.getStringExtra("contact")

        val client = LocationServices.getFusedLocationProviderClient(this)

        val generate = findViewById<Button>(R.id.generate)

        generate.setOnClickListener {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    val task = client.lastLocation
                    task.addOnSuccessListener {
                        val latitude = it.latitude
                        val longitude = it.longitude

                        val submissionAlert = AlertDialog.Builder(this)
                        val subMissionAlertView = LayoutInflater.from(this).inflate(R.layout.submit_location_coordinates,null)
                        submissionAlert.setView(subMissionAlertView)

                        val submit = subMissionAlertView.findViewById<Button>(R.id.submit)
                        val lat = subMissionAlertView.findViewById<TextView>(R.id.lat)
                        val lng = subMissionAlertView.findViewById<TextView>(R.id.lng)

                        lat.text = "Lat: $latitude"
                        lng.text = "Lng: $longitude"

                        submit.setOnClickListener {
                            val jsonObject = JSONObject()
                            jsonObject.put("name", name)
                            jsonObject.put("store_name", storeName)
                            jsonObject.put("email", email)
                            jsonObject.put("password", password)
                            jsonObject.put("type", type)
                            jsonObject.put("farmers_cooperative_id", coopId)
                            jsonObject.put("phone", contact)
                            jsonObject.put("coordinates", "$latitude,$longitude")

                            val request = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())

                            val progressBar = ProgressBar()
                            val progress = progressBar.showProgressBar(this,R.layout.loading,"Submitting...", R.id.progressText)
                            val alerts = RequestAlerts(this)
                            CoroutineScope(Dispatchers.IO).launch {
                                val registerResponse = try{ RetrofitInstance.retro.register(request) }
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
                                    if(registerResponse.isSuccessful){
                                        try{
                                            val gson = GsonBuilder().setPrettyPrinting().create()
                                            val json = gson.toJson(JsonParser.parseString(registerResponse.body()?.string()))

                                            Log.e("StoreLocation", json)

                                            val intent = Intent(this@StoreLocation, VerificationCode::class.java)
                                            intent.putExtra("type", type)
                                            intent.putExtra("contact", contact)
                                            startActivity(intent)
                                            finish()
                                        }catch(e: Exception){
                                            Log.e("Store", e.toString())
                                        }

                                    }else{
                                        Log.e("Error", registerResponse.errorBody().toString())
                                    }
                                }
                            }
                        }
                        submissionAlert.show()
                    }
                }else{
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)
                }
            }
        }

    }
}