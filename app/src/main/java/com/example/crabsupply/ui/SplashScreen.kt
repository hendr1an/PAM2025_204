package com.example.crabsupply.ui

import androidx.compose.foundation.Image // <--- Ganti Import
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource // <--- Import Penting
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.crabsupply.R // <--- Pastikan Import R ada (sesuaikan package Anda)
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(2500)
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            onNavigateToHome()
        } else {
            onNavigateToLogin()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            // --- BAGIAN LOGO KEPITING ---
            Image(
                // Panggil file logo_crab yang ada di drawable
                painter = painterResource(id = R.drawable.logo_crab),
                contentDescription = "Logo Crab Supply",
                modifier = Modifier.size(120.dp) // Sesuaikan ukuran
            )
            // ----------------------------

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Crab Supply",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "Solusi Bisnis Seafood B2B",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(48.dp))

            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp
            )
        }
    }
}