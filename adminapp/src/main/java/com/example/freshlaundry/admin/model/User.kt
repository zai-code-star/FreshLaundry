package com.example.freshlaundry.admin.model

data class User(
    val uid: String = "",
    val email: String = "",
    var lastChat: String = "" // ini bisa nullable juga kalau mau aman
)
