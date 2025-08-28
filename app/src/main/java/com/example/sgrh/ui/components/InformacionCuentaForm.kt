// components/InformacionCuentaForm.kt
package com.example.sgrh.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

// Modelo de Usuario
data class Usuario(
    var nombre: String = "",
    var apellido: String = "",
    var email: String = "",
    var telefono: String = "",
    var direccion: String = "",
    var ciudad: String = "",
    var fecha: String = "",
    var rol: String = "",
    var codigo: String = ""
)

@Composable
fun InformacionCuentaForm(
    usuarioId: String, // ⚠️ pásalo desde tu pantalla de login
    onBack: () -> Unit
) {
    var usuario by remember { mutableStateOf<Usuario?>(null) }
    var formData by remember { mutableStateOf(Usuario()) }
    var loading by remember { mutableStateOf(true) }
    var editando by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    // 🔹 Cargar información del usuario
    LaunchedEffect(usuarioId) {
        if (usuarioId == null) {
            mensaje = "❌ No se encontró usuario logueado."
            loading = false
            return@LaunchedEffect
        }

        scope.launch(Dispatchers.IO) {
            try {
                val url = URL("http://10.0.2.2:3000/api/personas/$usuarioId")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"

                if (conn.responseCode == 200) {
                    val response = conn.inputStream.bufferedReader().readText()
                    val json = JSONObject(response)

                    val userData = Usuario(
                        nombre = json.optString("nombre", ""),
                        apellido = json.optString("apellido", ""),
                        email = json.optString("email", ""),
                        telefono = json.optString("telefono", ""),
                        direccion = json.optString("direccion", ""),
                        ciudad = json.optString("ciudad", ""),
                        fecha = json.optString("fecha", "").take(10),
                        rol = json.optString("rol", ""),
                        codigo = json.optString("codigo", "")
                    )

                    usuario = userData
                    formData = Usuario() // inputs vacíos inicialmente
                } else {
                    mensaje = "❌ Error al obtener información"
                }
            } catch (e: Exception) {
                mensaje = "❌ ${e.message}"
            } finally {
                loading = false
            }
        }
    }

    if (loading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (usuario == null) {
        Text("No se pudo cargar la información del usuario.", color = MaterialTheme.colorScheme.error)
        return
    }

    val campos = listOf(
        "nombre" to "Nombres",
        "apellido" to "Apellidos",
        "email" to "Correo electrónico",
        "telefono" to "Teléfono",
        "direccion" to "Dirección",
        "fecha" to "Fecha de nacimiento",
        "ciudad" to "Ciudad"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Información de Cuenta", style = MaterialTheme.typography.headlineSmall)
        Text("Consulta y modifica los datos de tu cuenta.", style = MaterialTheme.typography.bodyMedium)

        if (mensaje.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            Text(mensaje, color = MaterialTheme.colorScheme.primary)
        }

        Spacer(Modifier.height(16.dp))

        campos.forEach { (clave, label) ->
            val valorActual = when (clave) {
                "nombre" -> usuario!!.nombre
                "apellido" -> usuario!!.apellido
                "email" -> usuario!!.email
                "telefono" -> usuario!!.telefono
                "direccion" -> usuario!!.direccion
                "fecha" -> usuario!!.fecha
                "ciudad" -> usuario!!.ciudad
                else -> ""
            }

            OutlinedTextField(
                value = when (clave) {
                    "nombre" -> formData.nombre
                    "apellido" -> formData.apellido
                    "email" -> formData.email
                    "telefono" -> formData.telefono
                    "direccion" -> formData.direccion
                    "fecha" -> formData.fecha
                    "ciudad" -> formData.ciudad
                    else -> ""
                },
                onValueChange = { nuevo ->
                    formData = when (clave) {
                        "nombre" -> formData.copy(nombre = nuevo)
                        "apellido" -> formData.copy(apellido = nuevo)
                        "email" -> formData.copy(email = nuevo)
                        "telefono" -> formData.copy(telefono = nuevo)
                        "direccion" -> formData.copy(direccion = nuevo)
                        "fecha" -> formData.copy(fecha = nuevo)
                        "ciudad" -> formData.copy(ciudad = nuevo)
                        else -> formData
                    }
                },
                label = { Text("$label (actual: $valorActual)") },
                placeholder = { Text("Nuevo $label") },
                enabled = editando,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            )
        }

        // Rol y Código solo lectura
        Spacer(Modifier.height(12.dp))
        Text("Rol: ${usuario!!.rol}")
        Text("Código de empresa: ${usuario!!.codigo}")

        Spacer(Modifier.height(20.dp))

        Row {
            if (!editando) {
                Button(onClick = { editando = true }) {
                    Text("Editar")
                }
            } else {
                Button(
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            try {
                                val datosActualizados = usuario!!.copy(
                                    nombre = if (formData.nombre.isNotBlank()) formData.nombre else usuario!!.nombre,
                                    apellido = if (formData.apellido.isNotBlank()) formData.apellido else usuario!!.apellido,
                                    email = if (formData.email.isNotBlank()) formData.email else usuario!!.email,
                                    telefono = if (formData.telefono.isNotBlank()) formData.telefono else usuario!!.telefono,
                                    direccion = if (formData.direccion.isNotBlank()) formData.direccion else usuario!!.direccion,
                                    ciudad = if (formData.ciudad.isNotBlank()) formData.ciudad else usuario!!.ciudad,
                                    fecha = if (formData.fecha.isNotBlank()) formData.fecha else usuario!!.fecha
                                )

                                val url = URL("http://10.0.2.2:3000/api/personas/$usuarioId")
                                val conn = url.openConnection() as HttpURLConnection
                                conn.requestMethod = "PUT"
                                conn.setRequestProperty("Content-Type", "application/json")
                                conn.doOutput = true

                                val jsonBody = JSONObject()
                                jsonBody.put("nombre", datosActualizados.nombre)
                                jsonBody.put("apellido", datosActualizados.apellido)
                                jsonBody.put("email", datosActualizados.email)
                                jsonBody.put("telefono", datosActualizados.telefono)
                                jsonBody.put("direccion", datosActualizados.direccion)
                                jsonBody.put("ciudad", datosActualizados.ciudad)
                                jsonBody.put("fecha", datosActualizados.fecha)

                                conn.outputStream.use { os ->
                                    os.write(jsonBody.toString().toByteArray())
                                }

                                if (conn.responseCode == 200) {
                                    usuario = datosActualizados
                                    mensaje = "✅ Información actualizada correctamente."
                                    editando = false
                                    formData = Usuario() // reset inputs
                                } else {
                                    mensaje = "❌ Error al actualizar"
                                }
                            } catch (e: Exception) {
                                mensaje = "❌ ${e.message}"
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Guardar Cambios")
                }
                Spacer(Modifier.width(12.dp))
                Button(
                    onClick = {
                        editando = false
                        formData = Usuario()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                ) {
                    Text("Cancelar")
                }
            }

            Spacer(Modifier.width(12.dp))
            OutlinedButton(onClick = onBack) {
                Text("Volver")
            }
        }
    }
}
