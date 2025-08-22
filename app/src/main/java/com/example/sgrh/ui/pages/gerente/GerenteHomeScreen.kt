// GerenteHomeScreen.kt
package com.example.sgrh.ui.pages.gerente

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GerenteHomeScreen() {
    var opcionSeleccionada by remember { mutableStateOf<String?>(null) }
    var empleadoSeleccionado by remember { mutableStateOf<Empleado?>(null) }

    val empleadosEjemplo = listOf(
        Empleado(
            id = 1,
            nombre = "Juan Pérez",
            documento = "123456789",
            fecha = "2021-03-15",
            estado = "activo",
            correo = "juan.perez@email.com",
            salario = 2500000,
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
            salario = 3200000,
            cargo = "Coordinadora",
            eps = "Nueva EPS"
        )
    )

    fun handleEditar(empleado: Empleado) {
        empleadoSeleccionado = empleado
    }

    fun handleCerrarDetalle() {
        empleadoSeleccionado = null
    }

    val contenido: @Composable () -> Unit = {
        when {
            empleadoSeleccionado != null -> {
                Text("Formulario detalle de: ${empleadoSeleccionado!!.nombre}")
            }
            else -> {
                when (opcionSeleccionada) {
                    "empleados" -> Text("Pantalla: Tabla de Empleados")
                    "asistencia" -> Text("Pantalla: Gestión Asistencia")
                    "nomina" -> Text("Pantalla: Gestión Nómina")
                    "reportes" -> Text("Pantalla: Gestión Reportes")
                    "informes" -> Text("Pantalla: Gestión Informes")
                    "permisos" -> Text("Pantalla: Gestión Permisos")
                    "sistema" -> Text("Pantalla: Configuración del Sistema")
                    "registrarPersona" -> Text("Pantalla: Registrar Persona")
                    else -> Text("Menú de Opciones del Gerente")
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Panel Gerente", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        contenido()
    }
}

// 👇 Data class corregida (nombre en PascalCase)
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
