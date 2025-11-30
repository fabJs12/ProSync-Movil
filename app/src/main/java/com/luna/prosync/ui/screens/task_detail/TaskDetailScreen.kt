package com.luna.prosync.ui.screens.task_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.RadioButtonChecked
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.luna.prosync.data.remote.dto.CommentDto
import com.luna.prosync.data.remote.dto.UserProjectDto
import com.luna.prosync.ui.screens.project_detail.TaskStatus
import com.luna.prosync.ui.theme.DarkBlue
import com.luna.prosync.ui.theme.InputBackground
import com.luna.prosync.ui.theme.TextLabel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.Locale
import java.util.TimeZone


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    viewModel: TaskDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf(TaskStatus.PENDING) }
    var selectedMember by remember { mutableStateOf<UserProjectDto?>(null) }
    var dueDate by remember { mutableStateOf("Sin fecha") }
    var showDatePicker by remember { mutableStateOf(false) }
    var newCommentText by remember { mutableStateOf("") }

    val todayMillis = remember {
        val now = LocalDate.now()
        val instant = now.atStartOfDay(ZoneId.systemDefault()).toInstant()
        instant.toEpochMilli()
    }

    LaunchedEffect(uiState.task, uiState.members) {
        uiState.task?.let { task ->
            title = task.title
            description = task.description ?: ""
            selectedStatus = TaskStatus.fromId(task.estadoId ?: task.estado?.id)
            dueDate = task.dueDate?.take(10) ?: "Sin fecha"

            if (task.responsableId != null && uiState.members.isNotEmpty()) {
                selectedMember = uiState.members.find { it.usuario.id == task.responsableId }
            }
        }
    }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onNavigateBack()
            viewModel.onNavigationDone()
        }
    }

    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= todayMillis
            }
        }
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            formatter.timeZone = TimeZone.getTimeZone("UTC")
                            dueDate = formatter.format(Date(millis))
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Detalle de Tarea",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkBlue
                    )
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = TextLabel)
                    }
                }
                Divider(color = Color(0xFFE2E8F0), thickness = 1.dp)
            }
        },
        bottomBar = {
            Column {
                if (uiState.error != null && uiState.task != null) {
                    Text(
                        text = uiState.error!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                    )
                }
                Divider(color = Color(0xFFE2E8F0), thickness = 1.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = onNavigateBack,
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = InputBackground,
                            contentColor = DarkBlue
                        ),
                        elevation = ButtonDefaults.buttonElevation(0.dp)
                    ) {
                        Text("Cerrar", fontWeight = FontWeight.Medium)
                    }

                    Button(
                        onClick = {
                            val isoDate = if (dueDate != "Sin fecha" && dueDate.isNotBlank()) {
                                if (dueDate.contains("T")) dueDate else "${dueDate}T12:00:00Z"
                            } else null

                            val responsableId = selectedMember?.usuario?.id
                            viewModel.saveTaskChanges(title, description, selectedStatus.id, isoDate, responsableId)
                        },
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkBlue,
                            contentColor = Color.White
                        ),
                        enabled = !uiState.isLoading
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Guardar Cambios", fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading && uiState.task == null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = DarkBlue)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Título", style = MaterialTheme.typography.labelMedium, color = TextLabel)
                    OutlinedTextField(
                        value = title,
                        onValueChange = { if (uiState.isLeader) title = it },
                        readOnly = !uiState.isLeader,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = DarkBlue
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = Color.Transparent
                        ),
                        placeholder = { Text("Sin título") }
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Descripción", style = MaterialTheme.typography.labelMedium, color = TextLabel)
                    OutlinedTextField(
                        value = description,
                        onValueChange = { if (uiState.isLeader) description = it },
                        readOnly = !uiState.isLeader,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = Color.Transparent
                        ),
                        placeholder = { Text("Añadir una descripción...") }
                    )
                }

                Divider(color = Color(0xFFE2E8F0))

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Estado", style = MaterialTheme.typography.labelMedium, color = TextLabel)

                    TaskStatus.entries.forEach { status ->
                        StatusOption(
                            status = status,
                            isSelected = selectedStatus == status,
                            onClick = { selectedStatus = status }
                        )
                    }
                }

                Divider(color = Color(0xFFE2E8F0))

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Responsable Asignado", style = MaterialTheme.typography.labelMedium, color = TextLabel)

                    if (uiState.members.isEmpty()) {
                        Text("No hay miembros en este proyecto.", style = MaterialTheme.typography.bodySmall)
                    } else {
                        uiState.members.forEach { memberRel ->
                            MemberOption(
                                member = memberRel,
                                isSelected = selectedMember == memberRel,
                                onClick = { if (uiState.isLeader) selectedMember = memberRel }
                            )
                        }
                    }
                }

                Divider(color = Color(0xFFE2E8F0))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Fecha de Entrega", style = MaterialTheme.typography.labelMedium, color = TextLabel)
                    Box {
                        OutlinedTextField(
                            value = if (dueDate.contains("T")) dueDate.take(10) else dueDate,
                            onValueChange = { },
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            trailingIcon = { if (uiState.isLeader) Icon(Icons.Default.DateRange, contentDescription = null) },
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color(0xFFE2E8F0),
                                focusedBorderColor = DarkBlue
                            )
                        )
                        if (uiState.isLeader) {
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clickable { showDatePicker = true }
                            )
                        }
                    }
                }

                Divider(color = Color(0xFFE2E8F0))

                Text(
                    text = "Comentarios (${uiState.comments.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DarkBlue,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                if (uiState.comments.isEmpty()) {
                    Text("No hay comentarios aún.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                } else {
                    uiState.comments.forEach { comment ->
                        CommentItem(comment)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = newCommentText,
                        onValueChange = { newCommentText = it },
                        placeholder = { Text("Escribe un comentario...") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFE2E8F0),
                            focusedBorderColor = DarkBlue
                        )
                    )
                    IconButton(
                        onClick = {
                            if (newCommentText.isNotBlank()) {
                                viewModel.addComment(newCommentText)
                                newCommentText = "" // Limpiar input
                            }
                        },
                        enabled = newCommentText.isNotBlank()
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Enviar", tint = if(newCommentText.isNotBlank()) DarkBlue else Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun CommentItem(comment: CommentDto) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(Color(0xFFE2E8F0), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            val initial = comment.user?.username?.take(1)?.uppercase() ?: "?"
            Text(text = initial, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextLabel)
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = comment.user?.username ?: "Usuario",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = DarkBlue
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = comment.createdAt?.take(10) ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }

            Text(
                text = comment.contenido,
                style = MaterialTheme.typography.bodyMedium,
                color = TextLabel
            )
        }
    }
}

@Composable
fun StatusOption(status: TaskStatus, isSelected: Boolean, onClick: () -> Unit) {
    val borderColor = if (isSelected) DarkBlue else Color(0xFFE2E8F0)
    val borderWidth = if (isSelected) 2.dp else 1.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(borderWidth, borderColor, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Surface(
            color = status.bgColor,
            shape = RoundedCornerShape(4.dp)
        ) {
            Text(
                text = status.label,
                color = status.color,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
        Icon(
            imageVector = if (isSelected) Icons.Outlined.RadioButtonChecked else Icons.Outlined.Circle,
            contentDescription = null,
            tint = if (isSelected) DarkBlue else Color.Gray
        )
    }
}

@Composable
fun MemberOption(member: UserProjectDto, isSelected: Boolean, onClick: () -> Unit) {
    val borderColor = if (isSelected) DarkBlue else Color(0xFFE2E8F0)
    val borderWidth = if (isSelected) 2.dp else 1.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(borderWidth, borderColor, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFFE2E8F0), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            val initials = member.usuario.username.take(2).uppercase()
            Text(text = initials, color = TextLabel, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = member.usuario.username, fontWeight = FontWeight.Medium, color = DarkBlue)
            Text(text = member.rol.rol, style = MaterialTheme.typography.bodySmall, color = TextLabel)
        }

        if (isSelected) {
            Icon(
                imageVector = Icons.Outlined.RadioButtonChecked,
                contentDescription = null,
                tint = DarkBlue
            )
        }
    }
}