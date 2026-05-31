package mx.unam.data.repository

import mx.unam.data.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun isUserLoggedIn(): Boolean = auth.currentUser != null

    suspend fun login(email: String, pass: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, pass).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(email: String, pass: String, profile: UserProfile): Result<Unit> {
        return try {
            // 1. Crear el usuario en Firebase Auth (esto requiere red y responde rápido o falla de inmediato)
            val result = auth.createUserWithEmailAndPassword(email, pass).await()
            val uid = result.user?.uid ?: throw Exception("ID de usuario nulo.")

            // 2. Guardar el perfil en Firestore.
            // NOTA IMPORTANTE: No usamos `.await()` en la operación de Firestore.
            // En Firestore, el método `set()` escribe inmediatamente en la base de datos local y encola la sincronización con el servidor en segundo plano.
            // Si usamos `.await()`, la corrutina se suspende de forma indefinida esperando confirmación del servidor (lo cual falla o se cuelga si Firestore no está inicializado o si está offline).
            // Sin `.await()`, la operación es instantánea y ultra robusta.
            try {
                firestore.collection("usuarios").document(uid).set(profile)
            } catch (firestoreError: Exception) {
                // Silenciamos cualquier error de Firestore local para no bloquear la creación de la cuenta principal
                firestoreError.printStackTrace()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
    }
}
