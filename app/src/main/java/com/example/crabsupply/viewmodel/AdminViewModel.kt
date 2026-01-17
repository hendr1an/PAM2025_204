package com.example.crabsupply.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crabsupply.data.model.Product
import com.example.crabsupply.data.repository.OrderRepository
import com.example.crabsupply.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AdminViewModel : ViewModel() {
    private val repository = ProductRepository()
    private val orderRepository = OrderRepository()

    // Status Upload/Delete
    private val _uploadStatus = MutableStateFlow<String?>(null)
    val uploadStatus: StateFlow<String?> = _uploadStatus

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // --- STATISTIK DASHBOARD ---
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

    // --- FUNGSI TAMBAH PRODUK (PERBAIKAN: BYPASS UPLOAD) ---
    fun uploadProduct(
        name: String,
        category: String,
        species: String,
        condition: String,
        size: String,
        priceRetail: String,
        stock: String,
        imageUrl: String // Menerima Link Gambar langsung (String)
    ) {
        _isLoading.value = true

        // 1. LOGIKA KATEGORI: Atur strip "-" jika bukan kepiting
        val finalSpecies = if (category == "Kepiting") species else "-"
        val finalCondition = if (category == "Kepiting") condition else "-"
        val finalSize = if (category == "Kepiting") size else "-"

        // 2. LANGSUNG SIMPAN KE DATABASE (TANPA UPLOAD KE STORAGE)
        // Kita langsung oper `imageUrl` ke fungsi simpan.
        saveProductToFirestore(name, category, finalSpecies, finalCondition, finalSize, priceRetail, stock, imageUrl)
    }

    // Fungsi Bantuan untuk Menyimpan ke Database
    private fun saveProductToFirestore(
        name: String,
        category: String,
        species: String, condition: String,
        size: String, priceRetail: String, stock: String,
        imageUrl: String
    ) {
        val priceInt = priceRetail.toIntOrNull() ?: 0
        // Logika Desimal
        val stockDouble = stock.toDoubleOrNull() ?: 0.0

        val newProduct = Product(
            name = name,
            category = category,
            species = species,
            condition = condition,
            size = size,
            priceRetail = priceInt,
            priceWholesale = priceInt - 10000,
            stock = stockDouble,
            isAvailable = stockDouble > 0.0,
            imageUrl = imageUrl // Simpan link yang diinput admin
        )

        repository.addProduct(newProduct) { success, message ->
            _isLoading.value = false
            _uploadStatus.value = if (success) "SUCCESS" else message
        }
    }

    // --- FUNGSI UPDATE & DELETE ---
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
        val stockDouble = stock.toDoubleOrNull() ?: 0.0

        // Asumsi saat update gambar tidak berubah dulu (atau logic lain)
        // Disini kita buat object product sederhana untuk update data teks
        val updatedProduct = Product(
            id = id, name = name, species = species, condition = condition,
            size = size, priceRetail = priceInt, priceWholesale = priceInt - 10000,
            stock = stockDouble, isAvailable = stockDouble > 0.0
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