package mx.unam.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mx.unam.data.model.Character
import mx.unam.data.repository.CharacterRepository

data class SearchUiState(
    val isLoading   : Boolean         = false,
    val characters  : List<Character> = emptyList(),
    val error       : String?         = null,
    val hasSearched : Boolean         = false
)

class SearchViewModel : ViewModel() {

    private val repo = CharacterRepository()

    private val _state = MutableStateFlow(SearchUiState())
    val state: StateFlow<SearchUiState> = _state.asStateFlow()

    fun search(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            _state.value = SearchUiState(isLoading = true)
            repo.searchCharacters(name)
                .onSuccess { list ->
                    _state.value = SearchUiState(characters = list, hasSearched = true)
                }
                .onFailure { e ->
                    _state.value = SearchUiState(
                        error       = e.message ?: "Error al conectar con la API",
                        hasSearched = true
                    )
                }
        }
    }

    fun clearResults() { _state.value = SearchUiState() }
}
