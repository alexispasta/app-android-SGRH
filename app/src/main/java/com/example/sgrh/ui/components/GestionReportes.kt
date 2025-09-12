package com.example.sgrh.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
    var reporteSeleccionado by remember { mutableStateOf<Reporte?>(null) }
    val scope = rememberCoroutineScope()

    // 🔹 Cargar empleados y reportes
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
            .verticalScroll(rememberScrollState())
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

        if (empleadoSeleccionado == null) {
            // 🔹 Selección de empleados
            Text("Empleados", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            if (empleados.isEmpty()) {
                Text("No hay empleados disponibles", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                empleados.forEach { emp ->
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
                            Text("${emp.nombre} ${emp.apellido}")
                            Text("➕")
                        }
                    }
                }
            }
        } else {
            // 🔹 Formulario nuevo reporte
            Text("Nuevo reporte para ${empleadoSeleccionado!!.nombre}", style = MaterialTheme.typography.titleMedium)
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
                        if (empleadoSeleccionado != null && asunto.isNotBlank() && descripcion.isNotBlank()) {
                            scope.launch {
                                try {
                                    val request = ReporteRequest(
                                        asunto = asunto,
                                        descripcion = descripcion,
                                        personaId = empleadoSeleccionado!!._id, // ✅ corregido
                                        empresaId = empresaId
                                    )
                                    val response = apiService.crearReporte(request)
                                    if (response.isSuccessful) {
                                        mensaje = "Reporte enviado correctamente ✅"
                                        empleadoSeleccionado = null
                                        asunto = ""
                                        descripcion = ""
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
                ) { Text("Guardar") }
                Spacer(Modifier.width(8.dp))
                OutlinedButton(onClick = { empleadoSeleccionado = null }) {
                    Text("Cancelar")
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        // 🔹 Historial
        Text("Historial de Reportes", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        if (historial.isEmpty()) {
            Text("No hay reportes registrados", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            historial.forEach { r ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    onClick = { reporteSeleccionado = r }
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(r.asunto, style = MaterialTheme.typography.titleMedium)
                        Text("Empleado: ${r.personaId.nombre} ${r.personaId.apellido}") // ✅ corregido
                        Text("Fecha: ${r.createdAt}")
                        if (reporteSeleccionado == r) {
                            Text("✅ Seleccionado", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            Row {
                Button(
                    onClick = {
                        reporteSeleccionado?.let {
                            mensaje = "📌 Asunto: ${it.asunto}\n📝 ${it.descripcion}"
                        } ?: run { mensaje = "Seleccione un reporte ❌" }
                    },
                    modifier = Modifier.weight(1f)
                ) { Text("Consultar") }

                Spacer(Modifier.width(8.dp))

                Button(
                    onClick = {
                        scope.launch {
                            if (reporteSeleccionado != null) {
                                try {
                                    val res = apiService.eliminarReporte(reporteSeleccionado!!._id)
                                    if (res.isSuccessful) {
                                        historial = historial.filterNot { it._id == reporteSeleccionado!!._id }
                                        reporteSeleccionado = null
                                        mensaje = "Reporte eliminado ✅"
                                    } else {
                                        mensaje = "Error al eliminar ❌"
                                    }
                                } catch (e: Exception) {
                                    mensaje = "Error: ${e.message}"
                                }
                            } else mensaje = "Seleccione un reporte ❌"
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
                ) { Text("Eliminar") }
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    scope.launch {
                        try {
                            val res = apiService.eliminarTodosReportes(empresaId)
                            if (res.isSuccessful) {
                                historial = emptyList()
                                reporteSeleccionado = null
                                mensaje = "Todos los reportes eliminados ✅"
                            } else {
                                mensaje = "Error al eliminar todos ❌"
                            }
                        } catch (e: Exception) {
                            mensaje = "Error: ${e.message}"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
            ) { Text("Eliminar Todos") }
        }

        Spacer(Modifier.height(16.dp))
        OutlinedButton(onClick = onVolver, modifier = Modifier.fillMaxWidth()) {
            Text("Volver")
        }
    }
}

