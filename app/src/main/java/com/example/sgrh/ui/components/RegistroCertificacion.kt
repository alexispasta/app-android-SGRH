// components/RegistroCertificacion.kt
package com.example.sgrh.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RegistroCertificacion(onVolver: () -> Unit) {
    // Lista de certificados simulada
    val certificados = remember {
        mutableStateListOf(
            Certificado("Certificado de habilidades blandas", "2023-05-20"),
            Certificado("Certificado de seguridad laboral", "2024-01-10")
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Gestión de Certificaciones",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Lista de certificados
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            items(certificados) { cert ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = cert.nombre, style = MaterialTheme.typography.bodyLarge)
                        Text(text = "Subido el ${cert.fecha}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        // Botón para simular carga de archivo (en móvil no hay input file)
        Button(
            onClick = {
                // Aquí solo simulamos subida, porque en Android es diferente
                certificados.add(Certificado("Nuevo Certificado", "2025-08-24"))
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cargar nuevo certificado")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Botón para volver al menú
        Button(
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text("← Volver al Menú")
        }
    }
}

data class Certificado(
    val nombre: String,
    val fecha: String
)
