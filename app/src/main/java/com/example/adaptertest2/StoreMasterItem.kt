package com.example.adaptertest2

data class StoreMasterItem(
    val Dairy: List<ProductItem>,
    val Fish: List<ProductItem>,
    val Fruit: List<ProductItem>,
    val Meat: List<ProductItem>,
    val Plant: List<ProductItem>,
    val Poultry: List<ProductItem>,
    val Seeds: List<ProductItem>,
    val Vegetable: List<ProductItem>,
    val address: String,
    val approved_as_store_owner_at: String,
    val coordinates: String,
    val created_at: String,
    val email: String,
    val email_verified_at: String,
    val farmers_cooperative_id: String,
    val id: Int,
    val name: String,
    val phone: String,
    val phone_verified_at: String,
    val store_name: String,
    val type: String,
    val updated_at: String
)