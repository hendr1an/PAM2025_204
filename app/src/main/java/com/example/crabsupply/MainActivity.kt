package com.example.crabsupply

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.crabsupply.data.model.Product
import com.example.crabsupply.ui.SplashScreen // <--- IMPORT BARU
import com.example.crabsupply.ui.admin.AddProductScreen
import com.example.crabsupply.ui.admin.AdminDashboardScreen
import com.example.crabsupply.ui.admin.AdminOrderScreen
import com.example.crabsupply.ui.admin.EditProductScreen
import com.example.crabsupply.ui.auth.LoginScreen
import com.example.crabsupply.ui.auth.ProfileScreen
import com.example.crabsupply.ui.auth.RegisterScreen
import com.example.crabsupply.ui.buyer.BuyerOrderScreen
import com.example.crabsupply.ui.buyer.HomeScreen
import com.example.crabsupply.ui.buyer.ProductDetailScreen
import com.example.crabsupply.ui.theme.CrabSupplyTheme
import com.example.crabsupply.viewmodel.AdminViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CrabSupplyTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val adminViewModel: AdminViewModel = viewModel()
                    val deleteStatus by adminViewModel.uploadStatus.collectAsState()
                    val context = LocalContext.current

                    LaunchedEffect(deleteStatus) {
                        if (deleteStatus == "DELETE_SUCCESS") {
                            Toast.makeText(context, "Produk dihapus!", Toast.LENGTH_SHORT).show()
                            adminViewModel.resetStatus()
                        }
                    }

                    // --- UBAH BAGIAN INI ---
                    // Tidak perlu cek Auth di sini lagi, biarkan Splash yang kerja
                    // Start awal selalu "splash"
                    var currentScreen by remember { mutableStateOf("splash") }

                    var selectedProduct by remember { mutableStateOf<Product?>(null) }

                    when (currentScreen) {
                        // HALAMAN BARU: SPLASH
                        "splash" -> SplashScreen(
                            onNavigateToHome = { currentScreen = "home" },
                            onNavigateToLogin = { currentScreen = "login" }
                        )

                        "login" -> LoginScreen(onLoginSuccess = { currentScreen = "home" }, onRegisterClick = { currentScreen = "register" })
                        "register" -> RegisterScreen(onRegisterSuccess = { currentScreen = "login" }, onLoginClick = { currentScreen = "login" })

                        "home" -> HomeScreen(
                            onProfileClick = { currentScreen = "profile" },
                            onAddProductClick = { currentScreen = "add_product" },
                            onEditClick = { product -> selectedProduct = product; currentScreen = "edit_product" },
                            onDeleteClick = { product -> adminViewModel.deleteProduct(product.id) },
                            onProductClick = { product -> selectedProduct = product; currentScreen = "detail_product" },
                            onAdminDashboardClick = { currentScreen = "admin_dashboard" },
                            onBuyerHistoryClick = { currentScreen = "buyer_orders" }
                        )

                        "add_product" -> AddProductScreen(onBackClick = { currentScreen = "home" })
                        "edit_product" -> selectedProduct?.let { EditProductScreen(productToEdit = it, onBackClick = { currentScreen = "home" }) }
                        "detail_product" -> selectedProduct?.let { ProductDetailScreen(product = it, onBackClick = { currentScreen = "home" }) }
                        "profile" -> ProfileScreen(onBackClick = { currentScreen = "home" }, onLogoutSuccess = { currentScreen = "login" })
                        "buyer_orders" -> BuyerOrderScreen(onBackClick = { currentScreen = "home" })
                        "admin_dashboard" -> AdminDashboardScreen(
                            onBackClick = { currentScreen = "home" },
                            onSeeOrdersClick = { currentScreen = "admin_orders" }
                        )
                        "admin_orders" -> AdminOrderScreen(onBackClick = { currentScreen = "admin_dashboard" })
                    }
                }
            }
        }
    }
}