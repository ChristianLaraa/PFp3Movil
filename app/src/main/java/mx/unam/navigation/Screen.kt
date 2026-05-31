package mx.unam.navigation

sealed class Screen(val route: String) {
    object Login    : Screen("login")
    object Register : Screen("register")
    object Search   : Screen("search")
}
