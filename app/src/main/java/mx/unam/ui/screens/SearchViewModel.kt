package mx.unam.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import mx.unam.data.model.Character
import mx.unam.data.model.UserProfile
import mx.unam.data.repository.CharacterRepository

data class SearchUiState(
    val isLoading       : Boolean         = false,
    val characters      : List<Character> = emptyList(),
    val catalog         : List<Character> = emptyList(),
    val error           : String?         = null,
    val isSearching     : Boolean         = false,
    val libraryConsulted: Boolean         = false,
    val username        : String          = "",
    val name            : String          = ""
)

class SearchViewModel : ViewModel() {

    private val repo = CharacterRepository()

    private val _state = MutableStateFlow(SearchUiState())
    val state: StateFlow<SearchUiState> = _state.asStateFlow()

    init {
        fetchUserProfile()
    }

    fun fetchUserProfile() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val snapshot = FirebaseFirestore.getInstance().collection("usuarios").document(uid).get().await()
                val profile = snapshot.toObject(UserProfile::class.java)
                if (profile != null) {
                    _state.value = _state.value.copy(
                        username = profile.username,
                        name = profile.nombre
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateAccount(newUsername: String, newPassword: String?, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                // 1. Actualizar el nombre de usuario en Firestore si no está en blanco.
                // NOTA: No usamos .await() para evitar que la corrutina se quede congelada si Firestore está offline o tiene problemas de sincronización en red.
                // El SDK de Firestore aplica el cambio inmediatamente en la base de datos local (caché offline) y lo sube en segundo plano.
                if (newUsername.isNotBlank()) {
                    FirebaseFirestore.getInstance().collection("usuarios").document(uid)
                        .update("username", newUsername)
                    _state.value = _state.value.copy(username = newUsername)
                }

                // 2. Actualizar la contraseña en Firebase Auth si se proporciona
                if (!newPassword.isNullOrBlank()) {
                    if (newPassword.length < 6) {
                        throw Exception("La contraseña debe tener al menos 6 caracteres")
                    }
                    FirebaseAuth.getInstance().currentUser?.updatePassword(newPassword)?.await()
                }

                _state.value = _state.value.copy(isLoading = false)
                onSuccess()
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false)
                val friendlyMsg = when {
                    "requires recent authentication" in (e.message ?: "") -> 
                        "Por seguridad, esta acción requiere que vuelvas a iniciar sesión antes de cambiar tu contraseña."
                    else -> e.message ?: "Error al actualizar la cuenta"
                }
                onFailure(friendlyMsg)
            }
        }
    }

    fun loadCatalog() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, libraryConsulted = true)
            repo.getAllCharacters(page = 1)
                .onSuccess { list ->
                    _state.value = _state.value.copy(
                        catalog = list,
                        characters = list,
                        isLoading = false,
                        isSearching = false
                    )
                }
                .onFailure { e ->
                    _state.value = _state.value.copy(
                        error = e.message ?: "Error al cargar el catálogo",
                        isLoading = false
                    )
                }
        }
    }

    fun search(name: String) {
        if (name.isBlank()) {
            _state.value = _state.value.copy(
                characters = _state.value.catalog, 
                isSearching = false,
                libraryConsulted = _state.value.catalog.isNotEmpty()
            )
            return
        }
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, isSearching = true)
            repo.searchCharacters(name)
                .onSuccess { list ->
                    _state.value = _state.value.copy(characters = list, isLoading = false)
                }
                .onFailure { e ->
                    _state.value = _state.value.copy(
                        error = e.message ?: "Error al buscar personaje",
                        isLoading = false
                    )
                }
        }
    }

    fun clearSearch() {
        _state.value = _state.value.copy(
            characters = _state.value.catalog,
            isSearching = false,
            error = null
        )
    }

    fun resetToInitialState() {
        val currentUsername = _state.value.username
        val currentName = _state.value.name
        _state.value = SearchUiState(username = currentUsername, name = currentName)
    }
}
