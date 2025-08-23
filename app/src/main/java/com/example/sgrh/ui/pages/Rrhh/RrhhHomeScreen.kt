// RrhhHomeScreen.kt
package com.example.sgrh.ui.pages.Rrhh

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RrhhHomeScreen() {
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
                EmpleadoDetalleForm(
                    empleado = empleadoSeleccionado!!,
                    onCerrar = { handleCerrarDetalle() }
                )
            }
            else -> {
                when (opcionSeleccionada) {
                    "empleados" -> EmpleadosTabla(
                        empleados = empleadosEjemplo,
                        onEditar = { handleEditar(it) },
                        onVolver = { opcionSeleccionada = null }
                    )
                    "asistencia" -> GestionAsistencia { opcionSeleccionada = null }
                    "reportes" -> GestionReportes { opcionSeleccionada = null }
                    "nomina" -> GestionNomina { opcionSeleccionada = null }
                    "permisos" -> GestionPermisos { opcionSeleccionada = null }
                    "informes" -> GestionInformes { opcionSeleccionada = null }
                    "registrarPersona" -> RegistrarPersona { opcionSeleccionada = null }
                    else -> MenuOpcionesRrhh { seleccion -> opcionSeleccionada = seleccion }
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
        Text("Panel de RRHH", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        contenido()
    }
}

// -------- Modelos --------
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

// -------- Componentes placeholders --------
@Composable
fun MenuOpcionesRrhh(onSeleccionar: (String) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = { onSeleccionar("empleados") }) { Text("Gestión de Empleados") }
        Button(onClick = { onSeleccionar("asistencia") }) { Text("Gestión de Asistencia") }
        Button(onClick = { onSeleccionar("reportes") }) { Text("Reportes") }
        Button(onClick = { onSeleccionar("nomina") }) { Text("Nómina") }
        Button(onClick = { onSeleccionar("permisos") }) { Text("Permisos") }
        Button(onClick = { onSeleccionar("informes") }) { Text("Informes") }
        Button(onClick = { onSeleccionar("registrarPersona") }) { Text("Registrar Persona") }
    }
}

@Composable
fun EmpleadosTabla(empleados: List<Empleado>, onEditar: (Empleado) -> Unit, onVolver: () -> Unit) {
    Column {
        empleados.forEach { empleado ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(empleado.nombre)
                Button(onClick = { onEditar(empleado) }) {
                    Text("Editar")
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Button(onClick = onVolver) { Text("Volver") }
    }
}

@Composable
fun EmpleadoDetalleForm(empleado: Empleado, onCerrar: () -> Unit) {
    Column {
        Text("Detalles de ${empleado.nombre}")
        Spacer(Modifier.height(8.dp))
        Text("Documento: ${empleado.documento}")
        Text("Correo: ${empleado.correo}")
        Text("Cargo: ${empleado.cargo}")
        Spacer(Modifier.height(16.dp))
        Button(onClick = onCerrar) { Text("Cerrar") }
    }
}

// Placeholders de las demás pantallas
@Composable fun GestionAsistencia(onVolver: () -> Unit) { Text("Pantalla de Asistencia"); Button(onClick = onVolver){ Text("Volver") } }
@Composable fun GestionReportes(onVolver: () -> Unit) { Text("Pantalla de Reportes"); Button(onClick = onVolver){ Text("Volver") } }
@Composable fun GestionNomina(onVolver: () -> Unit) { Text("Pantalla de Nómina"); Button(onClick = onVolver){ Text("Volver") } }
@Composable fun GestionPermisos(onVolver: () -> Unit) { Text("Pantalla de Permisos"); Button(onClick = onVolver){ Text("Volver") } }
@Composable fun GestionInformes(onVolver: () -> Unit) { Text("Pantalla de Informes"); Button(onClick = onVolver){ Text("Volver") } }
@Composable fun RegistrarPersona(onVolver: () -> Unit) { Text("Pantalla de Registrar Persona"); Button(onClick = onVolver){ Text("Volver") } }
