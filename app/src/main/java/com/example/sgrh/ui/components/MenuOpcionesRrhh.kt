package com.example.sgrh.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MenuOpcionesRrhh(
    onSeleccionar: (String) -> Unit
) {
    val opciones = listOf(
        "empleados" to "Gestión de empleados",
        "asistencia" to "Gestión de asistencia",
        "reportes" to "Gestión de reportes",
        "nomina" to "Gestión de nómina",
        "permisos" to "Gestión de permisos",
        "informes" to "Gestión de informes",
        "registrarPersona" to "Registrar usuario"
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
