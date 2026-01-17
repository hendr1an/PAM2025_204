package com.example.crabsupply.data.model

data class Order(
    val id: String = "",
    val buyerId: String = "",      // ID User pembeli
    val buyerName: String = "",    // Nama pembeli (biar Admin tahu)
    val buyerPhone: String = "",   // No HP (untuk konfirmasi WA)
    val productName: String = "",  // Barang yang dibeli
    val productSpecies: String = "", // Jenis (Bakau/Rajungan)
    val pricePerKg: Int = 0,       // Harga saat deal
    val quantity: Double = 0.0,         // Jumlah Kg
    val totalPrice: Int = 0,       // Total Bayar (Harga x Qty)
    val status: String = "pending",// Status: pending, diproses, selesai, tolak
    val address: String = "",      // Alamat kirim
    val timestamp: Long = System.currentTimeMillis() // Waktu pesan
)