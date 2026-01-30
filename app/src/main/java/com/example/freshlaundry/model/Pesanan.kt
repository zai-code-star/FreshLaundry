package com.example.freshlaundry.model

data class Pesanan(
    val orderId: String = "",
    val uid: String = "",
    val nama: String = "",
    val layanan: String = "",
    val lokasi: String = "",
    val jam: String = "",
    val catatan: String = "",
    val tanggal: String = "",
    val status: String = "",
    val berat: String = "",
    val biaya: String = "",
    var key: String? = null

)
