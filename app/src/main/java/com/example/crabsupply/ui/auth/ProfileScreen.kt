package com.example.crabsupply.ui.auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.crabsupply.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    onLogoutSuccess: () -> Unit
) {
    val viewModel: ProfileViewModel = viewModel()
    val userData by viewModel.userData.collectAsState()
    val status by viewModel.statusMsg.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current

    // Load data awal
    LaunchedEffect(Unit) {
        viewModel.loadUserProfile()
    }

    // Notifikasi Simpan
    LaunchedEffect(status) {
        status?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.resetStatus()
        }
    }

    // State untuk Form (Diisi saat userData loaded)
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    // Update state form ketika data user berhasil diambil dari database
    LaunchedEffect(userData) {
        userData?.let {
            name = it.name
            phone = it.phone
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil Saya") },
                navigationIcon = {
                    Button(onClick = onBackClick) { Text("Kembali") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // INFO AKUN (READ ONLY)
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Email: ${userData?.email ?: "Loading..."}", fontWeight = FontWeight.Bold)
                    Text("Role: ${userData?.role?.uppercase() ?: "Loading..."}")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // FORM EDIT
            OutlinedTextField(
                value = name, onValueChange = { name = it },
                label = { Text("Nama Lengkap") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = phone, onValueChange = { phone = it },
                label = { Text("Nomor HP (WhatsApp)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // TOMBOL SIMPAN
            Button(
                onClick = { viewModel.saveProfile(name, phone) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !isLoading
            ) {
                if (isLoading) CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                else Text("SIMPAN PERUBAHAN")
            }

            Spacer(modifier = Modifier.height(32.dp))
            Divider()
            Spacer(modifier = Modifier.height(32.dp))

            // TOMBOL LOGOUT (MERAH)
            Button(
                onClick = {
                    viewModel.logout()
                    onLogoutSuccess()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("LOGOUT (KELUAR)")
            }
        }
    }
}