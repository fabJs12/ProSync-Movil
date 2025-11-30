package com.luna.prosync.ui.screens.team

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.luna.prosync.data.remote.dto.UserProjectDto
import com.luna.prosync.ui.theme.DarkBlue
import com.luna.prosync.ui.theme.LightGray
import com.luna.prosync.ui.theme.TextLabel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamScreen(
    viewModel: TeamViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddMemberDialog by remember { mutableStateOf(false) }
    var memberToEdit by remember { mutableStateOf<UserProjectDto?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    // Sort members: Leaders (id=2) first, then others
    val sortedMembers = remember(uiState.members) {
        uiState.members.sortedBy { it.rol.id }
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            snackbarHostState.showSnackbar("Operación exitosa")
            viewModel.onSuccessMessageShown()
            showAddMemberDialog = false
            memberToEdit = null
        }
    }

    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            snackbarHostState.showSnackbar(uiState.error!!)
            viewModel.onSuccessMessageShown()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Equipo del Proyecto", fontWeight = FontWeight.Bold, color = DarkBlue) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddMemberDialog = true },
                containerColor = DarkBlue,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.PersonAdd, contentDescription = null) },
                text = { Text("Añadir Miembro") }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(LightGray)
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (uiState.isLoading && uiState.members.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = DarkBlue)
            } else if (uiState.members.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Group,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No hay miembros en el equipo aún.",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(sortedMembers) { member ->
                        MemberCard(
                            member = member,
                            onEditRole = { memberToEdit = member },
                            onDelete = { /* Implementar borrar si quieres */ }
                        )
                    }
                }
            }
        }

        if (showAddMemberDialog) {
            AddMemberDialog(
                onDismiss = { showAddMemberDialog = false },
                onAddMember = { email -> viewModel.addMember(email) },
                isLoading = uiState.isLoading
            )
        }

        if (memberToEdit != null) {
            ChangeRoleDialog(
                member = memberToEdit!!,
                onDismiss = { memberToEdit = null },
                onRoleSelected = { newRoleId ->
                    viewModel.changeRole(memberToEdit!!.usuario.id, newRoleId)
                }
            )
        }
    }
}

@Composable
fun MemberCard(
    member: UserProjectDto,
    onEditRole: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(Color(0xFFF1F5F9), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                val initials = member.usuario.username.take(2).uppercase()
                Text(
                    text = initials,
                    color = DarkBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = member.usuario.username,
                    fontWeight = FontWeight.Bold,
                    color = DarkBlue,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                RoleBadge(roleName = member.rol.rol, roleId = member.rol.id)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = member.usuario.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(onClick = onEditRole) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Editar Rol",
                    tint = DarkBlue
                )
            }
        }
    }
}

@Composable
fun RoleBadge(roleName: String, roleId: Int) {
    val (bgColor, textColor) = if (roleId == 2) { // Lider
        Pair(Color(0xFFEFF6FF), Color(0xFF3B82F6)) // Azul claro / Azul
    } else {
        Pair(Color(0xFFF1F5F9), Color(0xFF64748B)) // Gris claro / Gris
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp),
    ) {
        Text(
            text = roleName,
            color = textColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun AddMemberDialog(
    onDismiss: () -> Unit,
    onAddMember: (String) -> Unit,
    isLoading: Boolean
) {
    var email by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Añadir al Equipo",
                fontWeight = FontWeight.Bold,
                color = DarkBlue,
                fontSize = 20.sp
            )
        },
        text = {
            Column {
                Text(
                    "Ingresa el correo electrónico del usuario para invitarlo al proyecto.",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo electrónico") },
                    placeholder = { Text("ejemplo@correo.com") },
                    leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = androidx.compose.material3.TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = DarkBlue,
                        cursorColor = DarkBlue
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (email.isNotBlank()) onAddMember(email) },
                colors = ButtonDefaults.buttonColors(containerColor = DarkBlue),
                enabled = !isLoading,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.padding(bottom = 8.dp, end = 8.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Añadir Miembro")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text("Cancelar", color = TextLabel)
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
fun ChangeRoleDialog(
    member: UserProjectDto,
    onDismiss: () -> Unit,
    onRoleSelected: (Int) -> Unit
) {
    var selectedRoleId by remember { mutableStateOf(member.rol.id) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Cambiar Rol",
                fontWeight = FontWeight.Bold,
                color = DarkBlue
            )
        },
        text = {
            Column {
                Text(
                    text = "Selecciona el nuevo rol para ${member.usuario.username}:",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(24.dp))

                RoleSelectionButton(
                    text = "Líder",
                    isSelected = selectedRoleId == 2,
                    onClick = { selectedRoleId = 2 }
                )

                Spacer(modifier = Modifier.height(12.dp))

                RoleSelectionButton(
                    text = "Miembro",
                    isSelected = selectedRoleId == 3,
                    onClick = { selectedRoleId = 3 }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onRoleSelected(selectedRoleId) },
                colors = ButtonDefaults.buttonColors(containerColor = DarkBlue),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.padding(bottom = 8.dp, end = 8.dp)
            ) {
                Text("Guardar Cambios")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.padding(bottom = 8.dp)) {
                Text("Cancelar", color = TextLabel)
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
fun RoleSelectionButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val containerColor = if (isSelected) Color(0xFFEFF6FF) else Color(0xFFF8FAFC)
    val borderColor = if (isSelected) DarkBlue else Color.Transparent
    val textColor = if (isSelected) DarkBlue else Color(0xFF64748B)

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = containerColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        modifier = Modifier.fillMaxWidth().height(50.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = text, fontWeight = FontWeight.SemiBold, color = textColor)
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = DarkBlue,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}