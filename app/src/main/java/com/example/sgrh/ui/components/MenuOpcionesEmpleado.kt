package com.example.sgrh.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Modelo de opción
data class OpcionEmpleado(
    val id: String,
    val texto: String
)

@Composable
fun MenuOpcionesEmpleado(
    onSeleccionar: (String) -> Unit
) {
    val opciones = listOf(
        OpcionEmpleado("consultar", "Consultar Información"),
        OpcionEmpleado("permisos", "Solicitar Permisos"),
        OpcionEmpleado("certificacion", "Registro y Certificación")
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Opciones")

        opciones.forEach { opcion ->
            Button(
                onClick = { onSeleccionar(opcion.id) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = opcion.texto)
            }
        }
    }
}
