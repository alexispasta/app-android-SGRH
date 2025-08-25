package com.example.sgrh.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MenuOpcionesSupervisor(onSeleccionar: (String) -> Unit) {
    val opciones = listOf(
        "asistencia" to "Gestión de asistencia",
        "informes" to "Gestión de informes",
        "reportes" to "Gestión de reportes",
        "permisos" to "Gestión de permisos"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Opciones")

        opciones.forEach { (id, texto) ->
            Button(
                onClick = { onSeleccionar(id) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(texto)
            }
        }
    }
}
