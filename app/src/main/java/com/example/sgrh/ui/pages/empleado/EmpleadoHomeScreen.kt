// EmpleadoHomeScreen.kt
package com.example.sgrh.ui.pages.empleado

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EmpleadoHomeScreen() {
    var opcionSeleccionada by remember { mutableStateOf<String?>(null) }
    var empleadoSeleccionado by remember { mutableStateOf<Empleado?>(null) }

    // 🔹 Lista de empleados de ejemplo (temporal, puedes quitar cuando uses backend)
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
        Text("Panel de Empleado", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        when {
            // 👇 Placeholder de detalle
            empleadoSeleccionado != null -> {
                Text("Formulario detalle de: ${empleadoSeleccionado!!.nombre}")
            }
            else -> {
                when (opcionSeleccionada) {
                    "consultar" -> Text("Pantalla: Consultar Información")
                    "permisos" -> Text("Pantalla: Permisos del Empleado")
                    "certificacion" -> Text("Pantalla: Registro de Certificación")
                    else -> MenuOpcionesEmpleado { seleccion -> opcionSeleccionada = seleccion }
                }
            }
        }
    }
}

/** Menú de opciones del empleado */
@Composable
fun MenuOpcionesEmpleado(onSeleccionar: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(onClick = { onSeleccionar("consultar") }, modifier = Modifier.fillMaxWidth()) {
            Text("Consultar Información")
        }
        Button(onClick = { onSeleccionar("permisos") }, modifier = Modifier.fillMaxWidth()) {
            Text("Solicitar Permisos")
        }
        Button(onClick = { onSeleccionar("certificacion") }, modifier = Modifier.fillMaxWidth()) {
            Text("Registrar Certificación")
        }
    }
}

/** Modelo de datos de empleado */
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
