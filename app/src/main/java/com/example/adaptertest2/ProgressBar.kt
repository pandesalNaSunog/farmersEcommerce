package com.example.adaptertest2

import android.app.ProgressDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView

class ProgressBar {

    fun showProgressBar(context: Context, resource: Int, text: String, textViewId: Int): ProgressDialog{
        val progress = ProgressDialog(context)
        val progressView = LayoutInflater.from(context).inflate(resource, null)
        val loadingText = progressView.findViewById<TextView>(textViewId)
        progress.show()
        progress.setContentView(progressView)
        loadingText.text = text
        progress.setCancelable(false)
        progress.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return progress
    }
}