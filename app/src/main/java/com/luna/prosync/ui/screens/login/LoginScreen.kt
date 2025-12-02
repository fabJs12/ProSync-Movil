package com.luna.prosync.ui.screens.login

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.luna.prosync.ui.theme.DarkBlue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.IntentSenderRequest
import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import com.luna.prosync.ui.screens.login.LoginViewModel
import com.luna.prosync.data.remote.GoogleAuthClient
import com.luna.prosync.data.remote.SignInResult

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navigateToProjects by viewModel.navigateToProjects.collectAsStateWithLifecycle()

    LaunchedEffect(navigateToProjects) {
        if (navigateToProjects) {
            onLoginSuccess()
            viewModel.onNavigationDone()
        }
    }

    val context = LocalContext.current
    val googleAuthClient = remember { GoogleAuthClient(context) }
    val scope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        android.util.Log.d("GOOGLE_LOGIN", "Result Code: ${result.resultCode}")
        if (result.resultCode == Activity.RESULT_OK) {
            scope.launch {
                try {
                    val signInResult = googleAuthClient.signInWithIntent(result.data ?: return@launch)
                    android.util.Log.d("GOOGLE_LOGIN", "SignInResult: ${signInResult.errorMessage} Token: ${signInResult.idToken}")
                    signInResult.idToken?.let { token ->
                        viewModel.loginWithGoogle(token)
                    } ?: run {
                        viewModel.onGoogleSignInError(signInResult.errorMessage ?: "Error desconocido de Google")
                    }
                } catch (e: Exception) {
                    android.util.Log.e("GOOGLE_LOGIN", "Exception in launcher", e)
                    viewModel.onGoogleSignInError("Error en Google Sign-In: ${e.message}")
                }
            }
        } else {
             android.util.Log.e("GOOGLE_LOGIN", "Result not OK: ${result.resultCode}")
             viewModel.onGoogleSignInError("Google Sign-In cancelado o fallido (Código: ${result.resultCode})")
        }
    }

    if (uiState.showUsernameDialog) {
        var newUsername by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = viewModel::onDismissUsernameDialog,
            title = { Text("Elige un nombre de usuario") },
            text = {
                Column {
                    Text("Es tu primera vez aquí. Por favor elige un nombre de usuario.")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newUsername,
                        onValueChange = { newUsername = it },
                        label = { Text("Usuario") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.onGoogleUsernameSubmit(newUsername) },
                    enabled = newUsername.isNotBlank()
                ) {
                    Text("Continuar")
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::onDismissUsernameDialog) {
                    Text("Cancelar")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 48.dp)
        ) {
            Surface(
                color = DarkBlue,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(40.dp)
            ) {
                // Aquí iría tu icono dentro del cuadro
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "ProSync",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = DarkBlue
            )
        }

        Text(
            text = "Bienvenido de nuevo",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 8.dp)
        )
        Text(
            text = "Ingresa tus credenciales para continuar.",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = uiState.username,
            onValueChange = viewModel::onUsernameChange,
            label = { Text("Usuario") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = DarkBlue,
                unfocusedBorderColor = Color(0xFFE2E8F0),
                focusedLabelColor = DarkBlue
            ),
            singleLine = true,
            isError = uiState.error != null
        )

        Spacer(modifier = Modifier.height(16.dp))

        var passwordVisible by remember { mutableStateOf(false) }
        OutlinedTextField(
            value = uiState.password,
            onValueChange = viewModel::onPasswordChange,
            label = { Text("Contraseña") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(image, contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = DarkBlue,
                unfocusedBorderColor = Color(0xFFE2E8F0),
                focusedLabelColor = DarkBlue
            ),
            singleLine = true,
            isError = uiState.error != null
        )

        if (uiState.error != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Error,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = uiState.error!!,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = viewModel::onLoginClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = DarkBlue
            ),
            enabled = !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Iniciar Sesión", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = {
                scope.launch {
                    val signInIntentSender = googleAuthClient.signIn()
                    launcher.launch(
                        IntentSenderRequest.Builder(
                            signInIntentSender ?: return@launch
                        ).build()
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                painter = androidx.compose.ui.res.painterResource(id = com.luna.prosync.R.drawable.ic_google_logo),
                contentDescription = "Google Logo",
                modifier = Modifier.size(24.dp),
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Iniciar con Google", color = Color.Black)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("¿No tienes una cuenta?", color = Color.Gray, fontSize = 14.sp)
            TextButton(onClick = onNavigateToRegister) {
                Text("Regístrate", color = DarkBlue, fontWeight = FontWeight.Bold)
            }
        }
    }
}