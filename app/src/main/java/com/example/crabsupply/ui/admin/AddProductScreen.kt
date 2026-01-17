package com.example.crabsupply.ui.admin

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage // Untuk preview gambar dari link
import com.example.crabsupply.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    onBackClick: () -> Unit
) {
    val viewModel: AdminViewModel = viewModel()
    val status by viewModel.uploadStatus.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current

    // State Form
    var name by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Kepiting") }
    var species by remember { mutableStateOf("") }
    var condition by remember { mutableStateOf("") }
    var size by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }

    // STATE BARU: LINK GAMBAR (STRING)
    var imageUrl by remember { mutableStateOf("") }

    // Pantau Status Upload
    LaunchedEffect(status) {
        if (status == "SUCCESS") {
            Toast.makeText(context, "Produk Berhasil Ditambah!", Toast.LENGTH_SHORT).show()
            viewModel.resetStatus()
            onBackClick()
        } else if (status != null) {
            Toast.makeText(context, status, Toast.LENGTH_SHORT).show()
            viewModel.resetStatus()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Produk Baru") },
                navigationIcon = { Button(onClick = onBackClick) { Text("Batal") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- 1. INPUT LINK GAMBAR (MANUAL) ---
            OutlinedTextField(
                value = imageUrl, onValueChange = { imageUrl = it },
                label = { Text("Link Gambar (URL)") },
                placeholder = { Text("Paste link Google Image di sini...") },
                modifier = Modifier.fillMaxWidth()
            )

            // Preview Gambar (Jika link valid)
            if (imageUrl.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Preview",
                    modifier = Modifier.height(150.dp).fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- 2. INPUT DATA UMUM ---
            OutlinedTextField(
                value = name, onValueChange = { name = it },
                label = { Text("Nama Produk") },
                modifier = Modifier.fillMaxWidth()
            )

            // --- 3. PILIHAN KATEGORI (SAKLAR) ---
            Text(
                "Jenis Produk:",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 8.dp)
            )
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                RadioButton(selected = selectedCategory == "Kepiting", onClick = { selectedCategory = "Kepiting" })
                Text("Kepiting")
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(selected = selectedCategory == "Non-Kepiting", onClick = { selectedCategory = "Non-Kepiting" })
                Text("Lainnya")
            }

            // --- 4. FORM SPESIFIK ---
            if (selectedCategory == "Kepiting") {
                OutlinedTextField(value = species, onValueChange = { species = it }, label = { Text("Spesies (Bakau/Rajungan)") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = condition, onValueChange = { condition = it }, label = { Text("Kondisi (Telur/Daging)") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = size, onValueChange = { size = it }, label = { Text("Ukuran (Ex: 300gr)") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
            }

            // --- 5. HARGA & STOK ---
            OutlinedTextField(
                value = price, onValueChange = { price = it },
                label = { Text("Harga Eceran (Rp)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = stock, onValueChange = { stock = it },
                label = { Text("Stok Awal (Kg) - Boleh Desimal") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- TOMBOL SIMPAN ---
            Button(
                onClick = {
                    viewModel.uploadProduct(
                        name, selectedCategory, species, condition, size, price, stock, imageUrl
                    )
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !isLoading
            ) {
                if (isLoading) CircularProgressIndicator(color = Color.White)
                else Text("SIMPAN PRODUK")
            }
        }
    }
}