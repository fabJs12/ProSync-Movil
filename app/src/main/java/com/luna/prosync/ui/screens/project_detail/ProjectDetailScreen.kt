package com.luna.prosync.ui.screens.project_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.luna.prosync.data.remote.dto.TaskDto
import com.luna.prosync.ui.theme.DarkBlue
import com.luna.prosync.ui.theme.LightGray

enum class TaskStatus(val id: Int, val label: String, val color: Color, val bgColor: Color) {
    PENDING(1, "Pendiente", Color(0xFF64748B), Color(0xFFFFFFFF)),     // Gris / Blanco
    IN_PROGRESS(3, "En Progreso", Color(0xFF3B82F6), Color(0xFFEFF6FF)), // Azul / Azul muy claro
    DONE(2, "Hecho", Color(0xFF22C55E), Color(0xFFF0FDF4));            // Verde / Verde muy claro

    companion object {
        fun fromId(id: Int?): TaskStatus = entries.find { it.id == id } ?: PENDING
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    viewModel: ProjectDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onCreateTask: (Int) -> Unit,
    onTaskClick: (Int) -> Unit,
    onTeamClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.initializeProjectBoard()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Tablero del Proyecto", fontWeight = FontWeight.Bold)
                        if (uiState.selectedBoard != null) {
                            Text(
                                text = uiState.selectedBoard!!.name,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = onTeamClick) {
                        Icon(Icons.Default.Group, contentDescription = "Equipo")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            if (uiState.selectedBoard != null) {
                ExtendedFloatingActionButton(
                    onClick = { onCreateTask(uiState.selectedBoard!!.id) },
                    containerColor = DarkBlue,
                    contentColor = Color.White,
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Nueva Tarea") }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(LightGray)
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = DarkBlue)
            } else if (uiState.error != null) {
                Text("Error: ${uiState.error}", modifier = Modifier.align(Alignment.Center))
            } else if (uiState.selectedBoard != null) {

                KanbanBoard(
                    tasks = uiState.tasks,
                    onAddTaskClick = { onCreateTask(uiState.selectedBoard!!.id) },
                    onTaskClick = onTaskClick
                )
            }
        }
    }
}

@Composable
fun KanbanBoard(
    tasks: List<TaskDto>,
    onAddTaskClick: () -> Unit,
    onTaskClick: (Int) -> Unit
) {
   val tasksByStatus = remember(tasks) {
       tasks.groupBy { task ->
           TaskStatus.fromId(task.estadoId ?: task.estado?.id)
       }
   }

    LazyRow(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(TaskStatus.entries.toTypedArray()) { status ->
            val columnTasks = tasksByStatus[status] ?: emptyList()

            KanbanColumn(
                status = status,
                tasks = columnTasks,
                modifier = Modifier.width(300.dp)
                    .fillMaxHeight(),
                onTaskClick = onTaskClick,
                onAddTaskClick = onAddTaskClick
            )
        }
    }
}

@Composable
fun KanbanColumn(
    status: TaskStatus,
    tasks: List<TaskDto>,
    onAddTaskClick: () -> Unit,
    onTaskClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(Color.Transparent)
    ) {
        KanbanColumnHeader(status, tasks.size)
        Spacer(modifier = Modifier.height(12.dp))
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(tasks) { task ->
                KanbanTaskCard(
                    task = task,
                    onClick = { onTaskClick(task.id) }
                )
            }
            item {
                Button(
                    onClick = onAddTaskClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = DarkBlue
                    ),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    elevation = ButtonDefaults.buttonElevation(0.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Agregar tarea")
                }
            }
        }
    }
}

@Composable
fun KanbanColumnHeader(status: TaskStatus, count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(status.bgColor, RoundedCornerShape(8.dp))
            .border(1.dp, status.color.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(status.color)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = status.label,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = DarkBlue
            )
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.defaultMinSize(minWidth = 20.dp)
            ) {
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = status.color,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun KanbanTaskCard(
    task: TaskDto,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = DarkBlue
            )
            if (!task.description.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF64748B),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                val fechaFormateada = task.dueDate?.take(10) ?: "Sin fecha"
                Text(
                    text = "Vence: $fechaFormateada",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (task.dueDate != null) Color(0xFF64748B) else Color(0xFFCBD5E1)
                )
            }
        }
    }
}