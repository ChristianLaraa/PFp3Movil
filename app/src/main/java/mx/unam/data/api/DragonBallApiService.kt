package mx.unam.data.api

import mx.unam.data.model.Character
import mx.unam.data.model.CharacterResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DragonBallApiService {

    // Búsqueda por nombre: devuelve una lista directa [ ... ]
    @GET("characters")
    suspend fun searchCharacters(
        @Query("name") name: String
    ): List<Character>

    // Catálogo completo: devuelve objeto paginado { "items": [ ... ], "meta": { ... } }
    @GET("characters")
    suspend fun getAllCharacters(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50
    ): CharacterResponse

    // Detalle de personaje
    @GET("characters/{id}")
    suspend fun getCharacterById(
        @Path("id") id: Int
    ): Character
}
