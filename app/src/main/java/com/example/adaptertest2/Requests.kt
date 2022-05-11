package com.example.adaptertest2

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

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
    suspend fun getStoreMaster(@Header("Authorization") token: String): StoreMaster

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

    @POST("/api/write-feedback")
    suspend fun writeFeedBack(@Header("Authorization") token: String, @Body request: RequestBody): Response<ResponseBody>

    @GET("/api/feedback-products")
    suspend fun getFeedBack(@Query("product_id") id: Int): FeedBackDetails

    @POST("/api/mark-order-as-completed")
    suspend fun markOrderAsCompleted(@Header("Authorization") token: String, @Body request: RequestBody): Response<ResponseBody>

    @GET("/api/sales")
    suspend fun getSales(@Header("Authorization") token: String): SalesDetails

    @GET("/api/search-products")
    suspend fun searchProducts(@Query("keyword") keyword: String): Product

    @POST("/api/update-quantity")
    suspend fun updateQuantity(@Header("Authorization") token: String, @Body request: RequestBody): Response<ResponseBody>

    @GET("/api/my-store-orders")
    suspend fun getMyStoreOrders(@Header("Authorization") token: String): StoreOrders

    @POST("/api/follow/{store}")
    suspend fun followStore(@Header("Authorization") token: String, @Path("store") store: Int): Response<ResponseBody>

    @POST("/api/unfollow/{store}")
    suspend fun unfollowStore(@Header("Authorization") token: String, @Path("store") store: Int): Response<ResponseBody>

    @GET("/api/notifications")
    suspend fun getNotifications(@Header("Authorization") token: String): Notification

    @POST("/api/update-price")
    suspend fun updatePrice(@Header("Authorization") token: String, @Body request: RequestBody): Response<ResponseBody>

    @POST("/api/forgot-password")
    suspend fun forgotPassword(@Body request: RequestBody): Response<ResponseBody>

    @POST("/api/send-email-verification-link")
    suspend fun emailVerification(@Header("Authorization") token: String): Response<ResponseBody>

    @POST("/api/get-report")
    suspend fun getReport(@Header("Authorization") token: String, @Body request: RequestBody): SalesReport
}