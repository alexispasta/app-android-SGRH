package com.example.sgrh.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sgrh.data.remote.ApiService
import com.example.sgrh.data.remote.Permiso
import kotlinx.coroutines.launch

@Composable
fun GestionPermisos(
    apiService: ApiService,
    empresaId: String,
    onVolver: () -> Unit
) {
    var permisos by remember { mutableStateOf<List<Permiso>>(emptyList()) }
    var mensaje by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    // 🔹 Cargar permisos al iniciar
    LaunchedEffect(empresaId) {
        try {
            val response = apiService.getPermisosPorEmpresa(empresaId)
            if (response.isSuccessful) {
                permisos = response.body() ?: emptyList()
            } else {
                mensaje = "❌ Error al cargar permisos: ${response.code()}"
            }
        } catch (e: Exception) {
            mensaje = "❌ ${e.message}"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()) // Toda la pantalla scrolleable
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
            Text(
                "No hay solicitudes de permisos para esta empresa",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            permisos.forEach { permiso ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            "Empleado: ${permiso.empleadoNombre}",
                            fontWeight = FontWeight.SemiBold
                        )
                        Text("Motivo: ${permiso.motivo}")
                        Text("Fecha: ${permiso.createdAt}")

                        Spacer(Modifier.height(6.dp))

                        val colorEstado = when (permiso.estado) {
                            "pendiente" -> MaterialTheme.colorScheme.tertiary
                            "aprobado" -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.error
                        }
                        Text(
                            "Estado: ${permiso.estado}",
                            color = colorEstado,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(8.dp))

                        if (permiso.estado == "pendiente") {
                            Row {
                                Button(
                                    onClick = {
                                        scope.launch {
                                            try {
                                                val resp = apiService.actualizarPermiso(
                                                    permiso._id,
                                                    mapOf("estado" to "aprobado")
                                                )
                                                if (resp.isSuccessful) {
                                                    mensaje = "Permiso aprobado correctamente"
                                                    permisos = permisos.map {
                                                        if (it._id == permiso._id) it.copy(estado = "aprobado") else it
                                                    }
                                                } else {
                                                    mensaje = "❌ Error al aprobar: ${resp.code()}"
                                                }
                                            } catch (e: Exception) {
                                                mensaje = "❌ ${e.message}"
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Text("Aprobar")
                                }

                                Spacer(Modifier.width(8.dp))

                                Button(
                                    onClick = {
                                        scope.launch {
                                            try {
                                                val resp = apiService.actualizarPermiso(
                                                    permiso._id,
                                                    mapOf("estado" to "rechazado")
                                                )
                                                if (resp.isSuccessful) {
                                                    mensaje = "Permiso rechazado correctamente"
                                                    permisos = permisos.map {
                                                        if (it._id == permiso._id) it.copy(estado = "rechazado") else it
                                                    }
                                                } else {
                                                    mensaje = "❌ Error al rechazar: ${resp.code()}"
                                                }
                                            } catch (e: Exception) {
                                                mensaje = "❌ ${e.message}"
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    Text("Rechazar")
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Botón de volver siempre al final
        OutlinedButton(onClick = onVolver, modifier = Modifier.fillMaxWidth()) {
            Text("← Volver al Menú")
        }
    }
}
