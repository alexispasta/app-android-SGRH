package com.example.sgrh.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

    // 🔹 Función para recargar lista desde el backend
    fun recargarPermisos() {
        if (empresaId.isBlank()) {
            mensaje = "❌ Empresa inválida"
            return
        }
        scope.launch {
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
    }

    // 🔹 Cargar permisos al iniciar
    LaunchedEffect(empresaId) {
        recargarPermisos()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
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
        }

        if (permisos.isEmpty()) {
            item {
                Text(
                    "No hay solicitudes de permisos para esta empresa",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(permisos) { permiso ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            "Empleado: ${permiso.personaNombre}",
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

                        Row {
                            if (permiso.estado == "pendiente") {
                                Button(
                                    onClick = {
                                        if (permiso._id.isBlank()) {
                                            mensaje = "❌ Permiso inválido"
                                            return@Button
                                        }
                                        scope.launch {
                                            try {
                                                val resp = apiService.actualizarPermiso(
                                                    permiso._id,
                                                    mapOf("estado" to "aprobado")
                                                )
                                                if (resp.isSuccessful) {
                                                    mensaje = "Permiso aprobado correctamente"
                                                    recargarPermisos()
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
                                        if (permiso._id.isBlank()) {
                                            mensaje = "❌ Permiso inválido"
                                            return@Button
                                        }
                                        scope.launch {
                                            try {
                                                val resp = apiService.actualizarPermiso(
                                                    permiso._id,
                                                    mapOf("estado" to "rechazado")
                                                )
                                                if (resp.isSuccessful) {
                                                    mensaje = "Permiso rechazado correctamente"
                                                    recargarPermisos()
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

                            Spacer(Modifier.width(8.dp))

                            OutlinedButton(
                                onClick = {
                                    if (permiso._id.isBlank()) {
                                        mensaje = "❌ Permiso inválido"
                                        return@OutlinedButton
                                    }
                                    scope.launch {
                                        try {
                                            val resp = apiService.eliminarPermiso(permiso._id)
                                            if (resp.isSuccessful) {
                                                mensaje = "Permiso eliminado correctamente"
                                                recargarPermisos()
                                            } else {
                                                mensaje = "❌ Error al eliminar: ${resp.code()}"
                                            }
                                        } catch (e: Exception) {
                                            mensaje = "❌ ${e.message}"
                                        }
                                    }
                                },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Text("Eliminar")
                            }
                        }
                    }
                }
            }
        }

        // 🔹 Footer con botones
        item {
            Spacer(Modifier.height(16.dp))

            if (permisos.isNotEmpty()) {
                Button(
                    onClick = {
                        if (empresaId.isBlank()) {
                            mensaje = "❌ Empresa inválida"
                            return@Button
                        }
                        scope.launch {
                            try {
                                val resp = apiService.eliminarTodosPermisos(empresaId)
                                if (resp.isSuccessful) {
                                    permisos = emptyList()
                                    mensaje = "✅ Todos los permisos eliminados"
                                } else {
                                    mensaje = "❌ Error al eliminar todos: ${resp.code()}"
                                }
                            } catch (e: Exception) {
                                mensaje = "❌ ${e.message}"
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("🗑 Eliminar TODOS los permisos")
                }

                Spacer(Modifier.height(8.dp))
            }

            OutlinedButton(
                onClick = onVolver,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("← Volver al Menú")
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
