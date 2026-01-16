package com.example.crabsupply.viewmodel

import androidx.lifecycle.ViewModel
import com.example.crabsupply.data.model.User
import com.example.crabsupply.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProfileViewModel : ViewModel() {
    private val authRepository = AuthRepository()
    private val auth = FirebaseAuth.getInstance()

    // Data User yang sedang login
    private val _userData = MutableStateFlow<User?>(null)
    val userData: StateFlow<User?> = _userData

    private val _statusMsg = MutableStateFlow<String?>(null)
    val statusMsg: StateFlow<String?> = _statusMsg

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Load Data saat halaman dibuka
    fun loadUserProfile() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            authRepository.getUserDetail(userId) { user ->
                _userData.value = user
            }
        }
    }

    // Simpan Perubahan
    fun saveProfile(name: String, phone: String) {
        _isLoading.value = true
        val userId = auth.currentUser?.uid
        if (userId != null) {
            authRepository.updateUserProfile(userId, name, phone) { success, msg ->
                _isLoading.value = false
                _statusMsg.value = msg
            }
        }
    }

    // Logout
    fun logout() {
        auth.signOut()
    }

    fun resetStatus() {
        _statusMsg.value = null
    }
}