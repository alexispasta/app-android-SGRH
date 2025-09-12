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
import com.example.sgrh.data.remote.ApiService
import com.example.sgrh.data.remote.Permiso
import com.example.sgrh.data.remote.PermisoRequest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PermisosEmpleadoScreen(
    apiService: ApiService,
    empleadoId: String,
    empleadoNombre: String,
    empresaId: String,
    onVolver: () -> Unit
) {
    var asunto by remember { mutableStateOf("") }
    var razon by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var historial by remember { mutableStateOf<List<Permiso>>(emptyList()) }

    val scope = rememberCoroutineScope()
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

    // 🔹 Función para cargar historial de permisos
    fun cargarHistorial() {
        scope.launch {
            try {
                val response = apiService.getPermisosPorEmpleado(empleadoId)
                if (response.isSuccessful) {
                    historial = response.body() ?: emptyList()
                    println("📌 Permisos recibidos: ${response.body()}")
                } else {
                    mensaje = "❌ Error al cargar historial: ${response.code()}"
                }
            } catch (e: Exception) {
                mensaje = "❌ ${e.message}"
            }
        }
    }

    // 🔹 Cargar historial al iniciar
    LaunchedEffect(empleadoId) {
        cargarHistorial()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // 🔹 Formulario de solicitud
        Column(
            modifier = Modifier
                .fillMaxWidth()
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
                        if (asunto.isNotBlank()) {
                            mensaje = ""
                            scope.launch {
                                try {
                                    val request = PermisoRequest(
                                        personaId = empleadoId,
                                        personaNombre = empleadoNombre,
                                        motivo = asunto,
                                        descripcion = razon,
                                        empresaId = empresaId
                                    )
                                    val response = apiService.crearPermiso(request)
                                    if (response.isSuccessful) {
                                        mensaje = "✅ Solicitud enviada correctamente"
                                        asunto = ""
                                        razon = ""
                                        cargarHistorial() // 🔹 recargar historial
                                    } else {
                                        mensaje = "❌ Error al enviar la solicitud: ${response.code()}"
                                    }
                                } catch (e: Exception) {
                                    mensaje = "❌ ${e.message}"
                                }
                            }
                        } else {
                            mensaje = "❌ El asunto es obligatorio"
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
        }

        Spacer(Modifier.height(20.dp))

        // 🔹 Historial de permisos del empleado
        Text(
            "Historial de Solicitudes",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(Modifier.height(8.dp))

        if (historial.isEmpty()) {
            Text(
                "No tienes solicitudes registradas",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // 🔹 importante para ocupar el espacio restante
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(historial) { p ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = p.motivo, style = MaterialTheme.typography.bodyLarge)
                            Text(text = p.descripcion ?: "", style = MaterialTheme.typography.bodyMedium)

                            Spacer(Modifier.height(4.dp))

                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Fecha: ${
                                        try {
                                            dateFormat.format(
                                                SimpleDateFormat(
                                                    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                                                    Locale.getDefault()
                                                ).parse(p.createdAt)
                                            )
                                        } catch (e: Exception) {
                                            p.createdAt
                                        }
                                    }",
                                    style = MaterialTheme.typography.bodySmall
                                )

                                val estadoColor = when (p.estado.lowercase(Locale.getDefault())) {
                                    "pendiente" -> MaterialTheme.colorScheme.tertiary
                                    "aprobado" -> MaterialTheme.colorScheme.primary
                                    "rechazado" -> MaterialTheme.colorScheme.error
                                    else -> MaterialTheme.colorScheme.onSurface
                                }

                                Text(
                                    text = p.estado.uppercase(),
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
}
