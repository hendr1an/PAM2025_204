package com.example.crabsupply.ui.admin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.crabsupply.data.model.Product
import com.example.crabsupply.ui.buyer.HomeScreen

@Composable
fun AdminMainScreen(
    onLogOut: () -> Unit,
    onAddProduct: () -> Unit,
    onEditProduct: (Product) -> Unit
) {
    // State untuk mencatat Tab mana yang sedang aktif
    // 0 = Pesanan, 1 = Katalog, 2 = Laporan
    var selectedTab by remember { mutableIntStateOf(0) }

    val tabs = listOf("Pesanan", "Katalog", "Laporan")
    val icons = listOf(
        Icons.Default.ShoppingCart, // Ikon Pesanan
        Icons.Default.Home,         // Ikon Katalog
        Icons.Default.DateRange     // Ikon Laporan
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, title ->
                    NavigationBarItem(
                        icon = { Icon(icons[index], contentDescription = title) },
                        label = { Text(title) },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index }
                    )
                }
            }
        }
    ) { innerPadding ->
        // Konten berganti sesuai Tab yang dipilih
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> AdminOrderScreen(
                    onBackClick = { /* Di Tab tidak perlu back, biarkan kosong */ }
                )

                1 -> HomeScreen(
                    onProfileClick = onLogOut, // Di tab katalog, icon profile jadi Logout
                    onAddProductClick = onAddProduct,
                    onEditClick = onEditProduct,
                    onDeleteClick = { /* Handle delete di dalam HomeScreen */ },
                    onProductClick = onEditProduct, // Admin klik produk -> Edit
                    // Tombol dashboard & history kita sembunyikan via logika role di HomeScreen
                )

                2 -> AdminDashboardScreen(
                    onBackClick = { /* Tidak perlu back */ },
                    onSeeOrdersClick = { selectedTab = 0 } // Jika klik "Lihat Order", pindah ke Tab 0
                )
            }
        }
    }
}