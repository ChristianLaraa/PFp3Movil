package mx.unam.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mx.unam.data.model.UserProfile
import mx.unam.data.repository.AuthRepository

data class AuthUiState(
    val isLoading       : Boolean = false,
    val isLoginSuccess  : Boolean = false,
    val isRegisterSuccess : Boolean = false,
    val error           : String? = null
)

class AuthViewModel : ViewModel() {

    private val repo = AuthRepository()

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    // ── Login ─────────────────────────────────────────────────────────────────
    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _state.value = AuthUiState(error = "Completa todos los campos")
            return
        }
        viewModelScope.launch {
            _state.value = AuthUiState(isLoading = true)
            repo.login(email, password)
                .onSuccess { 
                    _state.value = AuthUiState(isLoginSuccess = true) 
                }
                .onFailure { e ->
                    _state.value = AuthUiState(error = friendlyError(e.message)) 
                }
        }
    }

    // ── Registro ──────────────────────────────────────────────────────────────
    fun register(
        nombre: String, apellidoP: String, apellidoM: String,
        username: String, email: String, password: String
    ) {
        if (listOf(nombre, apellidoP, apellidoM, username, email, password).any { it.isBlank() }) {
            _state.value = _state.value.copy(error = "Completa todos los campos", isLoading = false)
            return
        }
        if (password.length < 6) {
            _state.value = _state.value.copy(error = "La contraseña debe tener al menos 6 caracteres", isLoading = false)
            return
        }
        
        viewModelScope.launch {
            _state.value = AuthUiState(isLoading = true)
            val profile = UserProfile(
                nombre    = nombre,
                apellidoP = apellidoP,
                apellidoM = apellidoM,
                username  = username,
                email     = email
            )
            repo.register(email, password, profile)
                .onSuccess { 
                    _state.value = AuthUiState(isRegisterSuccess = true) 
                }
                .onFailure { e ->
                    _state.value = AuthUiState(error = friendlyError(e.message)) 
                }
        }
    }

    fun clearError() { 
        _state.value = _state.value.copy(error = null) 
    }
    
    fun resetState() {
        _state.value = AuthUiState()
    }

    private fun friendlyError(msg: String?): String = when {
        msg == null                              -> "Error desconocido"
        "email address is already"  in msg      -> "Este correo ya está registrado"
        "password is invalid"       in msg      -> "Contraseña incorrecta"
        "no user record"            in msg      -> "Usuario no encontrado"
        "badly formatted"           in msg      -> "Correo inválido"
        "network error"             in msg      -> "Sin conexión a internet"
        else                                    -> msg
    }
}
