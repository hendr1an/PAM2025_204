package com.example.crabsupply.data.repository

import com.example.crabsupply.data.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProductRepository {
    private val firestore = FirebaseFirestore.getInstance()

    // Fungsi mengambil semua produk
    suspend fun getAllProducts(): List<Product> {
        return try {
            val snapshot = firestore.collection("products").get().await()
            snapshot.documents.map { doc ->
                // Mengubah data Firestore menjadi objek Product kita
                doc.toObject(Product::class.java)!!.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList() // Kalau error, kembalikan list kosong
        }
    }
}