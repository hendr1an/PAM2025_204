package com.example.crabsupply.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crabsupply.data.model.Order
import com.example.crabsupply.data.model.Product
import com.example.crabsupply.data.repository.OrderRepository
import com.example.crabsupply.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class AdminViewModel : ViewModel() {
    private val repository = ProductRepository()
    private val orderRepository = OrderRepository()

    // Status & Loading
    private val _uploadStatus = MutableStateFlow<String?>(null)
    val uploadStatus: StateFlow<String?> = _uploadStatus
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // --- MANAJEMEN ORDER ---
    private val _adminOrders = MutableStateFlow<List<Order>>(emptyList())
    val adminOrders: StateFlow<List<Order>> = _adminOrders
    private val _selectedOrder = MutableStateFlow<Order?>(null)
    val selectedOrder: StateFlow<Order?> = _selectedOrder

    // --- DASHBOARD KEUANGAN (UPDATE BARU) ---
    // Filter Mode: "HARI INI", "BULAN INI", "CUSTOM"
    private val _filterMode = MutableStateFlow("HARI INI")
    val filterMode: StateFlow<String> = _filterMode

    // Rentang Tanggal (Start - End)
    private val _startDate = MutableStateFlow(getStartOfDay(Date()))
    val startDate: StateFlow<Date> = _startDate

    private val _endDate = MutableStateFlow(getEndOfDay(Date()))
    val endDate: StateFlow<Date> = _endDate

    private val _totalRevenue = MutableStateFlow(0)
    val totalRevenue: StateFlow<Int> = _totalRevenue
    private val _totalOrders = MutableStateFlow(0)
    val totalOrders: StateFlow<Int> = _totalOrders
    private val _totalProducts = MutableStateFlow(0)
    val totalProducts: StateFlow<Int> = _totalProducts

    // --- FUNGSI GANTI FILTER ---
    fun setFilterToday() {
        _filterMode.value = "HARI INI"
        _startDate.value = getStartOfDay(Date())
        _endDate.value = getEndOfDay(Date())
        recalculateStats()
    }

    fun setFilterThisMonth() {
        _filterMode.value = "BULAN INI"
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1) // Tanggal 1 bulan ini
        _startDate.value = getStartOfDay(calendar.time)

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) // Tanggal terakhir bulan ini
        _endDate.value = getEndOfDay(calendar.time)

        recalculateStats()
    }

    fun setFilterCustomRange(start: Date, end: Date) {
        _filterMode.value = "CUSTOM"
        _startDate.value = getStartOfDay(start)
        _endDate.value = getEndOfDay(end)
        recalculateStats()
    }

    // --- LOGIKA HITUNG ---
    fun loadDashboardStats() {
        viewModelScope.launch {
            repository.getProductsRealtime().collect { products ->
                _totalProducts.value = products.size
            }
        }
        viewModelScope.launch {
            orderRepository.getAllOrdersRealtime().collect { orders ->
                _adminOrders.value = orders
                recalculateStats()
            }
        }
    }

    private fun recalculateStats() {
        val allOrders = _adminOrders.value
        val start = _startDate.value.time
        val end = _endDate.value.time

        // FILTER YANG BENAR:
        // Cek apakah 'dateCreated' order ada di antara 'start' dan 'end'
        val filteredOrders = allOrders.filter { order ->
            order.dateCreated in start..end
        }

        val revenue = filteredOrders.filter { it.status == "selesai" }.sumOf { it.totalPrice }

        _totalOrders.value = filteredOrders.size
        _totalRevenue.value = revenue
    }

    // --- HELPER TANGGAL ---
    private fun getStartOfDay(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        return calendar.time
    }

    private fun getEndOfDay(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        return calendar.time
    }

    // --- FUNGSI PRODUK & ORDER LAINNYA (TETAP SAMA) ---
    fun selectOrder(order: Order) { _selectedOrder.value = order }

    fun updateOrderStatus(orderId: String, newStatus: String) {
        _isLoading.value = true
        orderRepository.updateOrderStatus(orderId, newStatus) { success ->
            _isLoading.value = false
            if (success) {
                _uploadStatus.value = "Status: $newStatus"
                _selectedOrder.value = _selectedOrder.value?.copy(status = newStatus)
                // loadDashboardStats() // Refresh revenue jika perlu
            }
        }
    }

    fun uploadProduct(name: String, category: String, species: String, condition: String, size: String, priceRetail: String, stock: String, imageUrl: String) {
        _isLoading.value = true
        val finalSpecies = if (category == "Kepiting") species else "-"
        val finalCondition = if (category == "Kepiting") condition else "-"
        val finalSize = if (category == "Kepiting") size else "-"
        val priceInt = priceRetail.toIntOrNull() ?: 0
        val stockDouble = stock.toDoubleOrNull() ?: 0.0
        val newProduct = Product(name = name, category = category, species = finalSpecies, condition = finalCondition, size = finalSize, priceRetail = priceInt, priceWholesale = priceInt - 10000, stock = stockDouble, isAvailable = stockDouble > 0.0, imageUrl = imageUrl)
        repository.addProduct(newProduct) { success, msg ->
            _isLoading.value = false
            _uploadStatus.value = if (success) "SUCCESS" else msg
        }
    }

    fun deleteProduct(productId: String) {
        repository.deleteProduct(productId) { success, _ -> if (success) _uploadStatus.value = "DELETE_SUCCESS" }
    }

    fun updateExistingProduct(id: String, name: String, species: String, condition: String, size: String, priceRetail: String, stock: String) {}
    fun resetStatus() { _uploadStatus.value = null }
}