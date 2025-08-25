package com.example.sgrh.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

// 🔹 Modelo específico para permisos de empleados
data class PermisoEmpleado(
    val _id: String,
    val motivo: String,
    val descripcion: String,
    val estado: String,
    val createdAt: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermisosEmpleadoScreen(
    onVolver: () -> Unit
) {
    var asunto by remember { mutableStateOf("") }
    var razon by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var historial by remember { mutableStateOf(listOf<PermisoEmpleado>()) }

    // ⚠️ Aquí deberías leer desde DataStore/SharedPreferences
    val empleadoId = "1"
    val empleadoNombre = "Empleado Demo"
    val empresaId = "1"

    // 🔹 Cargar historial al iniciar
    LaunchedEffect(Unit) {
        cargarHistorial(empleadoId) { permisos ->
            historial = permisos
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Solicitud de Permiso", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(12.dp))

        if (mensaje.isNotEmpty()) {
            Text(
                text = mensaje,
                color = if (mensaje.startsWith("✅")) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.error
            )
            Spacer(Modifier.height(12.dp))
        }

        // 🔹 Formulario
        OutlinedTextField(
            value = asunto,
            onValueChange = { asunto = it },
            label = { Text("Asunto del permiso") },
            placeholder = { Text("Ej. Cita médica") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = razon,
            onValueChange = { razon = it },
            label = { Text("Razón del permiso") },
            placeholder = { Text("Explica la razón detalladamente") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        )

        Spacer(Modifier.height(12.dp))

        Row(Modifier.fillMaxWidth()) {
            Button(
                onClick = {
                    mensaje = ""
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val permiso = JSONObject().apply {
                                put("motivo", asunto)
                                put("descripcion", razon)
                                put("estado", "pendiente")
                                put("empleadoId", empleadoId)
                                put("empleadoNombre", empleadoNombre)
                                put("empresaId", empresaId)
                            }

                            val url = URL("http://10.0.2.2:3000/api/permisos")
                            val conn = url.openConnection() as HttpURLConnection
                            conn.requestMethod = "POST"
                            conn.setRequestProperty("Content-Type", "application/json")
                            conn.doOutput = true

                            conn.outputStream.use { os ->
                                os.write(permiso.toString().toByteArray())
                            }

                            if (conn.responseCode in 200..299) {
                                asunto = ""
                                razon = ""
                                mensaje = "✅ Solicitud enviada correctamente"
                                cargarHistorial(empleadoId) { permisos ->
                                    historial = permisos
                                }
                            } else {
                                mensaje = "❌ Error al enviar la solicitud"
                            }
                        } catch (e: Exception) {
                            mensaje = "❌ ${e.message}"
                        }
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Enviar solicitud")
            }

            Spacer(Modifier.width(8.dp))

            OutlinedButton(
                onClick = onVolver,
                modifier = Modifier.weight(1f)
            ) {
                Text("Volver")
            }
        }

        Spacer(Modifier.height(20.dp))

        // 🔹 Historial
        Text("Historial de Solicitudes", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        if (historial.isEmpty()) {
            Text("No tienes solicitudes registradas", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            LazyColumn {
                items(historial) { p ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text("${p.motivo} - ${p.descripcion}")
                                Text("Fecha: ${p.createdAt}", style = MaterialTheme.typography.bodySmall)
                            }
                            val estadoColor = when (p.estado) {
                                "pendiente" -> MaterialTheme.colorScheme.tertiary
                                "aprobado" -> MaterialTheme.colorScheme.primary
                                else -> MaterialTheme.colorScheme.error
                            }
                            Text(
                                text = p.estado,
                                color = estadoColor,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

// 🔹 Función para cargar historial desde API
fun cargarHistorial(empleadoId: String, onResult: (List<PermisoEmpleado>) -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val url = URL("http://10.0.2.2:3000/api/permisos/empleado/$empleadoId")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            val response = conn.inputStream.bufferedReader().readText()
            val arr = JSONArray(response)

            val lista = mutableListOf<PermisoEmpleado>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                lista.add(
                    PermisoEmpleado(
                        _id = obj.getString("_id"),
                        motivo = obj.getString("motivo"),
                        descripcion = obj.getString("descripcion"),
                        estado = obj.getString("estado"),
                        createdAt = obj.getString("createdAt")
                    )
                )
            }
            onResult(lista)
        } catch (e: Exception) {
            onResult(emptyList())
        }
    }
}
