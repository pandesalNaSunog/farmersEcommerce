package com.example.adaptertest2

import java.io.Serializable

data class ProductItemX(
    val category: String?,
    val created_at: String?,
    val description: String?,
    val id: Int?,
    val image: String?,
    val name: String?,
    val price: String?,
    val quantity: String?,
    val store_id: Int?,
    val store_owner: StoreOwnerX?,
    val updated_at: String?
): Serializable