package com.example.adaptertest2

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface Requests {

    @POST("/api/request-verification-code")
    suspend fun requestVerificationCode(@Body request: RequestBody): Response<ResponseBody>

    @POST("/api/products")
    suspend fun addProduct(@Header("Authorization") token: String, @Body request: RequestBody): Response<ResponseBody>

    @POST("/api/login")
    suspend fun login(@Body request: RequestBody): Response<ResponseBody>

    @POST("/api/submit-verification-code")
    suspend fun submitVerificationCode(@Body request: RequestBody): Response<ResponseBody>

    @POST("/api/register")
    suspend fun register(@Body request: RequestBody): Response<ResponseBody>

    @GET("/api/products")
    suspend fun getProducts(): ProductsX
}