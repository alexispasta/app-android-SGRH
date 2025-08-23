package com.example.sgrh.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MenuOpcionesGerente(onSeleccionar: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(onClick = { onSeleccionar("empleados") }, modifier = Modifier.fillMaxWidth()) {
            Text("Gestión de Empleados")
        }
        Button(onClick = { onSeleccionar("asistencia") }, modifier = Modifier.fillMaxWidth()) {
            Text("Gestión de Asistencia")
        }
        Button(onClick = { onSeleccionar("nomina") }, modifier = Modifier.fillMaxWidth()) {
            Text("Gestión de Nómina")
        }
        Button(onClick = { onSeleccionar("reportes") }, modifier = Modifier.fillMaxWidth()) {
            Text("Gestión de Reportes")
        }
        Button(onClick = { onSeleccionar("informes") }, modifier = Modifier.fillMaxWidth()) {
            Text("Gestión de Informes")
        }
        Button(onClick = { onSeleccionar("permisos") }, modifier = Modifier.fillMaxWidth()) {
            Text("Gestión de Permisos")
        }
        Button(onClick = { onSeleccionar("sistema") }, modifier = Modifier.fillMaxWidth()) {
            Text("Configuración del Sistema")
        }
        Button(onClick = { onSeleccionar("registrarPersona") }, modifier = Modifier.fillMaxWidth()) {
            Text("Registrar Persona")
        }
    }
}
