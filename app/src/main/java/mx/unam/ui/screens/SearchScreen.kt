package mx.unam.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import mx.unam.data.model.Character

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onLogout  : () -> Unit,
    viewModel : SearchViewModel = viewModel()
) {
    val state    by viewModel.state.collectAsState()
    var query    by remember { mutableStateOf("") }
    val keyboard = LocalSoftwareKeyboardController.current

    // Diálogo de confirmación de logout
    var showLogoutDialog by remember { mutableStateOf(false) }
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title   = { Text("Cerrar sesión") },
            text    = { Text("¿Estás seguro que deseas salir?") },
            confirmButton = {
                TextButton(onClick = { showLogoutDialog = false; onLogout() }) {
                    Text("Salir", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("Cancelar") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "🐉 Dragon Ball",
                        fontWeight = FontWeight.Bold,
                        fontSize   = 20.sp
                    )
                },
                actions = {
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(
                            Icons.Default.Logout,
                            contentDescription = "Cerrar sesión",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {

            Spacer(Modifier.height(8.dp))

            // ── Barra de búsqueda ──────────────────────────────────────────────
            OutlinedTextField(
                value         = query,
                onValueChange = { query = it },
                label         = { Text("Nombre del personaje") },
                placeholder   = { Text("ej. Goku, Vegeta, Gohan...") },
                leadingIcon   = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon  = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = ""; viewModel.clearResults() }) {
                            Icon(Icons.Default.Clear, contentDescription = "Limpiar")
                        }
                    }
                },
                singleLine      = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    keyboard?.hide()
                    viewModel.search(query)
                }),
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(16.dp)
            )

            Spacer(Modifier.height(8.dp))

            // Botón buscar
            Button(
                onClick  = { keyboard?.hide(); viewModel.search(query) },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape    = RoundedCornerShape(12.dp),
                enabled  = query.isNotBlank() && !state.isLoading
            ) {
                Icon(Icons.Default.Search, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Buscar", fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(16.dp))

            // ── Contenido ──────────────────────────────────────────────────────
            when {
                state.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.height(12.dp))
                            Text("Buscando personaje...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                state.error != null -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("⚠️", fontSize = 48.sp)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text      = state.error!!,
                                color     = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center,
                                style     = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                state.hasSearched && state.characters.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🔍", fontSize = 48.sp)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text      = "No se encontró ningún personaje\nllamado \"$query\"",
                                textAlign = TextAlign.Center,
                                color     = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                !state.hasSearched -> {
                    // Estado inicial — mensaje de bienvenida
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🐲", fontSize = 64.sp)
                            Spacer(Modifier.height(12.dp))
                            Text(
                                text      = "Busca tu personaje favorito\nde Dragon Ball",
                                textAlign = TextAlign.Center,
                                style     = MaterialTheme.typography.bodyLarge,
                                color     = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                else -> {
                    // ── Lista de personajes ────────────────────────────────────
                    Text(
                        text  = "${state.characters.size} resultado(s) encontrado(s)",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))

                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        itemsIndexed(state.characters) { index, character ->
                            AnimatedVisibility(
                                visible = true,
                                enter   = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
                            ) {
                                CharacterCard(character = character)
                            }
                        }
                        item { Spacer(Modifier.height(16.dp)) }
                    }
                }
            }
        }
    }
}

// ── Tarjeta de personaje ──────────────────────────────────────────────────────
@Composable
fun CharacterCard(character: Character) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        onClick   = { expanded = !expanded }
    ) {
        Column {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ── Imagen ─────────────────────────────────────────────────────
                Card(
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier.size(90.dp)
                ) {
                    AsyncImage(
                        model              = character.image,
                        contentDescription = character.name,
                        contentScale       = ContentScale.Crop,
                        modifier           = Modifier.fillMaxSize()
                    )
                }

                Spacer(Modifier.width(14.dp))

                // ── Datos principales ──────────────────────────────────────────
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text       = character.name,
                        style      = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines   = 1,
                        overflow   = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(4.dp))

                    // Badges de raza y género
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        InfoChip(label = character.race)
                        InfoChip(label = character.gender)
                    }

                    Spacer(Modifier.height(6.dp))

                    // Ki base
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⚡ ", fontSize = 13.sp)
                        Text(
                            text  = "Ki: ${character.ki}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    // Ki máximo
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🌟 ", fontSize = 13.sp)
                        Text(
                            text  = "Ki Máx: ${character.maxKi}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }

                // ── Ícono expandir ─────────────────────────────────────────────
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                                  else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // ── Contenido expandible ───────────────────────────────────────────
            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(Modifier.height(4.dp))

                    // Afiliación
                    DetailRow(label = "Afiliación", value = character.affiliation)

                    // Descripción
                    Text(
                        text      = "Descripción",
                        style     = MaterialTheme.typography.labelMedium,
                        color     = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text  = character.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// ── Componente chip para raza/género ──────────────────────────────────────────
@Composable
private fun InfoChip(label: String) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Text(
            text     = label,
            style    = MaterialTheme.typography.labelSmall,
            color    = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

// ── Fila de detalle label: value ──────────────────────────────────────────────
@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text       = label,
            style      = MaterialTheme.typography.labelMedium,
            color      = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text  = value,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
