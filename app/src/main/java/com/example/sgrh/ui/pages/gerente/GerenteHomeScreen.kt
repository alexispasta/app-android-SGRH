// GerenteHomeScreen.kt
package com.example.sgrh.ui.pages.gerente

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GerenteHomeScreen() {
    var opcionSeleccionada by remember { mutableStateOf<String?>(null) }
    var empleadoSeleccionado by remember { mutableStateOf<Empleado?>(null) }

    // Datos de ejemplo (puedes quitarlos cuando conectes backend)
    val empleadosEjemplo = listOf(
        Empleado(
            id = 1,
            nombre = "Juan Pérez",
            documento = "123456789",
            fecha = "2021-03-15",
            estado = "activo",
            correo = "juan.perez@email.com",
            salario = 2_500_000,
            cargo = "Analista",
            eps = "SURA"
        ),
        Empleado(
            id = 2,
            nombre = "María García",
            documento = "987654321",
            fecha = "2022-01-10",
            estado = "inactivo",
            correo = "maria.garcia@email.com",
            salario = 3_200_000,
            cargo = "Coordinadora",
            eps = "Nueva EPS"
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Panel Gerente", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        when {
            // Placeholder para detalle de empleado
            empleadoSeleccionado != null -> {
                Text("Formulario detalle de: ${empleadoSeleccionado!!.nombre}")
            }
            else -> {
                when (opcionSeleccionada) {
                    "empleados" -> Text("Pantalla: Tabla de Empleados (${empleadosEjemplo.size})")
                    "asistencia" -> Text("Pantalla: Gestión Asistencia")
                    "nomina" -> Text("Pantalla: Gestión Nómina")
                    "reportes" -> Text("Pantalla: Gestión Reportes")
                    "informes" -> Text("Pantalla: Gestión Informes")
                    "permisos" -> Text("Pantalla: Gestión Permisos")
                    "sistema" -> Text("Pantalla: Configuración del Sistema")
                    "registrarPersona" -> Text("Pantalla: Registrar Persona")
                    else -> MenuOpcionesGerente { seleccion -> opcionSeleccionada = seleccion }
                }
            }
        }
    }
}

/** Menú de opciones del gerente */
@Composable
fun MenuOpcionesGerente(onSeleccionar: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
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

/** Modelo de datos de empleado (temporal/local) */
data class Empleado(
    val id: Int,
    val nombre: String,
    val documento: String,
    val fecha: String,
    val estado: String,
    val correo: String,
    val salario: Int,
    val cargo: String,
    val eps: String
)
