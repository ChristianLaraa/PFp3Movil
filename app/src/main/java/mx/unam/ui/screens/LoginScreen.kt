package mx.unam.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LoginScreen(
    onLoginSuccess : () -> Unit,
    onGoToRegister : () -> Unit,
    viewModel      : AuthViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    // Navegar cuando el login fue exitoso
    LaunchedEffect(state.success) {
        if (state.success) onLoginSuccess()
    }

    var email          by remember { mutableStateOf("") }
    var password       by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // ── Encabezado ─────────────────────────────────────────────────────
            Text(
                text       = "🐉",
                fontSize   = 64.sp,
                textAlign  = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text      = "Dragon Ball App",
                style     = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color     = MaterialTheme.colorScheme.primary
            )
            Text(
                text  = "Inicia sesión para continuar",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(40.dp))

            // ── Tarjeta de formulario ──────────────────────────────────────────
            Card(
                modifier  = Modifier.fillMaxWidth(),
                shape     = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    // Email
                    OutlinedTextField(
                        value         = email,
                        onValueChange = { email = it },
                        label         = { Text("Correo electrónico") },
                        leadingIcon   = { Icon(Icons.Default.Email, contentDescription = null) },
                        singleLine    = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier      = Modifier.fillMaxWidth(),
                        shape         = RoundedCornerShape(12.dp)
                    )

                    // Password
                    OutlinedTextField(
                        value                = password,
                        onValueChange        = { password = it },
                        label                = { Text("Contraseña") },
                        leadingIcon          = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon         = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible)
                                        Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (passwordVisible) "Ocultar" else "Mostrar"
                                )
                            }
                        },
                        singleLine           = true,
                        visualTransformation = if (passwordVisible)
                            VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions      = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier             = Modifier.fillMaxWidth(),
                        shape                = RoundedCornerShape(12.dp)
                    )

                    // Mensaje de error
                    AnimatedVisibility(visible = state.error != null) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text     = state.error ?: "",
                                color    = MaterialTheme.colorScheme.onErrorContainer,
                                style    = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }

                    // Botón login
                    Button(
                        onClick  = { viewModel.login(email, password) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape    = RoundedCornerShape(12.dp),
                        enabled  = !state.isLoading
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color    = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Iniciar sesión", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Ir a registro
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    "¿No tienes cuenta?",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
                TextButton(onClick = onGoToRegister) {
                    Text(
                        "Regístrate",
                        fontWeight = FontWeight.Bold,
                        color      = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}
