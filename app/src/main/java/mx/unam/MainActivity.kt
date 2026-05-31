package mx.unam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import mx.unam.navigation.NavGraph
import mx.unam.ui.theme.DragonBallTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DragonBallTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}
