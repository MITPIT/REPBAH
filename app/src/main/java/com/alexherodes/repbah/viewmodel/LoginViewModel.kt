package com.alexherodes.repbah.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    var email = mutableStateOf("")
    var password = mutableStateOf("")
    var isLoading = mutableStateOf(false)
    var errorMessage = mutableStateOf<String?>(null)
    var isLoggedIn = mutableStateOf(auth.currentUser != null)

    fun login() {
        if (email.value.isBlank() || password.value.isBlank()) {
            errorMessage.value = "Palun täida kõik väljad"
            return
        }
        isLoading.value = true
        errorMessage.value = null
        auth.signInWithEmailAndPassword(email.value, password.value)
            .addOnSuccessListener {
                isLoading.value = false
                isLoggedIn.value = true
            }
            .addOnFailureListener {
                isLoading.value = false
                errorMessage.value = "Viga: ${it.localizedMessage}"
            }
    }
}