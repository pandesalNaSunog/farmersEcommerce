package com.example.adaptertest2

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.net.SocketTimeoutException

class ImageCapture : AppCompatActivity() {
    private var orderId = 0
    private var token = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_capture)

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        }else{
            val db = UserDatabase(this)
            token = db.getToken()
            orderId = intent.getIntExtra("order_id", 0)
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                if(it.resultCode == RESULT_OK){
                    val uri = it.data?.data
                    Log.e("ImageCapture", uri.toString())
                }
            }
            resultLauncher.launch(intent)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == RESULT_OK && requestCode == 100) {
            Log.e("ImageCapture", data?.data.toString())
        }

//        if(requestCode == 100 && resultCode == Activity.RESULT_OK) {
//
//
//            val uri = data?.data
//            Log.e("ImageCapture", uri.toString())
//            try {
//                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
//                val stream = ByteArrayOutputStream()
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream)
//                val bytes: ByteArray = stream.toByteArray()
//
//                val image = Base64.encodeToString(bytes, Base64.DEFAULT)
//
//                val progressBar = ProgressBar()
//                val progress = progressBar.showProgressBar(
//                    this,
//                    R.layout.loading,
//                    "Marking...",
//                    R.id.progressText
//                )
//                val alerts = RequestAlerts(this)
//
//                val jsonObject = JSONObject()
//                jsonObject.put("order_id", orderId)
//                jsonObject.put("image", image)
//
//                val request = jsonObject.toString()
//                    .toRequestBody("application/json".toMediaTypeOrNull())
//                CoroutineScope(Dispatchers.IO).launch {
//                    val markAsCompleteResponse = try {
//                        RetrofitInstance.retro.markOrderAsCompleted(
//                            "Bearer $token",
//                            request
//                        )
//                    } catch (e: SocketTimeoutException) {
//                        withContext(Dispatchers.Main) {
//                            progress.dismiss()
//                            alerts.showSocketTimeOutAlert()
//                        }
//                        return@launch
//                    } catch (e: Exception) {
//                        withContext(Dispatchers.Main) {
//                            progress.dismiss()
//                            alerts.noInternetAlert()
//                        }
//                        return@launch
//                    }
//
//                    withContext(Dispatchers.Main) {
//                        progress.dismiss()
//                        if (markAsCompleteResponse.code() == 200 && markAsCompleteResponse.headers()
//                                .contains(Pair("content-type", "application/json"))
//                        ) {
//                            Toast.makeText(
//                                this@ImageCapture,
//                                "Proof of transaction has been uploaded",
//                                Toast.LENGTH_LONG
//                            ).show()
//                            val intent = Intent(this@ImageCapture, Navigation::class.java)
//                            startActivity(intent)
//                            finishAffinity()
//                        } else {
//                            AlertDialog.Builder(this@ImageCapture)
//                                .setTitle("Error")
//                                .setMessage("Something went wrong.")
//                                .setPositiveButton("OK", null)
//                                .show()
//                        }
//                    }
//                }
//            } catch (e: Exception) {
//                Log.e("ImageCapture", e.toString())
//            }
//        }
    }
}