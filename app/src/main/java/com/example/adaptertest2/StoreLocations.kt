package com.example.adaptertest2

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.adaptertest2.databinding.ActivityStoreLocationsBinding
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.Marker
import kotlinx.coroutines.*
import java.net.SocketTimeoutException
import kotlin.random.Random

class StoreLocations : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityStoreLocationsBinding
    private var open = true
    private var marker: Marker? = null
    private lateinit var initCurrent: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStoreLocationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


        val db = UserDatabase(this)
        val token = db.getToken()
        val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val client = LocationServices.getFusedLocationProviderClient(this)


        if(gpsStatus && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val firstTask = client.lastLocation

            firstTask.addOnSuccessListener {
                try {
                    mMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                it.latitude,
                                it.longitude
                            ), 10F
                        )
                    )
                }catch(e: Exception){
                    AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("Something went wrong. Please check if your phone's location services is turned on.")
                        .setPositiveButton("OK", null)
                        .show()
                }
            }
            CoroutineScope(Dispatchers.IO).launch {
                    while (open) {
                        withContext(Dispatchers.Main) {
                            marker?.remove()
                            val task = client.lastLocation
                            task.addOnSuccessListener {
                                initCurrent = LatLng(it.latitude, it.longitude)
                                marker = mMap.addMarker(
                                    MarkerOptions().position(
                                        LatLng(
                                            it.latitude,
                                            it.longitude
                                        )
                                    ).title("My Current Location")
                                )!!
                                Log.e("Location", LatLng(it.latitude, it.longitude).toString())
                            }
                        }
                        delay(1000)
                    }
            }
            val progressBar = ProgressBar()
            val progress = progressBar.showProgressBar(this,R.layout.loading,"Loading...", R.id.progressText)
            val alerts = RequestAlerts(this)

            var marker: Marker? = null

            CoroutineScope(Dispatchers.IO).launch {
                val storeMaster = try{ RetrofitInstance.retro.getStoreMaster("Bearer $token") }
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
                    for(i in storeMaster.indices){
                        val coordinates = storeMaster[i].coordinates.split(",")
                        val lat = coordinates[0].toDouble()
                        val lng = coordinates[1].toDouble()

                        val storeLocation = LatLng(lat,lng)
                        mMap.addMarker(MarkerOptions().position(storeLocation).title(storeMaster[i].store_name).snippet(storeMaster[i].farmers_cooperative_id.split("|")[1]))
                    }
                }
            }
        }else{
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION), 1)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        open = false
    }
}