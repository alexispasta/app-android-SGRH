package com.example.sgrh.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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

    // 🔹 Cargar informes desde backend al iniciar
    LaunchedEffect(empresaId) {
        try {
            val response = apiService.getInformesPorEmpresa(empresaId)
            if (response.isSuccessful) {
                informes = response.body() ?: emptyList()
            } else {
                mensaje = "❌ Error al cargar informes"
            }
        } catch (e: Exception) {
            mensaje = "❌ Error: ${e.message}"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()) // Toda la pantalla scrolleable
    ) {
        Text("Gestión de Informes", style = MaterialTheme.typography.headlineSmall)

        mensaje?.let {
            Spacer(Modifier.height(8.dp))
            Text(
                text = it,
                color = if (it.contains("✅")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }

        Spacer(Modifier.height(16.dp))

        // 🔹 Formulario de creación
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
                                    informes = listOf(response.body()!!) + informes
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

        // 🔹 Lista de informes
        Text("Lista de Informes", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        if (informes.isEmpty()) {
            Text(
                "No hay informes registrados para esta empresa",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyColumn(
                modifier = Modifier.heightIn(max = 400.dp) // Limita altura, para no bloquear el scroll completo
            ) {
                items(informes) { informe ->
                    Card(modifier = Modifier
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
                            Button(onClick = { onRevisar(informe) }) {
                                Text("Revisar")
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
