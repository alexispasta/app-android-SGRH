package com.example.sgrh.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MenuOpcionesGerente(onSeleccionar: (String) -> Unit) {
    val opciones = listOf(
        "empleados" to "Gestión de empleados",
        "asistencia" to "Gestión de asistencia",
        "nomina" to "Gestión de nómina",
        "reportes" to "Gestión de reportes",
        "informes" to "Gestión de informes",
        "permisos" to "Gestión de permisos",
        "sistema" to "Configuración del sistema",
        "registrarPersona" to "Registrar usuario"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
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
