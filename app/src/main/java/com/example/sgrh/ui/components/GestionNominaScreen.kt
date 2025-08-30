package com.example.sgrh.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.sgrh.data.remote.EmpleadoNomina
import com.example.sgrh.data.remote.EmpleadoAsistencia
import com.example.sgrh.data.remote.Nomina
import com.example.sgrh.data.remote.RetrofitClient
import kotlinx.coroutines.launch

@Composable
fun GestionNominaScreen(onVolver: () -> Unit, empresaId: String) {
    var empleados by remember { mutableStateOf(listOf<EmpleadoNomina>()) }
    var nominas by remember { mutableStateOf(listOf<Nomina>()) }
    var datosNomina by remember { mutableStateOf<Nomina?>(null) }
    var mostrarFormulario by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf("") }
    var mostrarModal by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val api = RetrofitClient.api

    LaunchedEffect(empresaId) {
        scope.launch {
            try {
                val resEmp = api.getEmpleados(empresaId)
                val resNom = api.getNominas(empresaId)

                if (resEmp.isSuccessful && resNom.isSuccessful) {
                    val empleadosResp = resEmp.body() ?: emptyList<EmpleadoAsistencia>()
                    empleados = empleadosResp.map {
                        EmpleadoNomina(
                            _id = it._id,
                            nombre = it.nombre ?: "",
                            apellido = it.apellido ?: "",
                            codigo = it.documento ?: it.codigo ?: ""
                        )
                    }
                    nominas = resNom.body() ?: emptyList()
                    mensaje = ""
                } else {
                    mensaje = "❌ Error cargando datos: ${resEmp.message()} / ${resNom.message()}"
                }
            } catch (e: Exception) {
                mensaje = "❌ Excepción: ${e.localizedMessage}"
            }
        }
    }

    fun obtenerNominaEmpleado(cedula: String): Nomina? = nominas.find { it.cedula == cedula }

    fun handleEditarClick(emp: EmpleadoNomina) {
        val registro = obtenerNominaEmpleado(emp.codigo)
        datosNomina = registro ?: Nomina(
            nombre = "${emp.nombre} ${emp.apellido}",
            cedula = emp.codigo,
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
                val response = if (nomina._id != null) api.actualizarNomina(nomina._id, nomina)
                else api.crearNomina(nomina)

                if (response.isSuccessful) {
                    val resNom = api.getNominas(empresaId)
                    if (resNom.isSuccessful) nominas = resNom.body() ?: emptyList()
                    mostrarFormulario = false
                    mostrarModal = true
                } else mensaje = "❌ Error guardando nómina: ${response.message()}"
            } catch (e: Exception) {
                mensaje = "❌ Excepción al guardar: ${e.localizedMessage}"
            }
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Text("Gestión de Nómina", style = MaterialTheme.typography.headlineSmall)

        if (mensaje.isNotEmpty()) {
            Text(mensaje,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFD9EDF7))
                    .padding(8.dp),
                color = Color.Black
            )
        }

        Spacer(Modifier.height(12.dp))

        LazyColumn {
            items(empleados) { emp ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${emp.nombre} ${emp.apellido}", Modifier.weight(1f))
                    Text(emp.codigo, Modifier.weight(1f))
                    Button(onClick = { handleEditarClick(emp) }) {
                        Text(if (obtenerNominaEmpleado(emp.codigo) != null) "Editar Nómina" else "Crear Nómina")
                    }
                }
                Divider()
            }
        }

        if (mostrarFormulario && datosNomina != null) {
            Spacer(Modifier.height(16.dp))
            Text("Información de Nómina", style = MaterialTheme.typography.titleMedium)

            InputField("Nombre", datosNomina!!.nombre, onChange = {}, enabled = false)
            InputField("Cédula", datosNomina!!.cedula, onChange = {}, enabled = false)
            InputField("Cuenta Bancaria", datosNomina!!.cuenta,
                onChange = { v -> datosNomina = datosNomina!!.copy(cuenta = v) })
            InputField("Salario Base", datosNomina!!.salario.toString(),
                onChange = { v -> datosNomina = datosNomina!!.copy(salario = v.toDoubleOrNull() ?: 0.0) })
            InputField("Auxilio de Transporte", datosNomina!!.auxilio.toString(),
                onChange = { v -> datosNomina = datosNomina!!.copy(auxilio = v.toDoubleOrNull() ?: 0.0) })
            InputField("Horas Extra", datosNomina!!.horasExtra.toString(),
                onChange = { v -> datosNomina = datosNomina!!.copy(horasExtra = v.toDoubleOrNull() ?: 0.0) })
            InputField("Bonificaciones", datosNomina!!.bonificacion.toString(),
                onChange = { v -> datosNomina = datosNomina!!.copy(bonificacion = v.toDoubleOrNull() ?: 0.0) })
            InputField("Deducciones", datosNomina!!.descuentos.toString(),
                onChange = { v -> datosNomina = datosNomina!!.copy(descuentos = v.toDoubleOrNull() ?: 0.0) })

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Button(onClick = { guardarCambiosNomina() }) { Text("Guardar Cambios") }
                Button(onClick = { mostrarFormulario = false }, colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)) { Text("Cancelar") }
            }
        }

        Spacer(Modifier.height(12.dp))
        Button(onClick = onVolver, colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)) { Text("← Volver al Menú") }
    }

    if (mostrarModal) {
        AlertDialog(
            onDismissRequest = { mostrarModal = false },
            title = { Text("✅ Cambios guardados") },
            confirmButton = { Button(onClick = { mostrarModal = false }) { Text("OK") } }
        )
    }
}

@Composable
fun InputField(label: String, value: String, onChange: (String) -> Unit, enabled: Boolean = true) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        enabled = enabled,
        singleLine = true
    )
}
