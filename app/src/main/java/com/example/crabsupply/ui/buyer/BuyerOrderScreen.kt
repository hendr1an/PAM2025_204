package com.example.crabsupply.ui.buyer

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
import com.example.crabsupply.viewmodel.OrderViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyerOrderScreen(
    onBackClick: () -> Unit
) {
    val viewModel: OrderViewModel = viewModel()
    val orderList by viewModel.buyerOrders.collectAsState()

    // Load data saat layar dibuka
    LaunchedEffect(Unit) {
        viewModel.loadOrdersForBuyer()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Riwayat Pesanan Saya") },
                navigationIcon = {
                    Button(onClick = onBackClick) { Text("Kembali") }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).padding(16.dp)
        ) {
            if (orderList.isEmpty()) {
                item {
                    Text("Belum ada riwayat pesanan.", modifier = Modifier.padding(8.dp))
                }
            }

            items(orderList) { order ->
                BuyerOrderCard(order)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun BuyerOrderCard(order: Order) {
    // Warna Status
    val (statusColor, statusText) = when (order.status) {
        "pending" -> Pair(Color(0xFFFFF3E0), "MENUNGGU KONFIRMASI")
        "proses"  -> Pair(Color(0xFFE3F2FD), "SEDANG DIPROSES")
        "selesai" -> Pair(Color(0xFFE8F5E9), "SELESAI / DIKIRIM")
        else -> Pair(Color.LightGray, order.status.uppercase())
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = statusColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Status
            Text(
                text = statusText,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(text = order.productName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(text = "Jumlah: ${order.quantity} Kg • Jenis: ${order.productSpecies}")

            val formatRp = NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(order.totalPrice)
            Text(
                text = "Total: $formatRp",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text("Alamat Kirim: ${order.address}", fontSize = 12.sp)
        }
    }
}