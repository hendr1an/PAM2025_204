package com.example.crabsupply.ui.admin

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.crabsupply.viewmodel.AdminViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AdminDashboardScreen(
    onSeeOrdersClick: () -> Unit
) {
    val viewModel: AdminViewModel = viewModel()
    val totalRevenue by viewModel.totalRevenue.collectAsState()
    val totalOrders by viewModel.totalOrders.collectAsState()
    val totalProducts by viewModel.totalProducts.collectAsState()

    val filterMode by viewModel.filterMode.collectAsState()
    val startDate by viewModel.startDate.collectAsState()
    val endDate by viewModel.endDate.collectAsState()

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // --- LOGIKA DATE PICKER GANDA (RANGE) ---
    // 2. Dialog Tanggal Akhir (Muncul setelah tanggal awal dipilih)
    val endDatePicker = DatePickerDialog(
        context,
        { _, year, month, day ->
            val end = Calendar.getInstance().apply { set(year, month, day) }.time
            // Kirim Range ke ViewModel (Start sudah dipilih sebelumnya, sekarang End)
            // Pastikan End di set ke jam 23:59 agar data hari itu masuk semua
            viewModel.setFilterCustomRange(startDate, end)
        },
        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
    )
    endDatePicker.setTitle("Pilih Tanggal AKHIR")

    // 1. Dialog Tanggal Awal
    val startDatePicker = DatePickerDialog(
        context,
        { _, year, month, day ->
            val start = Calendar.getInstance().apply { set(year, month, day) }.time
            // Simpan start sementara di viewmodel (atau update state lokal)
            // Lalu langsung buka Date Picker kedua
            viewModel.setFilterCustomRange(start, start) // Set sementara
            endDatePicker.datePicker.minDate = start.time // Validasi: Akhir gaboleh sebelum Awal
            endDatePicker.show() // BUKA PICKER KEDUA
        },
        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
    )
    startDatePicker.setTitle("Pilih Tanggal AWAL")

    LaunchedEffect(Unit) {
        viewModel.loadDashboardStats()
    }

    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())
    ) {
        Text("Laporan Keuangan", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(16.dp))

        // --- TOMBOL FILTER ---
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            FilterButton("Hari Ini", filterMode == "HARI INI") { viewModel.setFilterToday() }
            FilterButton("Bulan Ini", filterMode == "BULAN INI") { viewModel.setFilterThisMonth() }
            // Tombol Custom memicu dialog pertama
            FilterButton("Pilih Range", filterMode == "CUSTOM") { startDatePicker.show() }
        }

        // Tampilkan Periode Aktif
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.padding(vertical = 12.dp).fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Periode: ${dateFormat.format(startDate)}  s/d  ${dateFormat.format(endDate)}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(12.dp).align(Alignment.CenterHorizontally)
            )
        }

        // KARTU OMSET
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50)),
            modifier = Modifier.fillMaxWidth().height(150.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = currencyFormat.format(totalRevenue), fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(text = "Omset (Selesai)", fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // STATISTIK LAIN
        Row(modifier = Modifier.fillMaxWidth()) {
            StatCard("Total Pesanan", "$totalOrders", Color(0xFF2196F3), Modifier.weight(1f))
            Spacer(modifier = Modifier.width(8.dp))
            StatCard("Jenis Produk", "$totalProducts", Color(0xFFFF9800), Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(24.dp))
        OutlinedButton(onClick = onSeeOrdersClick, modifier = Modifier.fillMaxWidth()) {
            Text("Lihat Rincian Pesanan")
        }
    }
}

@Composable
fun FilterButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray
        ),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(text, fontSize = 12.sp)
    }
}

@Composable
fun StatCard(label: String, value: String, color: Color, modifier: Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = color),
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(text = label, fontSize = 12.sp, color = Color.White)
        }
    }
}