package com.example.sgrh.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.sgrh.data.remote.*
import kotlinx.coroutines.launch

@Composable
fun GestionNominaScreen(
    onVolver: () -> Unit,
    empresaId: String,
    apiService: ApiService = RetrofitClient.api // 👈 por defecto usa Retrofit real
) {
    var empleados by remember { mutableStateOf(listOf<EmpleadoNomina>()) }
    var nominas by remember { mutableStateOf(listOf<Nomina>()) }
    var datosNomina by remember { mutableStateOf<Nomina?>(null) }
    var mensaje by remember { mutableStateOf("") }
    var mostrarModal by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    // Cargar empleados y nóminas al iniciar
    LaunchedEffect(empresaId) {
        scope.launch {
            try {
                val resEmp = apiService.getEmpleados(empresaId)
                val resNom = apiService.getNominas(empresaId)

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
    }

    fun guardarCambiosNomina() {
        scope.launch {
            try {
                val nomina = datosNomina ?: return@launch
                val payload = Nomina(
                    _id = nomina._id,
                    nombre = nomina.nombre,
                    cedula = nomina.cedula,
                    cuenta = nomina.cuenta,
                    salario = nomina.salario,
                    auxilio = nomina.auxilio,
                    horasExtra = nomina.horasExtra,
                    bonificacion = nomina.bonificacion,
                    descuentos = nomina.descuentos,
                    empresaId = nomina.empresaId
                )

                val response = if (payload._id != null) {
                    apiService.actualizarNomina(payload._id!!, payload)
                } else {
                    apiService.crearNomina(payload)
                }

                if (response.isSuccessful) {
                    val resNom = apiService.getNominas(empresaId)
                    if (resNom.isSuccessful) nominas = resNom.body() ?: emptyList()
                    datosNomina = null
                    mostrarModal = true
                    mensaje = ""
                } else {
                    mensaje = "❌ Error guardando nómina: ${response.errorBody()?.string() ?: response.message()}"
                }

            } catch (e: Exception) {
                mensaje = "❌ Excepción al guardar: ${e.localizedMessage}"
            }
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Gestión de Nómina", style = MaterialTheme.typography.headlineSmall)
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

        // Mostrar botones de empleados solo si no hay ninguno seleccionado
        if (datosNomina == null) {
            empleados.forEach { emp ->
                Button(
                    onClick = { handleEditarClick(emp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text("${emp.nombre} ${emp.apellido}")
                }
            }
        }

        // Formulario de edición de nómina
        datosNomina?.let { nomina ->
            Spacer(Modifier.height(16.dp))
            Text("Información de Nómina", style = MaterialTheme.typography.titleMedium)

            InputField("Nombre", nomina.nombre, onChange = {}, enabled = false)
            InputField("Cédula", nomina.cedula, onChange = {}, enabled = false)
            InputField("Cuenta Bancaria", nomina.cuenta,
                onChange = { v -> datosNomina = nomina.copy(cuenta = v) })
            InputField("Salario Base", nomina.salario.toString(),
                onChange = { v -> datosNomina = nomina.copy(salario = v.toDoubleOrNull() ?: 0.0) })
            InputField("Auxilio de Transporte", nomina.auxilio.toString(),
                onChange = { v -> datosNomina = nomina.copy(auxilio = v.toDoubleOrNull() ?: 0.0) })
            InputField("Horas Extra", nomina.horasExtra.toString(),
                onChange = { v -> datosNomina = nomina.copy(horasExtra = v.toDoubleOrNull() ?: 0.0) })
            InputField("Bonificaciones", nomina.bonificacion.toString(),
                onChange = { v -> datosNomina = nomina.copy(bonificacion = v.toDoubleOrNull() ?: 0.0) })
            InputField("Deducciones", nomina.descuentos.toString(),
                onChange = { v -> datosNomina = nomina.copy(descuentos = v.toDoubleOrNull() ?: 0.0) })

            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Button(onClick = { guardarCambiosNomina() }) { Text("Guardar Cambios") }
                Button(onClick = { datosNomina = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)) {
                    Text("Cancelar")
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        Button(onClick = onVolver, colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)) {
            Text("← Volver al Menú")
        }
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        enabled = enabled,
        singleLine = true
    )
}
