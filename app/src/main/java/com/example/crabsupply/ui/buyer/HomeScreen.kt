package com.example.crabsupply.ui.buyer

import androidx.compose.foundation.background // Import Baru
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape // Import Baru
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip // Import Baru
import androidx.compose.ui.graphics.Color // Import Baru
import androidx.compose.ui.layout.ContentScale // Import Baru
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage // <--- PENTING: Import Coil untuk Gambar
import com.example.crabsupply.viewmodel.HomeViewModel
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.Alignment
import com.example.crabsupply.data.model.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onProfileClick: () -> Unit = {},
    onAddProductClick: () -> Unit,
    onEditClick: (Product) -> Unit = {},
    onDeleteClick: (Product) -> Unit = {},
    onProductClick: (Product) -> Unit = {},
    onAdminDashboardClick: () -> Unit = {},
    onBuyerHistoryClick: () -> Unit = {}
) {
    val viewModel: HomeViewModel = viewModel()
    val productList by viewModel.filteredProducts.collectAsState()
    val role by viewModel.userRole.collectAsState()
    val searchText by viewModel.searchQuery.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Katalog ($role)") },
                navigationIcon = {
                    if (role == "admin") {
                        // ADMIN: KE DASHBOARD
                        IconButton(onClick = onAdminDashboardClick) {
                            Icon(Icons.Default.Info, contentDescription = "Dashboard Admin")
                        }
                    } else {
                        // BUYER: KE RIWAYAT
                        IconButton(onClick = onBuyerHistoryClick) {
                            Icon(Icons.Default.DateRange, contentDescription = "Riwayat Pesanan")
                        }
                    }
                },
                actions = {
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
            // PENCARIAN
            OutlinedTextField(
                value = searchText,
                onValueChange = { viewModel.onSearchTextChange(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Cari Kepiting...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = MaterialTheme.shapes.medium
            )
            Spacer(modifier = Modifier.height(16.dp))

            // DAFTAR PRODUK
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                if (productList.isEmpty()) {
                    item { Text("Produk tidak ditemukan.", modifier = Modifier.padding(8.dp)) }
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

// --- BAGIAN INI YANG DIREVISI TOTAL (TAMPILKAN GAMBAR) ---
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
        // Gunakan ROW agar Gambar di Kiri, Teks di Kanan
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. BAGIAN GAMBAR (KIRI)
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Gray)
            ) {
                if (product.imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = "Foto Produk",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Placeholder jika tidak ada gambar
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No Img", fontSize = 10.sp, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 2. BAGIAN INFO TEKS (KANAN)
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = product.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )

                    if (isAdmin) {
                        Row {
                            IconButton(onClick = onEdit, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }

                Text(
                    text = "${product.species} • ${product.size}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                val formatRp = NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(product.priceRetail)
                Text(text = formatRp, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text(text = "Stok: ${product.stock} kg", fontSize = 12.sp)
            }
        }
    }
}