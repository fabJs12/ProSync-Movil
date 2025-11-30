package com.luna.prosync.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Card
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

private data class StatCardInfo(
    val icon: ImageVector,
    val title: String,
    val value: String,
    val change: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onLogout: () -> Unit,
    onNavigateToNotifications: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadDashboardData()
    }

    val statsList = if (uiState.stats != null) {
        val s = uiState.stats!!
        listOf(
            StatCardInfo(
                Icons.Outlined.Folder,
                "Proyectos Activos",
                s.proyectosActivos.toString(),
                formatChange(s.cambioProyectos)
            ),
            StatCardInfo(
                Icons.Outlined.CheckCircle,
                "Tareas Completadas",
                s.tareasCompletadas.toString(),
                formatChange(s.cambioTareas)
            ),
            StatCardInfo(
                Icons.Outlined.People,
                "Miembros Equipo",
                s.miembrosEquipo.toString(),
                formatChange(s.cambioMiembros)
            ),
            StatCardInfo(
                Icons.Outlined.Timer,
                "Días Promedio",
                s.tiempoPromedio.toString(),
                "${if(s.cambioTiempo > 0) "+" else ""}${s.cambioTiempo} vs anterior"
            )
        )
    } else {
        listOf(
            StatCardInfo(Icons.Outlined.Folder, "Proyectos Activos", "-", "..."),
            StatCardInfo(Icons.Outlined.CheckCircle, "Tareas Completadas", "-", "..."),
            StatCardInfo(Icons.Outlined.People, "Miembros Equipo", "-", "..."),
            StatCardInfo(Icons.Outlined.Timer, "Días Promedio", "-", "...")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ProSync", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onNavigateToNotifications) {
                        Icon(Icons.Outlined.Notifications, contentDescription = "Notificaciones")
                    }
                    IconButton(onClick = {
                        viewModel.onLogout()
                        onLogout()
                    }) {
                        Icon(Icons.Outlined.ExitToApp, contentDescription = "Cerrar Sesión")
                    }
                }
            )
        }
    ) { paddingValues ->

        if (uiState.error != null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text(text = "Error: ${uiState.error}", color = MaterialTheme.colorScheme.error )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                item (span = {
                    GridItemSpan(maxLineSpan)
                }){
                    WelcomeHeader(
                        isLoading = uiState.isLoading,
                        username = uiState.user?.username
                    )
                }

                items(statsList) { stat ->
                    StatCard(
                        icon = stat.icon,
                        title = stat.title,
                        value = stat.value,
                        change = stat.change
                    )
                }
            }
        }
    }
}

fun formatChange(value: Int): String {
    return if (value > 0) "+$value este mes" else "$value este mes"
}

@Composable
fun WelcomeHeader(isLoading: Boolean, username: String?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        if (isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        } else {
            Text(
                text = "Bienvenido, ${username ?: "Usuario"}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Aquí tienes el resumen de tu actividad.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun StatCard(icon: ImageVector, title: String, value: String, change: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary)
            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Text(
                text = change,
                style = MaterialTheme.typography.labelSmall,
                color = if (change.startsWith("+")) Color(0xFF22C55E) else Color.Gray,
                fontWeight = FontWeight.Medium
            )
        }
    }
}