package com.example.adaptertest2

data class CartItem(
    val created_at: String,
    val id: Int,
    val product: ProductX,
    val product_id: Int,
    val quantity: Int,
    val updated_at: String,
    val user: UserX,
    val user_id: Int
)