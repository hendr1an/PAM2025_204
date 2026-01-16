package com.example.crabsupply.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crabsupply.data.model.Product
import com.example.crabsupply.data.repository.AuthRepository
import com.example.crabsupply.data.repository.ProductRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val productRepository = ProductRepository()
    private val authRepository = AuthRepository()

    // 1. Data Mentah dari Database (Semua Produk)
    private val _allProducts = MutableStateFlow<List<Product>>(emptyList())

    // 2. Kata Kunci Pencarian (Ketikan User)
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    // 3. Data Hasil Filter (Ini yang akan ditampilkan di layar)
    private val _filteredProducts = MutableStateFlow<List<Product>>(emptyList())
    val filteredProducts: StateFlow<List<Product>> = _filteredProducts

    private val _userRole = MutableStateFlow("buyer")
    val userRole: StateFlow<String> = _userRole

    init {
        startRealtimeUpdates()
        checkUserRole()
        observeSearch() // Mulai memantau ketikan
    }

    private fun checkUserRole() {
        authRepository.getUserRole { role ->
            _userRole.value = role
        }
    }

    private fun startRealtimeUpdates() {
        viewModelScope.launch {
            productRepository.getProductsRealtime().collect { updatedList ->
                _allProducts.value = updatedList
                // Saat data baru masuk, update juga hasil filternya
                filterData(_searchQuery.value, updatedList)
            }
        }
    }

    // Fungsi Logika Pencarian
    private fun observeSearch() {
        viewModelScope.launch {
            // Gabungkan data produk & search query
            _searchQuery.collect { query ->
                filterData(query, _allProducts.value)
            }
        }
    }

    private fun filterData(query: String, list: List<Product>) {
        if (query.isEmpty()) {
            _filteredProducts.value = list
        } else {
            // Cari yang namanya mengandung kata kunci (tidak peduli huruf besar/kecil)
            _filteredProducts.value = list.filter { product ->
                product.name.contains(query, ignoreCase = true) ||
                        product.species.contains(query, ignoreCase = true)
            }
        }
    }

    // Fungsi dipanggil saat user mengetik
    fun onSearchTextChange(text: String) {
        _searchQuery.value = text
    }

    fun logout() {
        FirebaseAuth.getInstance().signOut()
    }
}