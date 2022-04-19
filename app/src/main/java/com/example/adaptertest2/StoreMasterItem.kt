package com.example.adaptertest2

import java.io.Serializable

data class StoreMasterItem(
    val Dairy: List<StoreMasterProductItem>,
    val Fish: List<StoreMasterProductItem>,
    val Fruit: List<StoreMasterProductItem>,
    val Meat: List<StoreMasterProductItem>,
    val Plant: List<StoreMasterProductItem>,
    val Poultry: List<StoreMasterProductItem>,
    val Seeds: List<StoreMasterProductItem>,
    val Vegetable: List<StoreMasterProductItem>,
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
    val updated_at: String,
    var can_be_follow: Boolean
)