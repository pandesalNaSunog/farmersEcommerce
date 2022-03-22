package com.example.adaptertest2

import android.content.Context
import androidx.appcompat.app.AlertDialog
import java.io.File

class RequestAlerts (private val context: Context){

    fun showSocketTimeOutAlert(){
        AlertDialog.Builder(context)
            .setTitle("Error")
            .setMessage("Connection time out.")
            .setPositiveButton("OK", null)
            .show()
    }
    fun noInternetAlert(){
        AlertDialog.Builder(context)
            .setTitle("Error")
            .setMessage("No Internet Connection")
            .setPositiveButton("OK", null)
            .show()
    }
    fun somethingWentWrongAlert(){
        AlertDialog.Builder(context)
            .setTitle("Error")
            .setMessage("Something went wrong.")
            .setPositiveButton("OK", null)
            .show()
    }
}