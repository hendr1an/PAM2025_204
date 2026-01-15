package com.example.crabsupply.ui.admin

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.crabsupply.data.model.Product
import com.example.crabsupply.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductScreen(
    productToEdit: Product, // Menerima data produk yang mau diedit
    onBackClick: () -> Unit = {}
) {
    val viewModel: AdminViewModel = viewModel()
    val isLoading by viewModel.isLoading.collectAsState()
    val status by viewModel.uploadStatus.collectAsState()
    val context = LocalContext.current

    // Notifikasi Sukses
    LaunchedEffect(status) {
        if (status == "UPDATE_SUCCESS") {
            Toast.makeText(context, "Produk Berhasil Diupdate!", Toast.LENGTH_SHORT).show()
            viewModel.resetStatus()
            onBackClick() // Kembali ke Home
        } else if (status != null) {
            Toast.makeText(context, status, Toast.LENGTH_LONG).show()
            viewModel.resetStatus()
        }
    }

    // Isi Form dengan Data Lama (Pre-fill)
    var name by remember { mutableStateOf(productToEdit.name) }
    var species by remember { mutableStateOf(productToEdit.species) }
    var condition by remember { mutableStateOf(productToEdit.condition) }
    var size by remember { mutableStateOf(productToEdit.size) }
    var price by remember { mutableStateOf(productToEdit.priceRetail.toString()) }
    var stock by remember { mutableStateOf(productToEdit.stock.toString()) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Edit Produk") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = name, onValueChange = { name = it },
                label = { Text("Nama Produk") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = species, onValueChange = { species = it },
                label = { Text("Jenis") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = condition, onValueChange = { condition = it },
                label = { Text("Kondisi") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = size, onValueChange = { size = it },
                label = { Text("Ukuran") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = price, onValueChange = { price = it },
                label = { Text("Harga Eceran (Rp)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = stock, onValueChange = { stock = it },
                label = { Text("Stok (Kg)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    // Panggil Fungsi UPDATE
                    viewModel.updateExistingProduct(
                        id = productToEdit.id, // ID Lama
                        name, species, condition, size, price, stock
                    )
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !isLoading
            ) {
                if (isLoading) CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                else Text("UPDATE DATA")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tombol Batal
            OutlinedButton(
                onClick = onBackClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Batal")
            }
        }
    }
}