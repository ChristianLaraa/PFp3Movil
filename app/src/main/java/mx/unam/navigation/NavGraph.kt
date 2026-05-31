package mx.unam.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.auth.FirebaseAuth
import mx.unam.ui.screens.LoginScreen
import mx.unam.ui.screens.RegisterScreen
import mx.unam.ui.screens.SearchScreen

@Composable
fun NavGraph(navController: NavHostController) {

    // Si ya hay sesión activa, salta directo al buscador
    val start = if (FirebaseAuth.getInstance().currentUser != null)
        Screen.Search.route else Screen.Login.route

    NavHost(navController = navController, startDestination = start) {

        // ── Login ──────────────────────────────────────────────────────────────
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Search.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onGoToRegister = { navController.navigate(Screen.Register.route) }
            )
        }

        // ── Register ───────────────────────────────────────────────────────────
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    // Al registrarse con éxito, regresamos al Login
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        // ── Search ─────────────────────────────────────────────────────────────
        composable(Screen.Search.route) {
            SearchScreen(
                onLogout = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Search.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
