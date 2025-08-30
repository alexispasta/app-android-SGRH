package com.example.sgrh.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.sgrh.data.remote.*
import kotlinx.coroutines.launch

@Composable
fun GestionAsistenciaScreen(
    onVolver: () -> Unit,
    empresaId: String // 📌 ahora lo recibimos como parámetro
) {
    var fecha by remember { mutableStateOf("") }
    var empleados by remember { mutableStateOf(listOf<EmpleadoAsistencia>()) }
    var asistencia by remember { mutableStateOf(mutableMapOf<String, String>()) }
    var mensaje by remember { mutableStateOf("") }
    var historial by remember { mutableStateOf(listOf<String>()) }

    val scope = rememberCoroutineScope()

    // 🔹 Cargar empleados
    LaunchedEffect(empresaId) {
        scope.launch {
            try {
                val response = RetrofitClient.api.getEmpleados(empresaId)
                if (response.isSuccessful) {
                    empleados = response.body() ?: emptyList()
                } else {
                    mensaje = "Error al cargar empleados"
                }
            } catch (e: Exception) {
                mensaje = "Error de conexión"
            }
        }
    }

    // 🔹 Cargar historial
    LaunchedEffect(empresaId) {
        scope.launch {
            try {
                val response = RetrofitClient.api.getHistorial(empresaId)
                if (response.isSuccessful) {
                    historial = response.body() ?: emptyList()
                }
            } catch (_: Exception) {}
        }
    }

    // 🔹 Guardar asistencia
    fun guardarAsistencia() {
        if (fecha.isBlank()) {
            mensaje = "Debe seleccionar una fecha"
            return
        }
        scope.launch {
            try {
                val registros = empleados.map { emp ->
                    AsistenciaRequest(
                        documento = emp.codigo ?: emp.documento.orEmpty(),
                        fecha = fecha,
                        estado = asistencia[emp._id] ?: "Presente",
                        empresaId = empresaId
                    )
                }

                val response = RetrofitClient.api.guardarAsistencia(registros)
                if (response.isSuccessful) {
                    mensaje = response.body()?.message ?: "Asistencia guardada ✅"

                    // refrescar historial
                    val histResponse = RetrofitClient.api.getHistorial(empresaId)
                    if (histResponse.isSuccessful) {
                        historial = histResponse.body() ?: emptyList()
                    }
                } else {
                    mensaje = "Error al guardar asistencia"
                }
            } catch (e: Exception) {
                mensaje = "Error de conexión"
            }
        }
    }

    // 🔹 Cargar asistencia por fecha
    fun cargarAsistenciaFecha(f: String) {
        fecha = f
        scope.launch {
            try {
                val response = RetrofitClient.api.getAsistenciaPorFecha(empresaId, f)
                if (response.isSuccessful) {
                    val registros = response.body() ?: emptyList()
                    val estados = mutableMapOf<String, String>()
                    registros.forEach { reg ->
                        val empleado = empleados.find {
                            it.codigo == reg.documento || it.documento == reg.documento
                        }
                        if (empleado != null) estados[empleado._id] = reg.estado
                    }
                    asistencia = estados
                }
            } catch (_: Exception) {}
        }
    }

    // 📌 UI
    Row(
        Modifier.fillMaxSize().padding(12.dp)
    ) {
        // 📌 Tabla de asistencia
        Column(Modifier.weight(2f).padding(end = 12.dp)) {
            Text("Gestión de Asistencia", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(8.dp))

            if (mensaje.isNotEmpty()) {
                Text(
                    mensaje,
                    modifier = Modifier.fillMaxWidth().background(Color(0xFFD9EDF7)).padding(8.dp),
                    color = Color.Black
                )
                Spacer(Modifier.height(8.dp))
            }

            OutlinedTextField(
                value = fecha,
                onValueChange = { fecha = it },
                label = { Text("Fecha (yyyy-mm-dd)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            LazyColumn {
                if (empleados.isEmpty()) {
                    item { Text("Cargando empleados...", color = Color.Gray) }
                } else {
                    items(empleados) { emp ->
                        Row(
                            Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("${emp.nombre} ${emp.apellido}", modifier = Modifier.weight(1f))
                            Text(emp.codigo ?: emp.documento.orEmpty(), modifier = Modifier.weight(1f))

                            var estado by remember { mutableStateOf(asistencia[emp._id] ?: "Presente") }
                            DropdownMenuEstado(
                                estado = estado,
                                onEstadoSeleccionado = {
                                    estado = it
                                    asistencia[emp._id] = it
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(onClick = { guardarAsistencia() }) {
                Text("Guardar Asistencia")
            }

            Spacer(Modifier.height(8.dp))

            Button(onClick = onVolver, colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)) {
                Text("← Volver al Menú")
            }
        }

        // 📌 Historial
        Column(Modifier.weight(1f).fillMaxHeight()) {
            Text("Historial de Fechas", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            LazyColumn {
                if (historial.isEmpty()) {
                    item { Text("Sin registros", color = Color.Gray) }
                } else {
                    items(historial) { f ->
                        Text(
                            f,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { cargarAsistenciaFecha(f) }
                                .padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}

// 🔹 Menú desplegable de estado
@Composable
fun DropdownMenuEstado(estado: String, onEstadoSeleccionado: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(estado)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            listOf("Presente", "Ausente", "Permiso", "Retardo").forEach {
                DropdownMenuItem(
                    text = { Text(it) },
                    onClick = {
                        onEstadoSeleccionado(it)
                        expanded = false
                    }
                )
            }
        }
    }
}
