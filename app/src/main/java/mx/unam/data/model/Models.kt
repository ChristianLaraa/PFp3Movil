package mx.unam.data.model

import com.google.gson.annotations.SerializedName

// ── Respuesta de la API (lista paginada para endpoint general) ────────────────
data class CharacterResponse(
    val items : List<Character>,
    val meta  : Meta
)

data class Meta(
    val totalItems   : Int,
    val itemCount    : Int,
    val itemsPerPage : Int,
    val totalPages   : Int,
    val currentPage  : Int
)

// ── Personaje de Dragon Ball ──────────────────────────────────────────────────
data class Character(
    val id          : Int     = 0,
    val name        : String  = "",
    val ki          : String  = "",
    val maxKi       : String  = "",
    val race        : String  = "",
    val gender      : String  = "",
    val description : String  = "",
    val image       : String  = "",
    val affiliation : String  = ""
)

// ── Perfil de usuario guardado en Firestore ───────────────────────────────────
data class UserProfile(
    val uid       : String = "",
    val nombre    : String = "",
    val apellidoP : String = "",
    val apellidoM : String = "",
    val username  : String = "",
    val email     : String = ""
)
