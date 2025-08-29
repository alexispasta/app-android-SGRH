package com.example.sgrh.ui.pages.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sgrh.R
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    navController: NavController, // Ahora pasamos el NavController
    viewModel: LoginViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var view by remember { mutableStateOf("login") }
    var recoveryEmail by remember { mutableStateOf("") }

    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        // IZQUIERDA
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.mi_logo),
                contentDescription = "Logo",
                modifier = Modifier.size(150.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "SISTEMA DE GESTIÓN DE RECURSOS HUMANOS",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Optimiza el control de tu empresa con nuestra plataforma profesional.",
                style = MaterialTheme.typography.bodySmall
            )
        }

        // DERECHA
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            SnackbarHost(hostState = snackbarHostState)

            when (view) {
                "login" -> {
                    Text("Ingresar", style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(16.dp))

                    if (uiState.error.isNotEmpty()) {
                        LaunchedEffect(uiState.error) {
                            scope.launch {
                                snackbarHostState.showSnackbar(uiState.error)
                            }
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
                                viewModel.login { rol ->
                                    navigateByRole(navController, rol)
                                }
                            }
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            viewModel.login { rol ->
                                navigateByRole(navController, rol)
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

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(onClick = { view = "recuperar" }) {
                            Text("¿Olvidaste tu contraseña?")
                        }
                        TextButton(onClick = { view = "crear" }) {
                            Text("¿Deseas registrar una empresa?")
                        }
                    }
                }

                "crear" -> {
                    Text("Registro de Empresa", style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Si aún no tienes una empresa registrada en el sistema, puedes hacerlo aquí. " +
                                "El primer usuario creado será el Gerente de la empresa."
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { /* TODO: Navegar a registrar empresa */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Registrar Empresa")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(onClick = { view = "login" }) {
                        Text("← Volver")
                    }
                }

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
                        onClick = { /* TODO: Enviar correo de recuperación */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Enviar")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(onClick = { view = "login" }) {
                        Text("← Volver")
                    }
                }
            }
        }
    }
}

fun navigateByRole(navController: NavController, rol: String) {
    when (rol) {
        "Gerente" -> navController.navigate("gerente_screen") {
            popUpTo("login_screen") { inclusive = true }
        }
        "RRHH" -> navController.navigate("rrhh_screen") {
            popUpTo("login_screen") { inclusive = true }
        }
        "Supervisor" -> navController.navigate("supervisor_screen") {
            popUpTo("login_screen") { inclusive = true }
        }
        "Empleado" -> navController.navigate("empleado_screen") {
            popUpTo("login_screen") { inclusive = true }
        }
        else -> { /* no hacer nada o mostrar error */ }
    }
}
