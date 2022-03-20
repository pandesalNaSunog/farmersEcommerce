package com.example.adaptertest2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.TextureView
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.internal.http2.Http2Reader
import org.json.JSONObject
import java.net.SocketTimeoutException
import kotlin.system.exitProcess

class VerificationCode : AppCompatActivity() {
    private lateinit var countDown: CountDownTimer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification_code)

        val resendLayout = findViewById<LinearLayout>(R.id.resendLayout)
        resendLayout.isVisible = false
        val resend = findViewById<Button>(R.id.resend)
        val contact = intent.getStringExtra("contact")
        val type = "SMS"
        val expiration = findViewById<TextView>(R.id.expiration)
        val backspace = findViewById<Button>(R.id.backspace)
        val code = findViewById<TextView>(R.id.code)
        val two = findViewById<Button>(R.id.two)
        val three = findViewById<Button>(R.id.three)
        val four = findViewById<Button>(R.id.four)
        val five = findViewById<Button>(R.id.five)
        val six = findViewById<Button>(R.id.six)
        val seven = findViewById<Button>(R.id.seven)
        val eight = findViewById<Button>(R.id.eight)
        val nine = findViewById<Button>(R.id.nine)
        val one = findViewById<Button>(R.id.one)
        val zero = findViewById<Button>(R.id.zero)
        var attemptLimit = 2
        var attempts = 1

        var codeAttemptLimit = 2
        var codeAttempts = 1

        backspace.setOnClickListener {
            if(code.text.toString().isNotEmpty()) {
                code.text = code.text.toString().dropLast(1)
            }
        }

        one.setOnClickListener {
            inputCode(code, "1", type!!, contact!!, codeAttemptLimit, codeAttempts)
        }
        two.setOnClickListener {
            inputCode(code, "2", type!!, contact!!, codeAttemptLimit, codeAttempts)
        }
        three.setOnClickListener {
            inputCode(code, "3", type!!, contact!!, codeAttemptLimit, codeAttempts)
        }
        four.setOnClickListener {
            inputCode(code, "4", type!!, contact!!, codeAttemptLimit, codeAttempts)
        }
        five.setOnClickListener {
            inputCode(code, "5", type!!, contact!!, codeAttemptLimit, codeAttempts)
        }
        six.setOnClickListener {
            inputCode(code, "6", type!!, contact!!, codeAttemptLimit, codeAttempts)
        }
        seven.setOnClickListener {
            inputCode(code, "7", type!!, contact!!, codeAttemptLimit, codeAttempts)
        }
        eight.setOnClickListener {
            inputCode(code, "8", type!!, contact!!, codeAttemptLimit, codeAttempts)
        }
        nine.setOnClickListener {
            inputCode(code, "9", type!!, contact!!, codeAttemptLimit, codeAttempts)
        }
        zero.setOnClickListener {
            inputCode(code, "0", type!!, contact!!, codeAttemptLimit, codeAttempts)
        }

        countDown = object: CountDownTimer(200000, 1000){
            override fun onTick(millisUntilFinished: Long) {
                val second = millisUntilFinished / 1000
                if(second.toInt() != 1)
                {
                    expiration.text = "Expires in $second seconds"
                }else{
                    expiration.text = "Expires in $second second"
                }
                if(second <= 185){
                    resendLayout.isVisible = true
                }

                resend.setOnClickListener {
                    val progressBar = ProgressBar()
                    val progress = progressBar.showProgressBar(this@VerificationCode, R.layout.loading, "Please Wait...", R.id.progressText)
                    val alerts = RequestAlerts(this@VerificationCode)

                    val jsonObject = JSONObject()
                    jsonObject.put("type", type)
                    jsonObject.put("contact", contact)

                    val request = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())

                    CoroutineScope(Dispatchers.IO).launch {
                        val requestVerif = try{ RetrofitInstance.retro.requestVerificationCode(request) }
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
                            if(requestVerif.code() == 200 && requestVerif.headers().contains(Pair("content-type","application/json"))){
                                countDown.cancel()
                                countDown.start()
                            }else{
                                AlertDialog.Builder(this@VerificationCode)
                                    .setTitle("Error")
                                    .setMessage("Please try again.")
                                    .setPositiveButton("OK", null)
                                    .show()
                            }
                        }
                    }
                }
            }

            override fun onFinish() {
                attempts++
                if(attempts <= attemptLimit){
                    countDown.start()
                }else{
                    finishAffinity()
                }
            }

        }.start()
    }

    private fun inputCode(code: TextView, value: String, type: String, contact: String, limit: Int, attempts: Int){
        val alerts = RequestAlerts(this)
        val progressbar = ProgressBar()

        if(code.text.toString().length != 3){
            code.text = "${code.text}$value"
        }else{
            code.text = "${code.text}$value"

            val jsonObject = JSONObject()
            jsonObject.put("type", type)
            jsonObject.put("contact", contact)
            jsonObject.put("code", code.text.toString())

            val request = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
            val progress = progressbar.showProgressBar(this, R.layout.loading, "Please wait...", R.id.progressText)

            CoroutineScope(Dispatchers.IO).launch {
                val codeResponse = try{ RetrofitInstance.retro.submitVerificationCode(request) }
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
                    if(codeResponse.code() == 200 && codeResponse.headers().contains(Pair("content-type","application/json"))){
                        AlertDialog.Builder(this@VerificationCode)
                            .setTitle("Verification Success")
                            .setMessage("Press OK to return to log in form.")
                            .setCancelable(false)
                            .setPositiveButton("OK"){_,_->
                                val intent = Intent(this@VerificationCode, MainActivity::class.java)
                                startActivity(intent)
                                finishAffinity()
                            }
                            .show()
                        Log.e("Hey", codeResponse.headers().toString())
                    }else{

                            finishAffinity()
                    }
                }
            }
        }
    }
}