package com.example.crabsupply.data.repository

import com.example.crabsupply.data.model.Order
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class OrderRepository {
    private val firestore = FirebaseFirestore.getInstance()

    // Fungsi Kirim Pesanan
    fun createOrder(order: Order, onResult: (Boolean, String) -> Unit) {
        firestore.collection("orders")
            .add(order)
            .addOnSuccessListener { docRef ->
                // Update ID dokumen agar sama
                docRef.update("id", docRef.id)
                onResult(true, "Pesanan Berhasil Dibuat!")
            }
            .addOnFailureListener { e ->
                onResult(false, "Gagal memesan: ${e.message}")
            }
    }

    fun getAllOrdersRealtime(): Flow<List<Order>> = callbackFlow {
        val listener = firestore.collection("orders")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING) // Urutkan dari yang terbaru
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val orders = snapshot.documents.map { doc ->
                        doc.toObject(Order::class.java)!!.copy(id = doc.id)
                    }
                    trySend(orders)
                }
            }
        awaitClose { listener.remove() }
    }

    // 2. FUNGSI UPDATE STATUS PESANAN
    fun updateOrderStatus(orderId: String, newStatus: String, onResult: (Boolean) -> Unit) {
        firestore.collection("orders").document(orderId)
            .update("status", newStatus)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }
}