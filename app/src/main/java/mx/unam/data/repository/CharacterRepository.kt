package mx.unam.data.repository

import mx.unam.data.api.RetrofitInstance
import mx.unam.data.model.Character

class CharacterRepository {

    private val api = RetrofitInstance.api

    suspend fun searchCharacters(name: String): Result<List<Character>> {
        return try {
            val response = api.searchCharacters(name.trim())
            // Como api.searchCharacters devuelve List<Character>, pasamos response directamente
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
