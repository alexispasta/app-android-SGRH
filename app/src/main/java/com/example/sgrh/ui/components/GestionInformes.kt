package com.example.sgrh.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

// Modelo de datos (ajústalo al backend)
data class Informe(
    val _id: String,
    val nombre: String,
    val descripcion: String?,
    val createdAt: String
)

@Composable
fun GestionInformes(
    informes: List<Informe>,
    onCrearInforme: (String, String) -> Unit,
    onRevisar: (Informe) -> Unit,
    onVolver: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
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
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
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

                Button(
                    onClick = {
                        if (nombre.isNotBlank()) {
                            scope.launch {
                                onCrearInforme(nombre, descripcion)
                                mensaje = "✅ Informe creado correctamente"
                                nombre = ""
                                descripcion = ""
                            }
                        } else {
                            mensaje = "❌ El nombre es obligatorio"
                        }
                    }
                ) {
                    Text("Crear Informe")
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // 🔹 Tabla de informes
        Text("Lista de Informes", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        if (informes.isEmpty()) {
            Text("No hay informes registrados para esta empresa", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            LazyColumn {
                items(informes) { informe ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
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
                            Button(
                                onClick = { onRevisar(informe) },
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Text("Revisar")
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
