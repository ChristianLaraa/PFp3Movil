package mx.unam.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary          = OrangeDB,
    onPrimary        = Color.White,
    primaryContainer = OrangeLight,
    onPrimaryContainer = Color(0xFF3D1600),
    secondary        = BlueDB,
    onSecondary      = Color.White,
    secondaryContainer = Color(0xFFD7E3FF),
    onSecondaryContainer = Color(0xFF001B3F),
    background       = Color(0xFFFFF8F5),
    onBackground     = Color(0xFF1A1A1A),
    surface          = Color.White,
    onSurface        = Color(0xFF1A1A1A),
    surfaceVariant   = Color(0xFFF5E6D8),
    onSurfaceVariant = Color(0xFF5C3D2E),
    error            = Color(0xFFBA1A1A),
    onError          = Color.White,
    outline          = Color(0xFFB07D5A)
)

private val DarkColorScheme = darkColorScheme(
    primary          = OrangeLight,
    onPrimary        = Color(0xFF4A1800),
    primaryContainer = OrangeDark,
    onPrimaryContainer = Color(0xFFFFDBCC),
    secondary        = Color(0xFFAEC6FF),
    onSecondary      = Color(0xFF002E6A),
    secondaryContainer = BlueDark,
    onSecondaryContainer = Color(0xFFD7E3FF),
    background       = SurfaceDark,
    onBackground     = Color(0xFFE8E0D8),
    surface          = SurfaceCard,
    onSurface        = Color(0xFFE8E0D8),
    surfaceVariant   = Color(0xFF3D2C1E),
    onSurfaceVariant = Color(0xFFD9B99A),
    error            = Color(0xFFFFB4AB),
    onError          = Color(0xFF690005),
    outline          = Color(0xFF9A7D65)
)

@Composable
fun DragonBallTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography(),
        content     = content
    )
}
