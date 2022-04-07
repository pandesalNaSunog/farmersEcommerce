package com.example.adaptertest2

data class StoreOrdersItem(
    val address: String,
    val created_at: String,
    val id: Int,
    val itemsBreakdown: String,
    val pop: List<Pop>,
    val remarks: String,
    val status: String,
    val subTotal: String,
    val total: String,
    val updated_at: String,
    val user_id: Int,
    val vatDue: String,
    val vatRate: String
)