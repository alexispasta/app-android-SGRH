package com.example.sgrh.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

// 🔹 Modelo de datos (renombrado para evitar choque con otros Empleado)
data class EmpleadoReporte(
    val _id: String,
    val nombre: String,
    val apellido: String,
    val codigo: String
)

data class Reporte(
    val _id: String,
    val asunto: String,
    val descripcion: String,
    val empleadoId: String,
    val createdAt: String
)

@Composable
fun GestionReportes(
    empleados: List<EmpleadoReporte>,
    historial: List<Reporte>,
    onEnviarReporte: (String, String, String) -> Unit,
    onVolver: () -> Unit
) {
    var empleadoSeleccionado by remember { mutableStateOf<EmpleadoReporte?>(null) }
    var asunto by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

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
            // Columna izquierda
            Column(modifier = Modifier.weight(1f)) {
                if (empleadoSeleccionado == null) {
                    Text("Empleados", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))

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
                                            onEnviarReporte(
                                                empleadoSeleccionado!!._id,
                                                asunto,
                                                descripcion
                                            )
                                            mensaje = "Reporte enviado correctamente ✅"
                                            empleadoSeleccionado = null
                                            asunto = ""
                                            descripcion = ""
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

            // Columna derecha
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
                                    Text("Empleado: ${r.empleadoId}")
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
