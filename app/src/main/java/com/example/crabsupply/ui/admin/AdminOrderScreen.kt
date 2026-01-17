package com.example.crabsupply.ui.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.crabsupply.data.model.Order
import com.example.crabsupply.viewmodel.AdminViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun AdminOrderScreen(
    onBackClick: () -> Unit = {}, // Default kosong
    onOrderClick: () -> Unit
) {
    val viewModel: AdminViewModel = viewModel()
    val orders by viewModel.adminOrders.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadDashboardStats() // Muat data order terbaru
    }

    // LIST PESANAN (Tanpa Scaffold/TopBar karena sudah ada di MainScreen)
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (orders.isEmpty()) {
            item { Text("Belum ada pesanan masuk.") }
        }
        items(orders) { order ->
            AdminOrderCard(order = order, onClick = {
                viewModel.selectOrder(order) // Simpan order yang dipilih
                onOrderClick() // Pindah layar
            })
        }
    }
}

@Composable
fun AdminOrderCard(order: Order, onClick: () -> Unit) {
    val statusColor = when (order.status) {
        "pending" -> Color(0xFFE0E0E0) // Abu
        "proses" -> Color(0xFFFFF176) // Kuning
        "selesai" -> Color(0xFFA5D6A7) // Hijau
        else -> Color.White
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = statusColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = order.buyerName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = order.status.uppercase(), fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("Barang: ${order.productName} (${order.quantity} Kg)")

            val formatRp = NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(order.totalPrice)
            Text("Total: $formatRp", color = Color.Blue, fontWeight = FontWeight.Bold)
            Text("Alamat: ${order.address}", maxLines = 1, fontSize = 12.sp)
        }
    }
}