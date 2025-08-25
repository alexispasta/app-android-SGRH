// 📂 ui/pages/registro/RegistrarPersonaScreen.kt
package com.example.sgrh.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL

@OptIn(ExperimentalMaterial3Api::class) // ✅ Optamos por usar APIs experimentales de Material3
@Composable
fun RegistrarPersonaScreen(
    onVolver: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var rol by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var ciudad by remember { mutableStateOf("") }

    var mensaje by remember { mutableStateOf("") }

    val scroll = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scroll)
    ) {
        Text(
            text = "Registrar Persona",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombres") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = apellido,
            onValueChange = { apellido = it },
            label = { Text("Apellidos") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo Electrónico") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = telefono,
            onValueChange = { telefono = it },
            label = { Text("Teléfono") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = direccion,
            onValueChange = { direccion = it },
            label = { Text("Dirección") },
            modifier = Modifier.fillMaxWidth()
        )

        // Selector de rol (API experimental de Material3)
        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
            OutlinedTextField(
                value = rol,
                onValueChange = {},
                readOnly = true,
                label = { Text("Rol de Empresa") },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                listOf("empleado", "rrhh", "gerente", "supervisor").forEach {
                    DropdownMenuItem(
                        text = { Text(it.replaceFirstChar { c -> c.uppercase() }) },
                        onClick = {
                            rol = it
                            expanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = fechaNacimiento,
            onValueChange = { fechaNacimiento = it },
            label = { Text("Fecha de Nacimiento (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = ciudad,
            onValueChange = { ciudad = it },
            label = { Text("Ciudad") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        // Botón registrar
        Button(
            onClick = {
                mensaje = ""
                val persona = mapOf(
                    "nombre" to nombre,
                    "apellido" to apellido,
                    "email" to email,
                    "password" to password,
                    "telefono" to telefono,
                    "direccion" to direccion,
                    "rol" to rol,
                    "fechaNacimiento" to fechaNacimiento,
                    "ciudad" to ciudad,
                    "empresaId" to "1" // ⚠️ Aquí deberías traer el empresaId guardado en SharedPreferences o tu sesión
                )

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val url = URL("http://10.0.2.2:3000/api/personas") // localhost para Android
                        val conn = url.openConnection() as HttpURLConnection
                        conn.requestMethod = "POST"
                        conn.setRequestProperty("Content-Type", "application/json")
                        conn.doOutput = true

                        val body = buildJson(persona)
                        conn.outputStream.use { os ->
                            os.write(body.toByteArray())
                        }

                        if (conn.responseCode == 200 || conn.responseCode == 201) {
                            mensaje = "✅ Persona registrada correctamente"
                            // limpiar campos
                            nombre = ""; apellido = ""; email = ""; password = ""
                            telefono = ""; direccion = ""; rol = ""; fechaNacimiento = ""; ciudad = ""
                        } else {
                            mensaje = "❌ Error al registrar persona"
                        }
                    } catch (e: Exception) {
                        mensaje = "❌ ${e.message}"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar Persona")
        }

        Spacer(Modifier.height(12.dp))

        if (mensaje.isNotEmpty()) {
            Text(
                text = mensaje,
                color = if (mensaje.startsWith("✅")) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.error
            )
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("← Volver al Menú")
        }
    }
}

// 🔹 Función helper para crear JSON sin librerías externas
fun buildJson(data: Map<String, String>): String {
    return data.entries.joinToString(prefix = "{", postfix = "}") {
        "\"${it.key}\":\"${it.value}\""
    }
}
