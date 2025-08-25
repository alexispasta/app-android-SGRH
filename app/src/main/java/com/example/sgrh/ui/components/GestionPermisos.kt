package com.example.sgrh.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// Modelo de datos (ajústalo a lo que devuelva tu API)
data class Permiso(
    val _id: String,
    val empleadoNombre: String?,
    val motivo: String,
    val createdAt: String,
    val estado: String
)

@Composable
fun GestionPermisos(
    permisos: List<Permiso>,
    onAccion: (String, String) -> Unit, // (id, nuevoEstado)
    onVolver: () -> Unit
) {
    var mensaje by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            "Gestión de Permisos",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        mensaje?.let {
            Spacer(Modifier.height(8.dp))
            Text(
                text = it,
                color = if (it.contains("correctamente")) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.error
            )
        }

        Spacer(Modifier.height(16.dp))

        if (permisos.isEmpty()) {
            Text("No hay solicitudes de permisos para esta empresa", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            LazyColumn {
                items(permisos) { permiso ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Empleado: ${permiso.empleadoNombre ?: "Desconocido"}", fontWeight = FontWeight.SemiBold)
                            Text("Motivo: ${permiso.motivo}")
                            Text("Fecha: ${permiso.createdAt}")

                            Spacer(Modifier.height(6.dp))

                            // Estado con estilo
                            val colorEstado = when (permiso.estado) {
                                "pendiente" -> MaterialTheme.colorScheme.tertiary
                                "aprobado" -> MaterialTheme.colorScheme.primary
                                else -> MaterialTheme.colorScheme.error
                            }
                            Text("Estado: ${permiso.estado}", color = colorEstado, fontWeight = FontWeight.Bold)

                            Spacer(Modifier.height(8.dp))

                            if (permiso.estado == "pendiente") {
                                Row {
                                    Button(
                                        onClick = {
                                            onAccion(permiso._id, "aprobado")
                                            mensaje = "Permiso aprobado correctamente"
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                    ) {
                                        Text("Aprobar")
                                    }
                                    Spacer(Modifier.width(8.dp))
                                    Button(
                                        onClick = {
                                            onAccion(permiso._id, "rechazado")
                                            mensaje = "Permiso rechazado correctamente"
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                    ) {
                                        Text("Rechazar")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        OutlinedButton(onClick = onVolver) {
            Text("← Volver al Menú")
        }
    }
}
