package com.example.sgrh.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sgrh.data.remote.*
import kotlinx.coroutines.launch

@Composable
fun GestionReportes(
    empresaId: String,
    apiService: ApiService,
    onVolver: () -> Unit
) {
    var empleados by remember { mutableStateOf<List<EmpleadoReporte>>(emptyList()) }
    var empleadoSeleccionado by remember { mutableStateOf<EmpleadoReporte?>(null) }
    var asunto by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf<String?>(null) }
    var historial by remember { mutableStateOf<List<Reporte>>(emptyList()) }

    val scope = rememberCoroutineScope()

    // 🔹 Cargar empleados y reportes al inicio
    LaunchedEffect(empresaId) {
        try {
            val respEmpleados = apiService.getEmpleados(empresaId)
            if (respEmpleados.isSuccessful) {
                empleados = respEmpleados.body()?.map { e ->
                    EmpleadoReporte(
                        _id = e._id,
                        nombre = e.nombre,
                        apellido = e.apellido,
                        codigo = e.codigo ?: ""
                    )
                } ?: emptyList()
            } else {
                mensaje = "Error al cargar empleados ❌"
            }

            val respReportes = apiService.getReportesPorEmpresa(empresaId)
            if (respReportes.isSuccessful) {
                historial = respReportes.body() ?: emptyList()
            } else {
                mensaje = "Error al cargar reportes ❌"
            }
        } catch (e: Exception) {
            mensaje = "Error: ${e.message}"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Gestión de Reportes", style = MaterialTheme.typography.headlineSmall)

        mensaje?.let {
            Spacer(Modifier.height(8.dp))
            Text(
                text = it,
                color = if (it.contains("✅")) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.error
            )
        }

        Spacer(Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxSize()) {
            // --- Columna izquierda: Selección de empleado / nuevo reporte ---
            Column(modifier = Modifier.weight(1f)) {
                if (empleadoSeleccionado == null) {
                    Text("Empleados", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))

                    if (empleados.isEmpty()) {
                        Text("No hay empleados disponibles", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        LazyColumn {
                            items(empleados) { emp ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    onClick = { empleadoSeleccionado = emp }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("${emp.nombre} ${emp.apellido} (${emp.codigo})")
                                        Text("➕")
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text("Nuevo reporte para ${empleadoSeleccionado!!.nombre}")
                        Spacer(Modifier.height(8.dp))

                        OutlinedTextField(
                            value = asunto,
                            onValueChange = { asunto = it },
                            label = { Text("Asunto") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))

                        OutlinedTextField(
                            value = descripcion,
                            onValueChange = { descripcion = it },
                            label = { Text("Descripción") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 4
                        )
                        Spacer(Modifier.height(8.dp))

                        Row {
                            Button(
                                onClick = {
                                    if (empleadoSeleccionado != null &&
                                        asunto.isNotBlank() &&
                                        descripcion.isNotBlank()
                                    ) {
                                        scope.launch {
                                            try {
                                                val request = ReporteRequest(
                                                    asunto = asunto,
                                                    descripcion = descripcion,
                                                    empleadoId = empleadoSeleccionado!!._id,
                                                    empresaId = empresaId
                                                )
                                                val response = apiService.crearReporte(request)
                                                if (response.isSuccessful) {
                                                    mensaje = "Reporte enviado correctamente ✅"
                                                    empleadoSeleccionado = null
                                                    asunto = ""
                                                    descripcion = ""
                                                    // 🔹 actualizar historial con el nuevo reporte
                                                    response.body()?.let { nuevo ->
                                                        historial = listOf(nuevo) + historial
                                                    }
                                                } else {
                                                    mensaje = "Error al enviar reporte ❌"
                                                }
                                            } catch (e: Exception) {
                                                mensaje = "Error: ${e.message}"
                                            }
                                        }
                                    } else {
                                        mensaje = "Completa todos los campos ❌"
                                    }
                                }
                            ) {
                                Text("Guardar Informe")
                            }
                            Spacer(Modifier.width(8.dp))
                            OutlinedButton(onClick = { empleadoSeleccionado = null }) {
                                Text("Cancelar")
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.width(16.dp))

            // --- Columna derecha: Historial de reportes ---
            Column(modifier = Modifier.weight(1f)) {
                Text("Historial de Reportes", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))

                if (historial.isEmpty()) {
                    Text(
                        "No hay reportes registrados",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyColumn {
                        items(historial) { r ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Column(Modifier.padding(12.dp)) {
                                    Text(r.asunto, style = MaterialTheme.typography.titleMedium)
                                    Text("Empleado: ${r.empleadoId.nombre} ${r.empleadoId.apellido}")
                                    Text("Fecha: ${r.createdAt}")
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        OutlinedButton(onClick = onVolver) {
            Text("Volver")
        }
    }
}
