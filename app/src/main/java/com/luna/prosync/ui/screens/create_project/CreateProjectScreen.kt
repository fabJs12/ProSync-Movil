package com.luna.prosync.ui.screens.create_project

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Title
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.luna.prosync.ui.theme.BorderGray
import com.luna.prosync.ui.theme.DarkBlue
import com.luna.prosync.ui.theme.InfoBoxBackground
import com.luna.prosync.ui.theme.InfoBoxBorder
import com.luna.prosync.ui.theme.InputBackground
import com.luna.prosync.ui.theme.TextLabel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProjectScreen(
    viewModel: CreateProjectViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onNavigateBack()
        }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            Column {
                TopAppBar(
                    title = { 
                        Text(
                            "Crear Nuevo Proyecto", 
                            fontWeight = FontWeight.Bold,
                            color = DarkBlue
                        ) 
                    },
                    actions = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = TextLabel)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
                Divider(color = BorderGray, thickness = 1.dp)
            }
        },
        bottomBar = {
            Column {
                Divider(color = BorderGray, thickness = 1.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = InputBackground,
                            contentColor = DarkBlue
                        ),
                        elevation = ButtonDefaults.buttonElevation(0.dp)
                    ) {
                        Text("Cancelar", fontWeight = FontWeight.Medium)
                    }

                    Button(
                        onClick = viewModel::onCreateClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (uiState.name.isNotBlank()) DarkBlue else Color(0xFFCBD5E1),
                            contentColor = Color.White
                        ),
                        enabled = !uiState.isLoading
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Crear Proyecto", fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            InputGroup(
                label = "Título del Proyecto",
                icon = Icons.Outlined.Title,
                value = uiState.name,
                onValueChange = viewModel::onNameChanged,
                placeholder = "Ej: Desarrollo de nueva aplicación móvil",
                isError = uiState.error?.contains("vacío") == true
            )

            InputGroup(
                label = "Descripción",
                icon = Icons.Outlined.Description,
                value = uiState.description,
                onValueChange = viewModel::onDescriptionChanged,
                placeholder = "Describe los objetivos y alcance del proyecto...",
                singleLine = false,
                minLines = 5
            )

            Text(
                text = "${uiState.description.length} caracteres",
                style = MaterialTheme.typography.bodySmall,
                color = TextLabel,
                modifier = Modifier.offset(y = (-16).dp) // Subirlo un poco para pegarlo al campo
            )

            InfoBox(
                text = "Una vez creado el proyecto, podrás agregar miembros del equipo, asignar tareas y establecer fechas de entrega."
            )
        }
    }
}

@Composable
fun InputGroup(
    label: String,
    icon: ImageVector,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    singleLine: Boolean = true,
    minLines: Int = 1,
    isError: Boolean = false
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TextLabel,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = TextLabel,
                fontWeight = FontWeight.Medium
            )
        }

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color(0xFF94A3B8)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = DarkBlue,
                unfocusedBorderColor = BorderGray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                cursorColor = DarkBlue
            ),
            singleLine = singleLine,
            minLines = minLines,
            isError = isError
        )
    }
}

@Composable
fun InfoBox(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = InfoBoxBackground, shape = RoundedCornerShape(8.dp))
            .border(width = 1.dp, color = InfoBoxBorder, shape = RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = DarkBlue,
            lineHeight = 20.sp
        )
    }
}