package com.example.adaptertest2

data class NotificationItem(
    val created_at: String,
    val data: Data,
    val id: String,
    val notifiable_id: Int,
    val notifiable_type: String,
    val read_at: String,
    val type: String,
    val updated_at: String
)