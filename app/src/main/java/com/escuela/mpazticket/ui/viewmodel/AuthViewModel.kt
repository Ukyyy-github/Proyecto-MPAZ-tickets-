package com.escuela.mpazticket.ui.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.escuela.mpazticket.data.local.entities.UserEntity
import com.escuela.mpazticket.data.local.entities.UserRole
import com.escuela.mpazticket.repository.TicketRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: TicketRepository
) : ViewModel() {

    private val _currentUser = MutableStateFlow<UserEntity?>(
        // Default guest/admin user for immediate usage
        UserEntity(
            email = "admin@mpazticket.com",
            nombreCompleto = "Administrador TI",
            contrasena = "123456",
            rol = UserRole.ENCARGADO_TI
        )
    )
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    private val _registerError = MutableStateFlow<String?>(null)
    val registerError: StateFlow<String?> = _registerError.asStateFlow()

    private val _registerSuccess = MutableStateFlow(false)
    val registerSuccess: StateFlow<Boolean> = _registerSuccess.asStateFlow()

    fun login(emailInput: String, contrasenaInput: String, onSuccess: () -> Unit) {
        val emailClean = emailInput.trim()
        val passClean = contrasenaInput.trim()

        if (emailClean.isBlank()) {
            _loginError.value = "Por favor, ingresa tu correo electrónico."
            return
        }

        if (!isValidEmail(emailClean)) {
            _loginError.value = "El formato del correo electrónico no es válido."
            return
        }

        if (passClean.isBlank()) {
            _loginError.value = "Por favor, ingresa tu contraseña."
            return
        }

        viewModelScope.launch {
            val user = repository.getUserByEmail(emailClean)
            if (user == null) {
                _loginError.value = "El usuario no existe en el sistema."
            } else if (user.contrasena != passClean) {
                _loginError.value = "Contraseña incorrecta."
            } else {
                _loginError.value = null
                _currentUser.value = user
                onSuccess()
            }
        }
    }

    fun register(
        nombreCompleto: String,
        email: String,
        contrasena: String,
        confirmarContrasena: String,
        rol: UserRole,
        onSuccess: () -> Unit
    ) {
        val nombreClean = nombreCompleto.trim()
        val emailClean = email.trim()
        val passClean = contrasena.trim()

        if (nombreClean.isBlank()) {
            _registerError.value = "Por favor, ingresa tu nombre completo."
            return
        }

        if (emailClean.isBlank()) {
            _registerError.value = "Por favor, ingresa tu correo electrónico."
            return
        }

        if (!isValidEmail(emailClean)) {
            _registerError.value = "El formato de correo electrónico no es válido."
            return
        }

        if (passClean.length < 6) {
            _registerError.value = "La contraseña debe tener al menos 6 caracteres."
            return
        }

        if (passClean != confirmarContrasena.trim()) {
            _registerError.value = "Las contraseñas no coinciden."
            return
        }

        viewModelScope.launch {
            val existing = repository.getUserByEmail(emailClean)
            if (existing != null) {
                _registerError.value = "Ya existe un usuario registrado con este correo."
            } else {
                val newUser = UserEntity(
                    email = emailClean,
                    nombreCompleto = nombreClean,
                    contrasena = passClean,
                    rol = rol
                )
                repository.insertUser(newUser)
                _registerError.value = null
                _currentUser.value = newUser
                _registerSuccess.value = true
                onSuccess()
            }
        }
    }

    fun logout(onLogoutDone: () -> Unit) {
        _currentUser.value = null
        _loginError.value = null
        _registerError.value = null
        onLogoutDone()
    }

    fun resetRegisterState() {
        _registerError.value = null
        _registerSuccess.value = false
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches() || email.contains("@")
    }
}
