package com.example.sgrh.ui.components

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.sgrh.data.remote.AsistenciaRequest
import com.example.sgrh.data.remote.EmpleadoAsistencia
import com.example.sgrh.data.remote.RetrofitClient
import kotlinx.coroutines.launch
import java.util.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionAsistenciaScreen(
    onVolver: () -> Unit,
    empresaId: String
) {
    var fechaSeleccionada by remember { mutableStateOf("") }
    var empleados by remember { mutableStateOf<List<EmpleadoAsistencia>>(emptyList()) }
    var asistencia by remember { mutableStateOf<MutableMap<String, String>>(mutableMapOf()) }
    var mensaje by remember { mutableStateOf("") }
    var historial by remember { mutableStateOf<List<String>>(emptyList()) }
    var mostrarHistorial by remember { mutableStateOf(false) }
    var asistenciasDetalle by remember { mutableStateOf<Map<String, List<AsistenciaRequest>>>(emptyMap()) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val estados = listOf("Presente", "Ausente", "Permiso", "Retardo")

    fun showDatePicker() {
        val calendario = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, day: Int ->
                val mes = String.format("%02d", month + 1)
                val dia = String.format("%02d", day)
                fechaSeleccionada = "$year-$mes-$dia"
            },
            calendario.get(Calendar.YEAR),
            calendario.get(Calendar.MONTH),
            calendario.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    // 🔹 Cargar empleados e historial
    LaunchedEffect(empresaId) {
        scope.launch {
            try {
                val response = RetrofitClient.api.getEmpleados(empresaId)
                if (response.isSuccessful) empleados = response.body() ?: emptyList()
            } catch (_: Exception) {
                mensaje = "Error de conexión"
            }

            try {
                val responseHist = RetrofitClient.api.getHistorial(empresaId)
                if (responseHist.isSuccessful) historial = responseHist.body() ?: emptyList()
            } catch (_: Exception) {
                mensaje = "Error al cargar historial"
            }
        }
    }

    fun guardarAsistencia() {
        scope.launch {
            try {
                val registros = empleados.mapNotNull { emp ->
                    asistencia[emp._id]?.let { estado ->
                        AsistenciaRequest(
                            documento = emp.codigo ?: emp._id,
                            fecha = fechaSeleccionada,
                            estado = estado,
                            empresaId = empresaId
                        )
                    }
                }

                if (registros.isEmpty()) {
                    mensaje = "Debes marcar al menos un empleado"
                    return@launch
                }

                val response = RetrofitClient.api.guardarAsistencia(registros)
                if (response.isSuccessful) {
                    mensaje = "✅ Asistencia guardada"
                    asistencia.clear()

                    val responseHist = RetrofitClient.api.getHistorial(empresaId)
                    if (responseHist.isSuccessful) historial = responseHist.body() ?: emptyList()
                } else {
                    mensaje = "❌ Error al guardar asistencia"
                }
            } catch (e: Exception) {
                mensaje = "Error: ${e.localizedMessage}"
            }
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(12.dp)
            .verticalScroll(rememberScrollState())
            .testTag("gestionAsistenciaScreen") // 👈 agregado para test
    ) {
        Text("Gestión de Asistencia", style = MaterialTheme.typography.headlineSmall, color = Color.Black)

        if (mensaje.isNotEmpty()) {
            Text(
                mensaje,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFD9EDF7))
                    .padding(8.dp)
                    .testTag("mensajeError"), // 👈 agregado
                color = Color.Black
            )
        }

        // 📅 Selector de fecha
        OutlinedTextField(
            value = fechaSeleccionada,
            onValueChange = {},
            label = { Text("Fecha", color = Color.Black) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("campoFecha"), // 👈 agregado
            readOnly = true,
            trailingIcon = {
                IconButton(
                    onClick = { showDatePicker() },
                    modifier = Modifier.testTag("btnSeleccionarFecha") // 👈 agregado
                ) {
                    Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha", tint = Color.Black)
                }
            },
            textStyle = LocalTextStyle.current.copy(color = Color.Black)
        )

        // 👥 Lista de empleados
        if (empleados.isNotEmpty()) {
            empleados.forEach { emp ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .testTag("cardEmpleado_${emp._id}"), // 👈 agregado
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                ) {
                    Column(Modifier.padding(8.dp)) {
                        Text("${emp.nombre} ${emp.apellido}", style = MaterialTheme.typography.bodyLarge, color = Color.Black)
                    }
                }
            }

            Button(
                onClick = { guardarAsistencia() },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btnGuardarAsistencia"), // 👈 agregado
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
            ) {
                Text("💾 Guardar asistencia", color = Color.White)
            }
        } else {
            Text("No hay empleados registrados", color = Color.Gray, modifier = Modifier.testTag("sinEmpleados"))
        }

        Button(
            onClick = { mostrarHistorial = !mostrarHistorial },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("btnHistorial"), // 👈 agregado
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
        ) {
            Text(if (mostrarHistorial) "Ocultar historial" else "Ver historial", color = Color.White)
        }

        Button(
            onClick = onVolver,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("btnVolver") // 👈 agregado
        ) {
            Text("← Volver al Menú", color = Color.White)
        }
    }
}
