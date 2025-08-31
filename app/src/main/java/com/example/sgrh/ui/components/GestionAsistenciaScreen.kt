package com.example.sgrh.ui.components

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sgrh.data.remote.*
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun GestionAsistenciaScreen(
    onVolver: () -> Unit,
    empresaId: String
) {
    var fechaSeleccionada by remember { mutableStateOf("") }
    var empleados by remember { mutableStateOf(listOf<EmpleadoAsistencia>()) }
    var asistencia by remember { mutableStateOf(mutableMapOf<String, String>()) }
    var mensaje by remember { mutableStateOf("") }
    var historial by remember { mutableStateOf(listOf<String>()) }
    var empleadoEditando by remember { mutableStateOf<EmpleadoAsistencia?>(null) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Cargar empleados
    LaunchedEffect(empresaId) {
        scope.launch {
            try {
                val response = RetrofitClient.api.getEmpleados(empresaId)
                if (response.isSuccessful) empleados = response.body() ?: emptyList()
            } catch (e: Exception) {
                mensaje = "Error de conexión"
            }
        }
    }

    // Cargar historial
    LaunchedEffect(empresaId) {
        scope.launch {
            try {
                val response = RetrofitClient.api.getHistorial(empresaId)
                if (response.isSuccessful) historial = response.body() ?: emptyList()
            } catch (_: Exception) {}
        }
    }

    // Guardar asistencia de todos los empleados
    fun guardarAsistenciaTodos() {
        if (fechaSeleccionada.isBlank()) {
            mensaje = "Debe seleccionar una fecha"
            return
        }
        scope.launch {
            try {
                val registros = empleados.map { emp ->
                    AsistenciaRequest(
                        documento = emp.codigo ?: emp.documento.orEmpty(),
                        fecha = fechaSeleccionada,
                        estado = asistencia[emp._id] ?: "Presente",
                        empresaId = empresaId
                    )
                }
                val response = RetrofitClient.api.guardarAsistencia(registros)
                if (response.isSuccessful) {
                    mensaje = response.body()?.message ?: "Asistencia guardada ✅"
                    empleadoEditando = null
                    val histResponse = RetrofitClient.api.getHistorial(empresaId)
                    if (histResponse.isSuccessful) historial = histResponse.body() ?: emptyList()
                } else mensaje = "Error al guardar asistencia"
            } catch (e: Exception) {
                mensaje = "Error de conexión"
            }
        }
    }

    // Selector de fecha usando DatePickerDialog
    fun abrirDatePicker() {
        val calendario = Calendar.getInstance()
        val year = calendario.get(Calendar.YEAR)
        val month = calendario.get(Calendar.MONTH)
        val day = calendario.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(context, { _, y, m, d ->
            fechaSeleccionada = "%04d-%02d-%02d".format(y, m + 1, d)
        }, year, month, day).show()
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Gestión de Asistencia", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))

        if (mensaje.isNotEmpty()) {
            Text(
                mensaje,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFD9EDF7))
                    .padding(8.dp),
                color = Color.Black
            )
            Spacer(Modifier.height(8.dp))
        }

        // Botón para abrir DatePicker
        OutlinedButton(
            onClick = { abrirDatePicker() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (fechaSeleccionada.isEmpty()) "Seleccionar fecha" else fechaSeleccionada)
        }

        Spacer(Modifier.height(12.dp))

        // Lista de empleados
        empleados.forEach { emp ->
            Column(Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        empleadoEditando = if (empleadoEditando?._id == emp._id) null else emp.copy()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text("${emp.nombre} ${emp.apellido}")
                }

                if (empleadoEditando?._id == emp._id) {
                    Spacer(Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Editando: ${emp.nombre} ${emp.apellido}", fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(8.dp))

                            // Botón de estado mostrando el valor seleccionado
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
        }

        Spacer(Modifier.height(12.dp))

        // Botón para guardar todos los estados
        Button(
            onClick = { guardarAsistenciaTodos() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar")
        }

        Spacer(Modifier.height(16.dp))

        // Botón de volver
        Button(
            onClick = onVolver,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
            modifier = Modifier.fillMaxWidth()
        ) { Text("← Volver al Menú") }
    }
}

@Composable
fun DropdownMenuEstado(
    estado: String,
    onEstadoSeleccionado: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var textoBoton by remember { mutableStateOf(estado) } // Mostrar el estado seleccionado en el botón

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(textoBoton)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            listOf("Presente", "Ausente", "Permiso", "Retardo").forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion) },
                    onClick = {
                        onEstadoSeleccionado(opcion)
                        textoBoton = opcion
                        expanded = false
                    }
                )
            }
        }
    }
}
