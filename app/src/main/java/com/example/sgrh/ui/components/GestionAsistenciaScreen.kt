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
    ) {
        Text("Gestión de Asistencia", style = MaterialTheme.typography.headlineSmall, color = Color.Black)
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

        // 📅 Selector de fecha
        OutlinedTextField(
            value = fechaSeleccionada,
            onValueChange = {},
            label = { Text("Fecha", color = Color.Black) },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { showDatePicker() }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha", tint = Color.Black)
                }
            },
            textStyle = LocalTextStyle.current.copy(color = Color.Black)
        )
        Spacer(Modifier.height(12.dp))

        // 👥 Lista de empleados
        if (empleados.isNotEmpty()) {
            empleados.forEach { emp ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                ) {
                    Column(Modifier.padding(8.dp)) {
                        Text("${emp.nombre} ${emp.apellido}", style = MaterialTheme.typography.bodyLarge, color = Color.Black)

                        var expanded by remember { mutableStateOf(false) }
                        val seleccionado = asistencia[emp._id] ?: "Seleccionar"

                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = seleccionado,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Estado", color = Color.Black) },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                                },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                textStyle = LocalTextStyle.current.copy(color = Color.Black)
                            )

                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                estados.forEach { estado ->
                                    DropdownMenuItem(
                                        text = { Text(estado, color = Color.Black) },
                                        onClick = {
                                            asistencia[emp._id] = estado
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = { guardarAsistencia() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
            ) {
                Text("💾 Guardar asistencia", color = Color.White)
            }
        } else {
            Text("No hay empleados registrados", color = Color.Gray)
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { mostrarHistorial = !mostrarHistorial },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
        ) {
            Text(if (mostrarHistorial) "Ocultar historial" else "Ver historial", color = Color.White)
        }

        Spacer(Modifier.height(12.dp))

        // 🔹 Mostrar historial
        if (mostrarHistorial) {
            if (historial.isEmpty()) {
                Text("No hay historial registrado", color = Color.Gray)
            } else {
                historial.forEach { fecha ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                    ) {
                        Column(Modifier.padding(8.dp)) {
                            Text("📌 $fecha", style = MaterialTheme.typography.bodyLarge, color = Color.Black, textAlign = TextAlign.Center)
                            Spacer(Modifier.height(4.dp))

                            asistenciasDetalle[fecha]?.let { lista ->
                                Column {
                                    lista.forEach { reg ->
                                        val emp = empleados.find { it.codigo == reg.documento || it._id == reg.documento }
                                        Text("Nombre: ${emp?.nombre ?: "Desconocido"} ${emp?.apellido ?: ""}", color = Color.Black)
                                        Text("Documento: ${reg.documento}", color = Color.Black)
                                        Text("Estado: ${reg.estado}", color = Color.Black)
                                        Spacer(Modifier.height(6.dp))
                                    }
                                }
                            }

                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Button(
                                    onClick = {
                                        scope.launch {
                                            try {
                                                val resp = RetrofitClient.api.getAsistenciaPorFecha(empresaId, fecha)
                                                if (resp.isSuccessful) {
                                                    asistenciasDetalle = asistenciasDetalle + (fecha to (resp.body() ?: emptyList()))
                                                    mensaje = "📊 Consulta de asistencia del $fecha cargada"
                                                } else {
                                                    mensaje = "❌ Error al consultar asistencia"
                                                }
                                            } catch (e: Exception) {
                                                mensaje = "Error: ${e.localizedMessage}"
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                                ) {
                                    Text("Consultar", color = Color.White)
                                }

                                Button(
                                    onClick = {
                                        scope.launch {
                                            try {
                                                val resp = RetrofitClient.api.eliminarAsistenciaPorFecha(empresaId, fecha)
                                                if (resp.isSuccessful) {
                                                    mensaje = "🗑️ Asistencia de $fecha eliminada"
                                                    historial = historial.filter { it != fecha }
                                                    asistenciasDetalle = asistenciasDetalle - fecha
                                                } else {
                                                    mensaje = "❌ Error al eliminar asistencia"
                                                }
                                            } catch (e: Exception) {
                                                mensaje = "Error: ${e.localizedMessage}"
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                                ) {
                                    Text("Eliminar", color = Color.White)
                                }

                                Button(
                                    onClick = {
                                        scope.launch {
                                            try {
                                                val resp = RetrofitClient.api.eliminarHistorialCompleto(empresaId)
                                                if (resp.isSuccessful) {
                                                    mensaje = "🗑️ Historial completo eliminado"
                                                    historial = emptyList()
                                                    asistenciasDetalle = emptyMap()
                                                } else {
                                                    mensaje = "❌ Error al eliminar todo el historial"
                                                }
                                            } catch (e: Exception) {
                                                mensaje = "Error: ${e.localizedMessage}"
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                                ) {
                                    Text("Eliminar todo", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onVolver,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
            modifier = Modifier.fillMaxWidth()
        ) { Text("← Volver al Menú", color = Color.White) }
    }
}
