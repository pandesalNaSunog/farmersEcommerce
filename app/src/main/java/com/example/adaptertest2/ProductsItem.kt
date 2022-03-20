package com.example.adaptertest2

data class ProductsItem(
    val category: String,
    val created_at: String,
    val description: String,
    val id: Int,
    val image: String,
    val name: String,
    val price: String,
    val quantity: String,
    val store_id: Int,
    val store_owner: StoreOwner,
    val updated_at: String
)