package com.example.sgrh.ui.pages.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sgrh.R
import com.example.sgrh.ui.navigation.navigateByRole
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var view by remember { mutableStateOf("login") }
    var recoveryEmail by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- ENCABEZADO ---
        Image(
            painter = painterResource(id = R.drawable.mi_logo),
            contentDescription = "Logo",
            modifier = Modifier.size(150.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "SISTEMA DE GESTIÓN DE RECURSOS HUMANOS",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            "Optimiza el control de tu empresa con nuestra plataforma profesional.",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        SnackbarHost(hostState = snackbarHostState)

        when (view) {
            // -------- LOGIN --------
            "login" -> {
                Text("Ingresar", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(16.dp))

                if (uiState.error.isNotEmpty()) {
                    LaunchedEffect(uiState.error) {
                        scope.launch { snackbarHostState.showSnackbar(uiState.error) }
                    }
                }

                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = { viewModel.onEmailChange(it) },
                    label = { Text("Correo Electrónico") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = uiState.password,
                    onValueChange = { viewModel.onPasswordChange(it) },
                    label = { Text("Contraseña") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            viewModel.login { rol, userId, empresaId ->
                                navigateByRole(navController, rol, userId, empresaId)
                            }
                        }
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        viewModel.login { rol, userId, empresaId ->
                            navigateByRole(navController, rol, userId, empresaId)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.loading
                ) {
                    if (uiState.loading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ingresando...")
                    } else {
                        Text("Iniciar Sesión")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    TextButton(onClick = { view = "recuperar" }) {
                        Text("¿Olvidaste tu contraseña?")
                    }
                    TextButton(onClick = {
                        // 🔹 Ahora redirige a la pantalla de registrar empresa
                        navController.navigate("registrarEmpresa")
                    }) {
                        Text("¿Deseas registrar una empresa?")
                    }
                }
            }

            // -------- RECUPERAR --------
            "recuperar" -> {
                Text("Recuperar contraseña", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = recoveryEmail,
                    onValueChange = { recoveryEmail = it },
                    label = { Text("Correo Electrónico") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { /* TODO: lógica recuperación */ },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Enviar") }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(onClick = { view = "login" }) {
                    Text("← Volver")
                }
            }
        }
    }
}
