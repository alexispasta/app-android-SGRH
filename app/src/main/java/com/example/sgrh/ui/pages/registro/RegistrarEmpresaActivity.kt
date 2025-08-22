// RegistrarEmpresaScreen.kt
package com.example.sgrh.ui.pages.registro

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun RegistrarEmpresaScreen(
    onSuccess: () -> Unit = {} // Llama esto para navegar al login cuando “registre”
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // --- Datos Empresa ---
    var nombreEmpresa by remember { mutableStateOf("") }
    var correoEmpresa by remember { mutableStateOf("") }
    var passwordEmpresa by remember { mutableStateOf("") }
    var pais by remember { mutableStateOf("") }
    var telefonoEmpresa by remember { mutableStateOf("") }
    var direccionEmpresa by remember { mutableStateOf("") }

    // --- Datos Gerente ---
    var nombrePersona by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var passwordPersona by remember { mutableStateOf("") }
    var telefonoPersona by remember { mutableStateOf("") }
    var direccionPersona by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") } // yyyy-mm-dd
    var ciudad by remember { mutableStateOf("") }

    fun validarCampos(): Boolean {
        val campos = listOf(
            nombreEmpresa, correoEmpresa, passwordEmpresa, pais, telefonoEmpresa, direccionEmpresa,
            nombrePersona, apellido, email, passwordPersona, telefonoPersona, direccionPersona, fecha, ciudad
        )
        return campos.all { it.isNotBlank() }
    }

    fun onSubmit() {
        if (!validarCampos()) {
            scope.launch { snackbarHostState.showSnackbar("Completa todos los campos") }
            return
        }

        // Aquí más adelante conectamos con el backend (Retrofit/OkHttp).
        // De momento, imitamos éxito como en tu React:
        scope.launch {
            snackbarHostState.showSnackbar("✅ Empresa y gerente registrados correctamente")
            onSuccess()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            Text("Registro Empresa y Gerente", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(16.dp))

            // ----- Empresa -----
            Text("Datos de la Empresa", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = nombreEmpresa, onValueChange = { nombreEmpresa = it },
                label = { Text("Nombre de Empresa") }, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = correoEmpresa, onValueChange = { correoEmpresa = it },
                label = { Text("Correo Empresa") }, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = passwordEmpresa, onValueChange = { passwordEmpresa = it },
                label = { Text("Contraseña Empresa") }, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = pais, onValueChange = { pais = it },
                label = { Text("País") }, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = telefonoEmpresa, onValueChange = { telefonoEmpresa = it },
                label = { Text("Teléfono Empresa") }, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = direccionEmpresa, onValueChange = { direccionEmpresa = it },
                label = { Text("Dirección Empresa") }, modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))
            Divider()
            Spacer(Modifier.height(16.dp))

            // ----- Gerente -----
            Text("Datos del Gerente", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = nombrePersona, onValueChange = { nombrePersona = it },
                label = { Text("Nombres") }, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = apellido, onValueChange = { apellido = it },
                label = { Text("Apellidos") }, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = email, onValueChange = { email = it },
                label = { Text("Correo Electrónico") }, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = passwordPersona, onValueChange = { passwordPersona = it },
                label = { Text("Contraseña") }, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = telefonoPersona, onValueChange = { telefonoPersona = it },
                label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = direccionPersona, onValueChange = { direccionPersona = it },
                label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = fecha, onValueChange = { fecha = it },
                label = { Text("Fecha de Nacimiento (yyyy-mm-dd)") }, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = ciudad, onValueChange = { ciudad = it },
                label = { Text("Ciudad") }, modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))
            Button(
                onClick = { onSubmit() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrar Empresa y Gerente")
            }
        }
    }
}

/* Si quieres un payload listo para la futura llamada HTTP:
data class RegistrarEmpresaPayload(
    val nombreEmpresa: String,
    val correoEmpresa: String,
    val passwordEmpresa: String,
    val pais: String,
    val telefonoEmpresa: String,
    val direccionEmpresa: String,
    val nombrePersona: String,
    val apellido: String,
    val email: String,
    val passwordPersona: String,
    val telefonoPersona: String,
    val direccionPersona: String,
    val fecha: String,
    val ciudad: String
)
*/
