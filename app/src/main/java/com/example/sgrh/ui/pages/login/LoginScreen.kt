package com.example.sgrh.ui.pages.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.sgrh.R

@Composable
fun LoginScreen(
    onLoginSuccess: (rol: String) -> Unit,
) {
    var view by remember { mutableStateOf("login") } // 🔹 Controla la vista
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var recoveryEmail by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        // IZQUIERDA - Logo y descripción (ARRIBA en lugar de centrado)
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
                modifier = Modifier.size(150.dp) // un poco más pequeño para dar espacio
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

        // DERECHA - Contenido dinámico
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            when (view) {
                "login" -> {
                    Text("Ingresar", style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(16.dp))

                    if (error.isNotEmpty()) {
                        Text(error, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo Electrónico") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            // Aquí siempre redirige al selector de roles
                            onLoginSuccess("roles")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Iniciar Sesión")
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
                    Text("Si aún no tienes una empresa registrada en el sistema, puedes hacerlo aquí. El primer usuario creado será el Gerente de la empresa.")

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { /* Aquí podrías navegar a registrar empresa */ },
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
                        onClick = { /* Aquí enviarías correo de recuperación */ },
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
