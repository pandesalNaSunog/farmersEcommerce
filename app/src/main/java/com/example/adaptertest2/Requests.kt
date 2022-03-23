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

    @POST("/api/logout")
    suspend fun logout(@Header("Authorization") token: String): Response<ResponseBody>

    @GET("/api/store-master")
    suspend fun getStoreMaster(): StoreMaster

    @GET("/api/products")
    suspend fun getProducts(): Product

    @POST("/api/add-to-cart")
    suspend fun addToCart(@Header("Authorization") token: String, @Body request: RequestBody): Response<ResponseBody>

    @GET("/api/my-products")
    suspend fun getSellerProducts(@Header("Authorization") token: String): Product

    @POST("/api/remove-to-cart")
    suspend fun removeCartItem(@Header("Authorization") token: String, @Body request: RequestBody): Response<ResponseBody>

    @GET("/api/get-cart-items")
    suspend fun getCartItems(@Header("Authorization") token: String): Cart

    @POST("/api/clear-cart")
    suspend fun clearCart(@Header("Authorization") token: String): Response<ResponseBody>

    @POST("/api/add-to-wishlist")
    suspend fun addToWishList(@Header("Authorization") token: String, @Body request: RequestBody): Response<ResponseBody>

    @GET("/api/get-wishlist")
    suspend fun getWishList(@Header("Authorization") token: String): WishListItems

    @POST("/api/update-profile")
    suspend fun updateProfile(@Header("Authorization") token: String, @Body request: RequestBody): Response<ResponseBody>

    @GET("/api/my-profile")
    suspend fun getMyProfile(@Header("Authorization") token: String): Profile

    @POST("/api/clear-wishlist")
    suspend fun clearWishList(@Header("Authorization") token: String): Response<ResponseBody>

    @POST("/api/remove-to-wishlist")
    suspend fun removeToWishList(@Header("Authorization") token: String, @Body request: RequestBody): Response<ResponseBody>

    @GET("/api/orders")
    suspend fun getOrders(@Header("Authorization") token: String): Orders

    @POST("/api/checkout")
    suspend fun checkOut(@Header("Authorization") token: String, @Body request: RequestBody): Response<ResponseBody>
}