package mx.unam.data.api

import mx.unam.data.model.Character
import mx.unam.data.model.CharacterResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DragonBallApiService {

    // GET /api/characters?name=goku
    @GET("characters")
    suspend fun searchCharacters(
        @Query("name") name: String
    ): List<Character>

    // GET /api/characters/1
    @GET("characters/{id}")
    suspend fun getCharacterById(
        @Path("id") id: Int
    ): Character
}
