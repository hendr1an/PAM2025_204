package com.example.crabsupply.ui.buyer

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.crabsupply.data.model.Product
import com.example.crabsupply.viewmodel.OrderViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    product: Product, // Data produk yang dikirim dari Home
    onBackClick: () -> Unit
) {
    val viewModel: OrderViewModel = viewModel()
    val isLoading by viewModel.isLoading.collectAsState()
    val status by viewModel.orderStatus.collectAsState()
    val context = LocalContext.current

    // Notifikasi
    LaunchedEffect(status) {
        if (status == "SUCCESS") {
            Toast.makeText(context, "Pesanan Berhasil Dibuat!", Toast.LENGTH_LONG).show()
            viewModel.resetStatus()
            onBackClick() // Kembali ke Home setelah pesan
        } else if (status != null) {
            Toast.makeText(context, status, Toast.LENGTH_SHORT).show()
            viewModel.resetStatus()
        }
    }

    // Input Pesanan
    var quantity by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Detail Produk") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // 1. INFO PRODUK (HEADER)
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = product.name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Jenis: ${product.species} • Kondisi: ${product.condition}")
                    Spacer(modifier = Modifier.height(8.dp))

                    val formatRp = NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(product.priceRetail)
                    Text(text = "$formatRp / Kg", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text(text = "Stok Tersedia: ${product.stock} Kg")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Form Pemesanan:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))

            // 2. FORM INPUT JUMLAH
            OutlinedTextField(
                value = quantity,
                onValueChange = { quantity = it },
                label = { Text("Mau beli berapa Kg?") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            // Kalkulasi Total Harga Real-time
            if (quantity.isNotEmpty()) {
                val qtyInt = quantity.toIntOrNull() ?: 0
                val total = qtyInt * product.priceRetail
                val formatTotal = NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(total)

                Text(
                    text = "Total Bayar: $formatTotal",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 3. FORM ALAMAT
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Alamat Pengiriman Lengkap") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 4. TOMBOL PESAN
            Button(
                onClick = {
                    viewModel.submitOrder(product, quantity, address)
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !isLoading
            ) {
                if (isLoading) CircularProgressIndicator(color = Color.White)
                else Text("PESAN SEKARANG")
            }

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(onClick = onBackClick, modifier = Modifier.fillMaxWidth()) {
                Text("Batal")
            }
        }
    }
}