package com.example.freshlaundry.admin.model

data class Chat(
    val senderId: String = "",
    val receiverId: String = "",
    val message: String = "",
    val timestamp: Long = 0
)
