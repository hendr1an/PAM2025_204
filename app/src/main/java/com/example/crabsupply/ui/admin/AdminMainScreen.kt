package com.example.crabsupply.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.crabsupply.data.model.Product
import com.example.crabsupply.ui.buyer.HomeScreen
import kotlinx.coroutines.launch

@Composable
fun AdminMainScreen(
    onNavigateToProfile: () -> Unit,
    onLogOut: () -> Unit,
    onAddProduct: () -> Unit,
    onEditProduct: (Product) -> Unit,
    onOrderDetailClick: () -> Unit
) {
    // 1. DEFAULT TAB = 1 (KATALOG) SESUAI REQUEST
    var selectedTab by remember { mutableIntStateOf(1) }

    // State untuk Drawer (Menu Kiri)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val tabs = listOf("Pesanan", "Katalog", "Laporan")
    val icons = listOf(Icons.Default.ShoppingCart, Icons.Default.Home, Icons.Default.DateRange)

    // DRAWER (POP UP DARI KIRI)
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Menu Admin", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 24.dp))

                        NavigationDrawerItem(
                            label = { Text("Kelola Profile") },
                            icon = { Icon(Icons.Default.Person, contentDescription = null) },
                            selected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                onNavigateToProfile()
                            }
                        )
                    }

                    // TOMBOL LOGOUT DI BAWAH
                    Button(
                        onClick = {
                            scope.launch { drawerState.close() }
                            onLogOut()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("LOGOUT")
                    }
                }
            }
        }
    ) {
        // KONTEN UTAMA
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
            Box(modifier = Modifier.padding(innerPadding)) {
                when (selectedTab) {
                    0 -> AdminOrderScreen(
                        onOrderClick = onOrderDetailClick
                    )

                    1 -> HomeScreen(
                        // Saat di Tab Katalog (Admin), tombol Menu Kiri akan membuka Drawer
                        onMenuClick = {
                            scope.launch { drawerState.open() }
                        },
                        isAdminMode = true, // Penanda mode admin
                        onAddProductClick = onAddProduct,
                        onEditClick = onEditProduct,
                        onProductClick = onEditProduct
                    )

                    2 -> AdminDashboardScreen(
                        onSeeOrdersClick = { selectedTab = 0 }
                    )
                }
            }
        }
    }
}