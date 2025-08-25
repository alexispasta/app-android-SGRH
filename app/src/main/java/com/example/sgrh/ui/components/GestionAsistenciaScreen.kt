// 📂 ui/pages/gerente/GestionAsistenciaScreen.kt
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
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

// 🔹 Modelo de Empleado
data class EmpleadoAsistencia(
    val id: String,
    val nombre: String,
    val apellido: String,
    val documento: String
)

@Composable
fun GestionAsistenciaScreen(onVolver: () -> Unit) {
    var fecha by remember { mutableStateOf("") }
    var empleados by remember { mutableStateOf(listOf<EmpleadoAsistencia>()) }
    var asistencia by remember { mutableStateOf(mutableMapOf<String, String>()) }
    var mensaje by remember { mutableStateOf("") }
    var historial by remember { mutableStateOf(listOf<String>()) }

    val scope = rememberCoroutineScope()
    val empresaId = "123456" // TODO: usar SharedPreferences o ViewModel

    // 🔹 Cargar empleados
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val url = URL("http://10.0.2.2:3000/api/personas/empresa/$empresaId")
                val conn = url.openConnection() as HttpURLConnection
                val data = conn.inputStream.bufferedReader().readText()
                val json = JSONArray(data)
                val lista = mutableListOf<EmpleadoAsistencia>()
                for (i in 0 until json.length()) {
                    val obj = json.getJSONObject(i)
                    lista.add(
                        EmpleadoAsistencia(
                            id = obj.getString("_id"),
                            nombre = obj.getString("nombre"),
                            apellido = obj.getString("apellido"),
                            documento = obj.optString("codigo", obj.optString("documento", ""))
                        )
                    )
                }
                empleados = lista
            } catch (e: Exception) {
                mensaje = "Error al cargar empleados"
            }
        }
    }

    // 🔹 Cargar historial
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val url = URL("http://10.0.2.2:3000/api/gerente/asistencia/historial/$empresaId")
                val conn = url.openConnection() as HttpURLConnection
                val data = conn.inputStream.bufferedReader().readText()
                val json = JSONArray(data)
                historial = List(json.length()) { json.getString(it) }
            } catch (_: Exception) {}
        }
    }

    // 🔹 Función para guardar asistencia
    fun guardarAsistencia() {
        if (fecha.isBlank()) {
            mensaje = "Debe seleccionar una fecha"
            return
        }

        scope.launch {
            try {
                val registros = JSONArray()
                empleados.forEach { emp ->
                    val obj = JSONObject()
                    obj.put("documento", emp.documento)
                    obj.put("fecha", fecha)
                    obj.put("estado", asistencia[emp.id] ?: "Presente")
                    obj.put("empresaId", empresaId)
                    registros.put(obj)
                }

                val url = URL("http://10.0.2.2:3000/api/gerente/asistencia")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json")
                conn.doOutput = true
                conn.outputStream.write(registros.toString().toByteArray())

                val response = conn.inputStream.bufferedReader().readText()
                val jsonRes = JSONObject(response)
                mensaje = jsonRes.optString("message", "Asistencia guardada ✅")

                // refrescar historial
                val histUrl = URL("http://10.0.2.2:3000/api/gerente/asistencia/historial/$empresaId")
                val histConn = histUrl.openConnection() as HttpURLConnection
                val histData = histConn.inputStream.bufferedReader().readText()
                val histJson = JSONArray(histData)
                historial = List(histJson.length()) { histJson.getString(it) }

            } catch (e: Exception) {
                mensaje = "Error al guardar asistencia"
            }
        }
    }

    // 🔹 Cargar asistencia previa de una fecha
    fun cargarAsistenciaFecha(f: String) {
        fecha = f
        scope.launch {
            try {
                val url = URL("http://10.0.2.2:3000/api/gerente/asistencia/$empresaId/$f")
                val conn = url.openConnection() as HttpURLConnection
                val data = conn.inputStream.bufferedReader().readText()
                val json = JSONArray(data)

                val estados = mutableMapOf<String, String>()
                for (i in 0 until json.length()) {
                    val obj = json.getJSONObject(i)
                    val documento = obj.getString("documento")
                    val estado = obj.getString("estado")
                    val empleado = empleados.find { it.documento == documento }
                    if (empleado != null) estados[empleado.id] = estado
                }
                asistencia = estados
            } catch (_: Exception) {}
        }
    }

    Row(
        Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // 📌 Tabla de asistencia
        Column(
            Modifier.weight(2f).padding(end = 12.dp)
        ) {
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
                            Text(emp.documento, modifier = Modifier.weight(1f))

                            var estado by remember { mutableStateOf(asistencia[emp.id] ?: "Presente") }
                            DropdownMenuEstado(
                                estado = estado,
                                onEstadoSeleccionado = {
                                    estado = it
                                    asistencia[emp.id] = it
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
        Column(
            Modifier.weight(1f).fillMaxHeight()
        ) {
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

// 🔹 Composable para menú desplegable de estado
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
