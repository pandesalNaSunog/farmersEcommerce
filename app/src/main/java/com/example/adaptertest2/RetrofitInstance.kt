package com.example.adaptertest2

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    val retro: Requests by lazy{
        Retrofit.Builder()
            .baseUrl("https://yourzaj.xyz")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Requests::class.java)
    }
}