package com.example.sgrh.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sgrh.data.remote.ApiService
import com.example.sgrh.data.remote.Informe
import com.example.sgrh.data.remote.InformeRequest
import kotlinx.coroutines.launch

// 🔹 Pantalla principal que cambia entre gestión y detalle
@Composable
fun InformesScreen(
    empresaId: String,
    apiService: ApiService,
    onVolverMenu: () -> Unit
) {
    var informeSeleccionado by remember { mutableStateOf<Informe?>(null) }

    if (informeSeleccionado == null) {
        GestionInformes(
            empresaId = empresaId,
            apiService = apiService,
            onRevisar = { informe -> informeSeleccionado = informe },
            onVolver = onVolverMenu
        )
    } else {
        DetalleInforme(
            informe = informeSeleccionado!!,
            onVolver = { informeSeleccionado = null }
        )
    }
}

@Composable
fun GestionInformes(
    empresaId: String,
    apiService: ApiService,
    onRevisar: (Informe) -> Unit,
    onVolver: () -> Unit
) {
    var informes by remember { mutableStateOf<List<Informe>>(emptyList()) }
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    // 🔹 Cargar informes al iniciar
    LaunchedEffect(empresaId) {
        cargarInformes(apiService, empresaId) { result, msg ->
            informes = result
            mensaje = msg
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 🔹 Encabezado
        item {
            Text("Gestión de Informes", style = MaterialTheme.typography.headlineSmall)

            mensaje?.let {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = it,
                    color = if (it.contains("✅")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }

            Spacer(Modifier.height(16.dp))
        }

        // 🔹 Formulario
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre del informe") },
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

                    Button(onClick = {
                        if (nombre.isNotBlank()) {
                            scope.launch {
                                try {
                                    val request = InformeRequest(nombre, descripcion, empresaId)
                                    val response = apiService.crearInforme(request)
                                    if (response.isSuccessful) {
                                        mensaje = "✅ Informe creado correctamente"
                                        nombre = ""
                                        descripcion = ""
                                        cargarInformes(apiService, empresaId) { result, msg ->
                                            informes = result
                                            mensaje = msg ?: mensaje
                                        }
                                    } else {
                                        mensaje = "❌ Error al crear informe"
                                    }
                                } catch (e: Exception) {
                                    mensaje = "❌ Error: ${e.message}"
                                }
                            }
                        } else {
                            mensaje = "❌ El nombre es obligatorio"
                        }
                    }) {
                        Text("Crear Informe")
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            Text("Lista de Informes", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
        }

        // 🔹 Lista de informes
        if (informes.isEmpty()) {
            item {
                Text(
                    "No hay informes registrados para esta empresa",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(informes) { informe ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(informe.nombre, style = MaterialTheme.typography.bodyLarge)
                            Text("Fecha: ${informe.createdAt}", style = MaterialTheme.typography.bodySmall)
                        }
                        Row {
                            OutlinedButton(onClick = { onRevisar(informe) }) {
                                Text("Consultar")
                            }
                            Spacer(Modifier.width(8.dp))
                            OutlinedButton(
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.error
                                ),
                                onClick = {
                                    scope.launch {
                                        try {
                                            val response = apiService.eliminarInforme(informe._id)
                                            if (response.isSuccessful) {
                                                cargarInformes(apiService, empresaId) { result, msg ->
                                                    informes = result
                                                    mensaje = msg ?: "✅ Informe eliminado"
                                                }
                                            } else {
                                                mensaje = "❌ Error al eliminar informe"
                                            }
                                        } catch (e: Exception) {
                                            mensaje = "❌ Error: ${e.message}"
                                        }
                                    }
                                }
                            ) {
                                Text("Eliminar")
                            }
                        }
                    }
                }
            }

            // 🔹 Botón eliminar todos
            item {
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = {
                        scope.launch {
                            try {
                                val response = apiService.eliminarInformesPorEmpresa(empresaId)
                                if (response.isSuccessful) {
                                    informes = emptyList()
                                    mensaje = "✅ Todos los informes eliminados"
                                } else {
                                    mensaje = "❌ Error al eliminar informes"
                                }
                            } catch (e: Exception) {
                                mensaje = "❌ Error: ${e.message}"
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Eliminar Todos")
                }
            }
        }

        // 🔹 Botón volver
        item {
            Spacer(Modifier.height(16.dp))
            OutlinedButton(onClick = onVolver, modifier = Modifier.fillMaxWidth()) {
                Text("← Volver al Menú")
            }
        }
    }
}

// 🔹 Pantalla de Detalles del Informe
@Composable
fun DetalleInforme(
    informe: Informe,
    onVolver: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Detalles del Informe", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(16.dp))

        Text("Nombre:", style = MaterialTheme.typography.titleMedium)
        Text(informe.nombre, style = MaterialTheme.typography.bodyLarge)

        Spacer(Modifier.height(8.dp))

        Text("Descripción:", style = MaterialTheme.typography.titleMedium)
        Text(
            if (informe.descripcion.isNullOrBlank()) "Sin descripción"
            else informe.descripcion,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(Modifier.height(8.dp))

        Text("Fecha de creación:", style = MaterialTheme.typography.titleMedium)
        Text(informe.createdAt, style = MaterialTheme.typography.bodyLarge)

        Spacer(Modifier.height(24.dp))

        OutlinedButton(onClick = onVolver, modifier = Modifier.fillMaxWidth()) {
            Text("← Volver")
        }
    }
}

// 🔹 Función auxiliar para cargar informes
private suspend fun cargarInformes(
    apiService: ApiService,
    empresaId: String,
    onResult: (List<Informe>, String?) -> Unit
) {
    try {
        val response = apiService.getInformesPorEmpresa(empresaId)
        if (response.isSuccessful) {
            val lista = response.body() ?: emptyList()
            val ordenados = lista.sortedByDescending { it.createdAt }
            onResult(ordenados, "✅ Historial cargado (${ordenados.size}) informes")
        } else {
            onResult(emptyList(), "❌ Error al cargar informes")
        }
    } catch (e: Exception) {
        onResult(emptyList(), "❌ Error: ${e.message}")
    }
}
