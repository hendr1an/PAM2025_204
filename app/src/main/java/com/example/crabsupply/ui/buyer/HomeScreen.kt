package com.example.crabsupply.ui.buyer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.crabsupply.viewmodel.HomeViewModel
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Person // <--- IMPORT BARU (Ikon Orang)
import androidx.compose.ui.Alignment
import com.example.crabsupply.data.model.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    // onLogoutClick DIHAPUS, dipindah ke halaman Profil
    onProfileClick: () -> Unit = {}, // <--- PARAMETER BARU (Ke Profil)

    onAddProductClick: () -> Unit,
    onEditClick: (Product) -> Unit = {},
    onDeleteClick: (Product) -> Unit = {},
    onProductClick: (Product) -> Unit = {},
    onAdminOrdersClick: () -> Unit = {},
    onBuyerHistoryClick: () -> Unit = {}
) {
    val viewModel: HomeViewModel = viewModel()

    // Ambil data hasil filter
    val productList by viewModel.filteredProducts.collectAsState()
    val role by viewModel.userRole.collectAsState()
    val searchText by viewModel.searchQuery.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Katalog ($role)") },

                // Ikon Kiri (List Admin / History Buyer)
                navigationIcon = {
                    if (role == "admin") {
                        IconButton(onClick = onAdminOrdersClick) {
                            Icon(Icons.Default.List, contentDescription = "Kelola Pesanan")
                        }
                    } else {
                        IconButton(onClick = onBuyerHistoryClick) {
                            Icon(Icons.Default.DateRange, contentDescription = "Riwayat Pesanan")
                        }
                    }
                },

                // Ikon Kanan (Profil Akun)
                actions = {
                    // TOMBOL KELUAR DIGANTI IKON PROFIL
                    IconButton(onClick = onProfileClick) {
                        Icon(Icons.Default.Person, contentDescription = "Profil Akun")
                    }
                }
            )
        },
        floatingActionButton = {
            if (role == "admin") {
                FloatingActionButton(
                    onClick = onAddProductClick,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Text("+", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // --- KOLOM PENCARIAN ---
            OutlinedTextField(
                value = searchText,
                onValueChange = { viewModel.onSearchTextChange(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Cari Kepiting (Ex: Rajungan)") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = MaterialTheme.shapes.medium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- DAFTAR PRODUK ---
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                if (productList.isEmpty()) {
                    item {
                        Text(
                            text = "Produk tidak ditemukan.",
                            modifier = Modifier.padding(8.dp),
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }

                items(productList) { product ->
                    ProductCard(
                        product = product,
                        isAdmin = (role == "admin"),
                        onEdit = { onEditClick(product) },
                        onDelete = { onDeleteClick(product) },
                        onClick = { onProductClick(product) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

// ProductCard Tetap Sama
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCard(
    product: Product,
    isAdmin: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = product.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                if (isAdmin) {
                    Row {
                        IconButton(onClick = onEdit) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                        }
                        IconButton(onClick = onDelete) {
                            Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }

            Text(
                text = "${product.species} • ${product.condition} • Size ${product.size}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val formatRp = NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(product.priceRetail)
                Text(text = formatRp, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text(text = "Stok: ${product.stock} kg", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}