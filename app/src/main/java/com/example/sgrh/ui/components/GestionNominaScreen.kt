// 📂 ui/pages/gerente/GestionNominaScreen.kt
package com.example.sgrh.ui.components
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

// 🔹 Modelos
data class EmpleadoNomina(
    val id: String,
    val nombre: String,
    val apellido: String,
    val documento: String
)

data class Nomina(
    val id: String? = null,
    val nombre: String,
    val cedula: String,
    var cuenta: String,
    var salario: Double,
    var auxilio: Double,
    var horasExtra: Double,
    var bonificacion: Double,
    var descuentos: Double,
    var empresaId: String
)

@Composable
fun GestionNominaScreen(onVolver: () -> Unit) {
    var empleados by remember { mutableStateOf(listOf<EmpleadoNomina>()) }
    var nominas by remember { mutableStateOf(listOf<Nomina>()) }
    var datosNomina by remember { mutableStateOf<Nomina?>(null) }
    var mostrarFormulario by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf("") }
    var mostrarModal by remember { mutableStateOf(false) }

    val empresaId = "123456" // TODO: obtener de SharedPreferences/Datastore
    val scope = rememberCoroutineScope()

    // 🔹 Cargar empleados y nóminas
    LaunchedEffect(empresaId) {
        try {
            val (empList, nomList) = withContext(Dispatchers.IO) {
                // Empleados
                val urlEmpleados = URL("http://10.0.2.2:3000/api/personas/empresa/$empresaId")
                val resEmp = (urlEmpleados.openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                }
                val empleadosJson = JSONArray(resEmp.inputStream.bufferedReader().use { it.readText() })
                val empListLocal = List(empleadosJson.length()) { i ->
                    val obj = empleadosJson.getJSONObject(i)
                    EmpleadoNomina(
                        id = obj.getString("_id"),
                        nombre = obj.getString("nombre"),
                        apellido = obj.getString("apellido"),
                        documento = obj.getString("codigo")
                    )
                }

                // Nóminas
                val urlNomina = URL("http://10.0.2.2:3000/api/nomina/empresa/$empresaId")
                val resNom = (urlNomina.openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                }
                val nominasJson = JSONArray(resNom.inputStream.bufferedReader().use { it.readText() })
                val nomListLocal = List(nominasJson.length()) { i ->
                    val obj = nominasJson.getJSONObject(i)
                    Nomina(
                        id = obj.optString("_id", null),
                        nombre = obj.optString("nombre", ""),
                        cedula = obj.optString("cedula", ""),
                        cuenta = obj.optString("cuenta", ""),
                        salario = obj.optDouble("salario", 0.0),
                        auxilio = obj.optDouble("auxilio", 0.0),
                        horasExtra = obj.optDouble("horasExtra", 0.0),
                        bonificacion = obj.optDouble("bonificacion", 0.0),
                        descuentos = obj.optDouble("descuentos", 0.0),
                        empresaId = empresaId
                    )
                }

                empListLocal to nomListLocal
            }

            empleados = empList
            nominas = nomList
            mensaje = ""
        } catch (e: Exception) {
            mensaje = "❌ Error al cargar datos"
        }
    }

    fun obtenerNominaEmpleado(cedula: String): Nomina? {
        return nominas.find { it.cedula == cedula }
    }

    fun handleEditarClick(emp: EmpleadoNomina) {
        val registro = obtenerNominaEmpleado(emp.documento)
        datosNomina = registro ?: Nomina(
            nombre = "${emp.nombre} ${emp.apellido}",
            cedula = emp.documento,
            cuenta = "",
            salario = 0.0,
            auxilio = 0.0,
            horasExtra = 0.0,
            bonificacion = 0.0,
            descuentos = 0.0,
            empresaId = empresaId
        )
        mostrarFormulario = true
    }

    fun guardarCambiosNomina() {
        scope.launch {
            try {
                val nomina = datosNomina ?: return@launch
                val tieneId = nomina.id != null

                withContext(Dispatchers.IO) {
                    val payload = JSONObject().apply {
                        put("nombre", nomina.nombre)
                        put("cedula", nomina.cedula)
                        put("cuenta", nomina.cuenta)
                        put("salario", nomina.salario)
                        put("auxilio", nomina.auxilio)
                        put("horasExtra", nomina.horasExtra)
                        put("bonificacion", nomina.bonificacion)
                        put("descuentos", nomina.descuentos)
                        put("empresaId", nomina.empresaId)
                    }

                    val url = if (tieneId) {
                        URL("http://10.0.2.2:3000/api/nomina/${nomina.id}")
                    } else {
                        URL("http://10.0.2.2:3000/api/nomina")
                    }

                    val conn = url.openConnection() as HttpURLConnection
                    conn.requestMethod = if (tieneId) "PUT" else "POST"
                    conn.setRequestProperty("Content-Type", "application/json")
                    conn.doOutput = true
                    conn.outputStream.use { it.write(payload.toString().toByteArray()) }

                    val responseCode = conn.responseCode
                    val responseText = (if (responseCode in 200..299) conn.inputStream else conn.errorStream)
                        .bufferedReader().use { it.readText() }

                    if (responseCode !in 200..299) throw Exception(responseText)

                    // Refrescar nóminas
                    val urlNomina = URL("http://10.0.2.2:3000/api/nomina/empresa/$empresaId")
                    val resNom = (urlNomina.openConnection() as HttpURLConnection).apply {
                        requestMethod = "GET"
                    }
                    val nominasJson = JSONArray(resNom.inputStream.bufferedReader().use { it.readText() })

                    val nuevasNominas = List(nominasJson.length()) { i ->
                        val obj = nominasJson.getJSONObject(i)
                        Nomina(
                            id = obj.optString("_id", null),
                            nombre = obj.optString("nombre", ""),
                            cedula = obj.optString("cedula", ""),
                            cuenta = obj.optString("cuenta", ""),
                            salario = obj.optDouble("salario", 0.0),
                            auxilio = obj.optDouble("auxilio", 0.0),
                            horasExtra = obj.optDouble("horasExtra", 0.0),
                            bonificacion = obj.optDouble("bonificacion", 0.0),
                            descuentos = obj.optDouble("descuentos", 0.0),
                            empresaId = empresaId
                        )
                    }

                    withContext(Dispatchers.Main) {
                        nominas = nuevasNominas
                        mostrarFormulario = false
                        mostrarModal = true
                    }
                }
            } catch (e: Exception) {
                mensaje = "❌ Error al guardar nómina"
            }
        }
    }

    Column(Modifier.fillMaxSize().padding(12.dp)) {
        Text("Gestión de Nómina", style = MaterialTheme.typography.headlineSmall)

        if (mensaje.isNotEmpty()) {
            Text(
                mensaje,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFD9EDF7))
                    .padding(8.dp),
                color = Color.Black
            )
        }

        Spacer(Modifier.height(12.dp))

        // 📌 Lista de empleados
        LazyColumn {
            items(empleados) { emp ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${emp.nombre} ${emp.apellido}", Modifier.weight(1f))
                    Text(emp.documento, Modifier.weight(1f))
                    Button(onClick = { handleEditarClick(emp) }) {
                        Text(
                            if (obtenerNominaEmpleado(emp.documento) != null)
                                "Editar Nómina"
                            else
                                "Crear Nómina"
                        )
                    }
                }
                Divider()
            }
        }

        // 📌 Formulario
        if (mostrarFormulario && datosNomina != null) {
            Spacer(Modifier.height(16.dp))
            Text("Información de Nómina", style = MaterialTheme.typography.titleMedium)

            @Composable
            fun InputField(
                label: String,
                value: String,
                onChange: (String) -> Unit,
                enabled: Boolean = true
            ) {
                OutlinedTextField(
                    value = value,
                    onValueChange = onChange,
                    label = { Text(label) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    enabled = enabled,
                    singleLine = true
                )
            }

            // Solo lectura
            InputField(
                label = "Nombre",
                value = datosNomina!!.nombre,
                onChange = { _ -> },
                enabled = false
            )
            InputField(
                label = "Cédula",
                value = datosNomina!!.cedula,
                onChange = { _ -> },
                enabled = false
            )

            // Editables
            InputField(
                label = "Cuenta Bancaria",
                value = datosNomina!!.cuenta,
                onChange = { v -> datosNomina = datosNomina!!.copy(cuenta = v) }
            )
            InputField(
                label = "Salario Base",
                value = datosNomina!!.salario.toString(),
                onChange = { v -> datosNomina = datosNomina!!.copy(salario = v.toDoubleOrNull() ?: 0.0) }
            )
            InputField(
                label = "Auxilio de Transporte",
                value = datosNomina!!.auxilio.toString(),
                onChange = { v -> datosNomina = datosNomina!!.copy(auxilio = v.toDoubleOrNull() ?: 0.0) }
            )
            InputField(
                label = "Horas Extra",
                value = datosNomina!!.horasExtra.toString(),
                onChange = { v -> datosNomina = datosNomina!!.copy(horasExtra = v.toDoubleOrNull() ?: 0.0) }
            )
            InputField(
                label = "Bonificaciones",
                value = datosNomina!!.bonificacion.toString(),
                onChange = { v -> datosNomina = datosNomina!!.copy(bonificacion = v.toDoubleOrNull() ?: 0.0) }
            )
            InputField(
                label = "Deducciones",
                value = datosNomina!!.descuentos.toString(),
                onChange = { v -> datosNomina = datosNomina!!.copy(descuentos = v.toDoubleOrNull() ?: 0.0) }
            )

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Button(onClick = { guardarCambiosNomina() }) { Text("Guardar Cambios") }
                Button(
                    onClick = { mostrarFormulario = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text("Cancelar")
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = onVolver,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
        ) {
            Text("← Volver al Menú")
        }
    }

    // 📌 Modal de confirmación
    if (mostrarModal) {
        AlertDialog(
            onDismissRequest = { mostrarModal = false },
            title = { Text("✅ Cambios guardados") },
            confirmButton = {
                Button(onClick = { mostrarModal = false }) { Text("OK") }
            }
        )
    }
}
