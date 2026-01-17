package com.example.crabsupply.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.crabsupply.viewmodel.AdminViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrderDetailScreen(
    onBackClick: () -> Unit
) {
    val viewModel: AdminViewModel = viewModel()
    val order = viewModel.selectedOrder.collectAsState().value
    val status by viewModel.uploadStatus.collectAsState()

    if (order == null) {
        Text("Data order tidak ditemukan")
        Button(onClick = onBackClick) { Text("Kembali") }
        return
    }

    // Formatter Tanggal (Contoh: 17 Jan 2026, 14:30)
    val timeFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))

    Scaffold(
        topBar = { TopAppBar(title = { Text("Detail Pesanan") }, navigationIcon = { Button(onClick = onBackClick) { Text("Kembali") } }) }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState())
        ) {
            // DETAIL PEMBELI
            Text("Info Pembeli", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Nama: ${order.buyerName}")
                    Text("No HP: ${order.buyerPhone}")
                    Text("Alamat: ${order.address}")
                }
            }

            // DETAIL PRODUK
            Text("Info Produk", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(top = 8.dp))
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Produk: ${order.productName}")
                    Text("Jumlah: ${order.quantity} Kg")
                    val formatRp = NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(order.totalPrice)
                    Text("Total Bayar: $formatRp", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }

            // --- TIMELINE / RIWAYAT WAKTU (BARU) ---
            Text("Riwayat Pesanan", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(top = 8.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // 1. Waktu Masuk
                    TimelineRow("Pesanan Masuk", order.dateCreated, timeFormat, true)

                    // 2. Waktu Proses
                    TimelineRow("Diproses Admin", order.dateProcessed, timeFormat, order.dateProcessed > 0)

                    // 3. Waktu Selesai
                    TimelineRow("Selesai / Dikirim", order.dateCompleted, timeFormat, order.dateCompleted > 0)
                }
            }

            // BUKTI TRANSFER
            Text("Bukti Transfer", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(top = 8.dp))
            Box(modifier = Modifier.height(150.dp).fillMaxWidth().background(Color.LightGray)) {
                Text("Belum ada bukti upload", modifier = Modifier.padding(16.dp))
            }

            // TOMBOL AKSI
            Spacer(modifier = Modifier.height(24.dp))
            Text("Update Status:", fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(
                    onClick = { viewModel.updateOrderStatus(order.id, "proses") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107)),
                    enabled = order.status == "pending"
                ) { Text("PROSES", color = Color.Black) }

                Button(
                    onClick = { viewModel.updateOrderStatus(order.id, "selesai") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    enabled = order.status == "proses"
                ) { Text("SELESAI") }
            }

            if (status != null) {
                Text(text = status ?: "", color = Color.Red, modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}

@Composable
fun TimelineRow(label: String, timestamp: Long, formatter: SimpleDateFormat, isActive: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = if (isActive) Color(0xFF4CAF50) else Color.Gray,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = label, fontWeight = if(isActive) FontWeight.Bold else FontWeight.Normal)
            if (isActive && timestamp > 0) {
                Text(text = formatter.format(Date(timestamp)), fontSize = 12.sp, color = Color.DarkGray)
            } else {
                Text(text = "-", fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}