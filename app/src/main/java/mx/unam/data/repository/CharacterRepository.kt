package mx.unam.data.repository

import mx.unam.data.api.RetrofitInstance
import mx.unam.data.model.Character

class CharacterRepository {

    private val api = RetrofitInstance.api

    suspend fun searchCharacters(name: String): Result<List<Character>> {
        return try {
            val response = api.searchCharacters(name.trim())
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllCharacters(page: Int = 1): Result<List<Character>> {
        return try {
            val response = api.getAllCharacters(page = page, limit = 50)
            Result.success(response.items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
