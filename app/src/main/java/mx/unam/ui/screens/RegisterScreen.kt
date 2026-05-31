package mx.unam.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess : () -> Unit,
    onBack            : () -> Unit,
    viewModel         : AuthViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val density = LocalDensity.current
    
    // Detección de teclado para escalado dinámico (Requiere Edge-to-Edge habilitado)
    val isKeyboardVisible = WindowInsets.ime.getBottom(density) > 0
    val scrollState = rememberScrollState()

    // Factores de escalado dinámico para maximizar visibilidad
    val topSpacerHeight by animateDpAsState(if (isKeyboardVisible) 4.dp else 24.dp)
    val cardPadding by animateDpAsState(if (isKeyboardVisible) 14.dp else 24.dp)
    val fieldArrangement by animateDpAsState(if (isKeyboardVisible) 6.dp else 14.dp)
    val horizontalMargin by animateDpAsState(if (isKeyboardVisible) 12.dp else 24.dp)
    val buttonHeight by animateDpAsState(if (isKeyboardVisible) 42.dp else 52.dp)

    // Alerta de éxito
    if (state.isRegisterSuccess) {
        AlertDialog(
            onDismissRequest = { },
            icon = { 
                Icon(
                    imageVector = Icons.Default.CheckCircle, 
                    contentDescription = null, 
                    tint = Color(0xFF4CAF50), 
                    modifier = Modifier.size(64.dp)
                ) 
            },
            title = { 
                Text(
                    "¡REGISTRO COMPLETADO!", 
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                ) 
            },
            text = { 
                Text(
                    "Tu cuenta se ha creado correctamente en Firebase. Ahora serás redirigido al inicio de sesión.",
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    modifier = Modifier.fillMaxWidth()
                ) 
            },
            confirmButton = {
                Button(
                    onClick = { 
                        viewModel.resetState() // Limpiamos el estado
                        onRegisterSuccess()    // Navegamos al login
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("ACEPTAR E IR AL LOGIN", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Campos del formulario
    var nombre         by remember { mutableStateOf("") }
    var apellidoP      by remember { mutableStateOf("") }
    var apellidoM      by remember { mutableStateOf("") }
    var username       by remember { mutableStateOf("") }
    var email          by remember { mutableStateOf("") }
    var password       by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear cuenta", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor    = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .imePadding() // Redimensiona el área disponible al activar el teclado
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(topSpacerHeight))

            Text(
                text  = "Ingresa tus datos de guerrero",
                style = if (isKeyboardVisible) MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold) else MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = horizontalMargin)
            )

            Spacer(Modifier.height(topSpacerHeight))

            Card(
                modifier  = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalMargin),
                shape     = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(cardPadding),
                    verticalArrangement = Arrangement.spacedBy(fieldArrangement)
                ) {

                    // ── Nombre ─────────────────────────────────────────────────
                    OutlinedTextField(
                        value         = nombre,
                        onValueChange = { nombre = it },
                        label         = { Text("Nombre(s)") },
                        leadingIcon   = { Icon(Icons.Default.Person, null) },
                        singleLine    = true,
                        modifier      = Modifier.fillMaxWidth(),
                        shape         = RoundedCornerShape(12.dp)
                    )

                    // ── Apellidos ──────────────────────────────────────────────
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value         = apellidoP,
                            onValueChange = { apellidoP = it },
                            label         = { Text("A. Paterno") },
                            modifier      = Modifier.weight(1f),
                            shape         = RoundedCornerShape(12.dp),
                            singleLine    = true
                        )
                        OutlinedTextField(
                            value         = apellidoM,
                            onValueChange = { apellidoM = it },
                            label         = { Text("A. Materno") },
                            modifier      = Modifier.weight(1f),
                            shape         = RoundedCornerShape(12.dp),
                            singleLine    = true
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.padding(vertical = 2.dp))

                    // ── Username ────────────────────────────────────────────────
                    OutlinedTextField(
                        value         = username,
                        onValueChange = { username = it },
                        label         = { Text("Usuario") },
                        leadingIcon   = { Icon(Icons.Default.AlternateEmail, null) },
                        singleLine    = true,
                        modifier      = Modifier.fillMaxWidth(),
                        shape         = RoundedCornerShape(12.dp)
                    )

                    // ── Email ───────────────────────────────────────────────────
                    OutlinedTextField(
                        value           = email,
                        onValueChange   = { email = it },
                        label           = { Text("Correo electrónico") },
                        leadingIcon     = { Icon(Icons.Default.Email, null) },
                        singleLine      = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier        = Modifier.fillMaxWidth(),
                        shape           = RoundedCornerShape(12.dp)
                    )

                    // ── Password ────────────────────────────────────────────────
                    OutlinedTextField(
                        value                = password,
                        onValueChange        = { password = it },
                        label                = { Text("Contraseña") },
                        leadingIcon          = { Icon(Icons.Default.Lock, null) },
                        trailingIcon         = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible)
                                        Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null
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

                    // ── Error ───────────────────────────────────────────────────
                    AnimatedVisibility(visible = state.error != null) {
                        Text(
                            text     = state.error ?: "",
                            color    = MaterialTheme.colorScheme.error,
                            style    = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }

                    // ── Botón registrar ─────────────────────────────────────────
                    Button(
                        onClick  = {
                            viewModel.register(
                                nombre, apellidoP, apellidoM, username, email, password
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(buttonHeight),
                        shape   = RoundedCornerShape(12.dp),
                        enabled = !state.isLoading
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier    = Modifier.size(20.dp),
                                color       = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("CREAR CUENTA", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}
