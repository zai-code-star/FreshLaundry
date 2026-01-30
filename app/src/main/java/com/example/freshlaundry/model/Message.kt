package com.example.freshlaundry.model

data class Message(
    val senderId: String? = null,
    val receiverId: String? = null,
    val message: String? = null,
    val timestamp: Long = 0L
)