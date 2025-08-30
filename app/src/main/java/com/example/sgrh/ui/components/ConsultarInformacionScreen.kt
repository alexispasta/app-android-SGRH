package com.example.sgrh.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sgrh.ui.models.Empleado
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

@Composable
fun ConsultarInformacionScreen(
    onVolver: () -> Unit,
    usuarioId: String = "1"
) {
    var empleado by remember { mutableStateOf<Empleado?>(null) }
    var loading by remember { mutableStateOf(true) }
    var editando by remember { mutableStateOf(false) }
    var formData by remember { mutableStateOf(Empleado("", "", "", "", "", "", "", "", "", "")) }
    var mensaje by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        cargarEmpleado(usuarioId) { user ->
            empleado = user
            loading = false
        }
    }

    if (loading) {
        Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            Text("Cargando información...")
        }
        return
    }

    if (empleado == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            Text("❌ No se pudo cargar la información del usuario.", color = MaterialTheme.colorScheme.error)
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Información de Cuenta", style = MaterialTheme.typography.headlineSmall)
        Text("Consulta y modifica los datos de tu cuenta.", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(12.dp))

        if (mensaje.isNotEmpty()) {
            Text(mensaje, color = if (mensaje.startsWith("✅")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(12.dp))
        }

        val campos = listOf(
            "nombre" to "Nombres",
            "apellido" to "Apellidos",
            "email" to "Correo Electrónico",
            "telefono" to "Teléfono",
            "direccion" to "Dirección",
            "fecha" to "Fecha de Nacimiento",
            "ciudad" to "Ciudad"
        )

        campos.forEach { (key, label) ->
            val valorActual = when (key) {
                "nombre" -> empleado?.nombre ?: ""
                "apellido" -> empleado?.apellido ?: ""
                "email" -> empleado?.email ?: ""
                "telefono" -> empleado?.telefono ?: ""
                "direccion" -> empleado?.direccion ?: ""
                "fecha" -> empleado?.fecha ?: ""
                "ciudad" -> empleado?.ciudad ?: ""
                else -> ""
            }

            val valorForm = when (key) {
                "nombre" -> formData.nombre ?: ""
                "apellido" -> formData.apellido ?: ""
                "email" -> formData.email ?: ""
                "telefono" -> formData.telefono ?: ""
                "direccion" -> formData.direccion ?: ""
                "fecha" -> formData.fecha ?: ""
                "ciudad" -> formData.ciudad ?: ""
                else -> ""
            }

            OutlinedTextField(
                value = valorForm,
                onValueChange = {
                    formData = when (key) {
                        "nombre" -> formData.copy(nombre = it)
                        "apellido" -> formData.copy(apellido = it)
                        "email" -> formData.copy(email = it)
                        "telefono" -> formData.copy(telefono = it)
                        "direccion" -> formData.copy(direccion = it)
                        "fecha" -> formData.copy(fecha = it)
                        "ciudad" -> formData.copy(ciudad = it)
                        else -> formData
                    }
                },
                label = { Text("$label (Actual: $valorActual)") },
                enabled = editando,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            )
        }

        Spacer(Modifier.height(12.dp))
        Text("Rol: ${empleado?.rol ?: ""}")
        Text("Código de empresa: ${empleado?.codigo ?: ""}")

        Spacer(Modifier.height(20.dp))

        Row {
            if (!editando) {
                Button(onClick = { editando = true }) {
                    Text("Editar")
                }
            } else {
                Button(onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        val actualizado = actualizarEmpleado(usuarioId, empleado!!, formData)
                        if (actualizado != null) {
                            empleado = actualizado
                            formData = Empleado("", "", "", "", "", "", "", "", "", "")
                            mensaje = "✅ Información actualizada correctamente."
                            editando = false
                        } else {
                            mensaje = "❌ Error al actualizar la información."
                        }
                    }
                }) {
                    Text("Guardar Cambios")
                }

                Spacer(Modifier.width(8.dp))

                OutlinedButton(onClick = {
                    editando = false
                    formData = Empleado("", "", "", "", "", "", "", "", "", "")
                }) {
                    Text("Cancelar")
                }
            }

            Spacer(Modifier.width(8.dp))

            OutlinedButton(onClick = onVolver) {
                Text("Volver")
            }
        }
    }
}

// 🔹 Funciones de carga y actualización usando Empleado
fun cargarEmpleado(usuarioId: String, onResult: (Empleado?) -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val url = URL("http://10.0.2.2:3000/api/personas/$usuarioId")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            val response = conn.inputStream.bufferedReader().readText()
            val obj = JSONObject(response)

            val empleado = Empleado(
                _id = obj.optString("_id"),
                nombre = obj.optString("nombre"),
                apellido = obj.optString("apellido"),
                email = obj.optString("email"),
                telefono = obj.optString("telefono"),
                direccion = obj.optString("direccion"),
                ciudad = obj.optString("ciudad"),
                fecha = obj.optString("fecha").take(10),
                rol = obj.optString("rol"),
                codigo = obj.optString("codigo")
            )
            onResult(empleado)
        } catch (e: Exception) {
            onResult(null)
        }
    }
}

fun actualizarEmpleado(usuarioId: String, empleado: Empleado, formData: Empleado): Empleado? {
    return try {
        val actualizado = empleado.copy(
            nombre = formData.nombre ?: empleado.nombre,
            apellido = formData.apellido ?: empleado.apellido,
            email = formData.email ?: empleado.email,
            telefono = formData.telefono ?: empleado.telefono,
            direccion = formData.direccion ?: empleado.direccion,
            ciudad = formData.ciudad ?: empleado.ciudad,
            fecha = formData.fecha ?: empleado.fecha
        )

        val url = URL("http://10.0.2.2:3000/api/personas/$usuarioId")
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "PUT"
        conn.setRequestProperty("Content-Type", "application/json")
        conn.doOutput = true

        val json = JSONObject().apply {
            put("nombre", actualizado.nombre)
            put("apellido", actualizado.apellido)
            put("email", actualizado.email)
            put("telefono", actualizado.telefono)
            put("direccion", actualizado.direccion)
            put("ciudad", actualizado.ciudad)
            put("fecha", actualizado.fecha)
            put("rol", actualizado.rol)
            put("codigo", actualizado.codigo)
        }

        conn.outputStream.use { os ->
            os.write(json.toString().toByteArray())
        }

        if (conn.responseCode in 200..299) actualizado else null
    } catch (e: Exception) {
        null
    }
}
