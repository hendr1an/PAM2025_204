package com.example.crabsupply.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crabsupply.data.model.Product
import com.example.crabsupply.data.repository.OrderRepository // <--- Import Baru
import com.example.crabsupply.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AdminViewModel : ViewModel() {
    private val repository = ProductRepository()
    private val orderRepository = OrderRepository() // <--- Tambahkan Repository Order

    // Status Upload/Delete
    private val _uploadStatus = MutableStateFlow<String?>(null)
    val uploadStatus: StateFlow<String?> = _uploadStatus

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // --- STATISTIK DASHBOARD (BARU) ---
    private val _totalRevenue = MutableStateFlow(0)
    val totalRevenue: StateFlow<Int> = _totalRevenue

    private val _totalOrders = MutableStateFlow(0)
    val totalOrders: StateFlow<Int> = _totalOrders

    private val _totalProducts = MutableStateFlow(0)
    val totalProducts: StateFlow<Int> = _totalProducts

    // FUNGSI HITUNG STATISTIK
    fun loadDashboardStats() {
        viewModelScope.launch {
            // 1. Hitung Produk
            repository.getProductsRealtime().collect { products ->
                _totalProducts.value = products.size
            }
        }
        viewModelScope.launch {
            // 2. Hitung Omset & Order
            orderRepository.getAllOrdersRealtime().collect { orders ->
                _totalOrders.value = orders.size

                // Hanya hitung uang jika status = "selesai"
                val revenue = orders.filter { it.status == "selesai" }
                    .sumOf { it.totalPrice }
                _totalRevenue.value = revenue
            }
        }
    }

    // --- FUNGSI LAMA (TETAP ADA) ---
    fun uploadProduct(
        name: String, species: String, condition: String,
        size: String, priceRetail: String, stock: String
    ) {
        _isLoading.value = true
        val priceInt = priceRetail.toIntOrNull() ?: 0
        val stockInt = stock.toIntOrNull() ?: 0

        val newProduct = Product(
            name = name, species = species, condition = condition,
            size = size, priceRetail = priceInt, priceWholesale = priceInt - 10000,
            stock = stockInt, isAvailable = stockInt > 0
        )

        repository.addProduct(newProduct) { success, message ->
            _isLoading.value = false
            _uploadStatus.value = if (success) "SUCCESS" else message
        }
    }

    fun deleteProduct(productId: String) {
        _isLoading.value = true
        repository.deleteProduct(productId) { success, message ->
            _isLoading.value = false
            _uploadStatus.value = if (success) "DELETE_SUCCESS" else message
        }
    }

    fun updateExistingProduct(
        id: String, name: String, species: String, condition: String,
        size: String, priceRetail: String, stock: String
    ) {
        _isLoading.value = true
        val priceInt = priceRetail.toIntOrNull() ?: 0
        val stockInt = stock.toIntOrNull() ?: 0

        val updatedProduct = Product(
            id = id, name = name, species = species, condition = condition,
            size = size, priceRetail = priceInt, priceWholesale = priceInt - 10000,
            stock = stockInt, isAvailable = stockInt > 0
        )

        repository.updateProduct(updatedProduct) { success, message ->
            _isLoading.value = false
            _uploadStatus.value = if (success) "UPDATE_SUCCESS" else message
        }
    }

    fun resetStatus() {
        _uploadStatus.value = null
    }
}