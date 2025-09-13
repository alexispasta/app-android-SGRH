package com.example.sgrh.ui.pages.gerente

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.example.sgrh.data.remote.ApiService
import com.example.sgrh.data.remote.EmpleadoAsistencia
import com.example.sgrh.data.remote.Empleado as RemoteEmpleado
import com.example.sgrh.ui.models.Empleado
import com.example.sgrh.ui.components.RegistroCertificacion
import kotlinx.coroutines.launch

// 🔹 Mapper de EmpleadoAsistencia → Empleado (UI)
fun EmpleadoAsistencia.toUI(): Empleado = Empleado(
    _id = this._id,
    nombre = this.nombre,
    apellido = this.apellido,
    codigo = this.codigo
)

// 🔹 Mapper de Empleado (UI) → Empleado (Remote)
fun Empleado.toRemote(empresaId: String): RemoteEmpleado = RemoteEmpleado(
    _id = this._id,
    nombre = this.nombre ?: "",
    apellido = this.apellido ?: "",
    email = this.email ?: "",
    telefono = this.telefono,
    direccion = this.direccion,
    codigo = this.codigo,
    rol = this.rol ?: "",
    fecha = this.fecha,
    ciudad = this.ciudad,
    empresaId = empresaId
)

// 🔹 Mapper de RemoteEmpleado → Empleado (UI)
fun RemoteEmpleado.toUI(): Empleado = Empleado(
    _id = this._id,
    nombre = this.nombre,
    apellido = this.apellido,
    codigo = this.codigo,
    email = this.email,
    telefono = this.telefono,
    direccion = this.direccion,
    rol = this.rol,
    fecha = this.fecha,
    ciudad = this.ciudad
)

@Composable
fun EmpleadosTablaScreen(
    apiService: ApiService,
    empresaId: String,
    onVolver: () -> Unit
) {
    var empleados by remember { mutableStateOf(listOf<Empleado>()) }
    var empleadoEditando by remember { mutableStateOf<Empleado?>(null) }
    var mostrarCertificados by remember { mutableStateOf(false) }
    var personaSeleccionada by remember { mutableStateOf<Empleado?>(null) }
    var mensaje by remember { mutableStateOf<Pair<String, String>?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // 🔹 Cargar empleados al iniciar
    LaunchedEffect(Unit) {
        try {
            val response = apiService.getEmpleados(empresaId)
            if (response.isSuccessful) {
                val lista = response.body() ?: emptyList<EmpleadoAsistencia>()
                empleados = lista.map { it.toUI() }
            } else {
                mensaje = "error" to "Error al cargar empleados: ${response.code()}"
            }
        } catch (e: Exception) {
            mensaje = "error" to (e.message ?: "Error al cargar empleados")
        }
    }

    // 🔹 Si mostrarCertificados es true, mostramos RegistroCertificacion
    if (mostrarCertificados && personaSeleccionada != null) {
        RegistroCertificacion(
            apiService = apiService,
            personaId = personaSeleccionada!!._id,
            empresaId = empresaId,
            onVolver = { mostrarCertificados = false }
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Gestión de Empleados", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))

        mensaje?.let { (tipo, texto) ->
            val color = if (tipo == "exito") Color(0xFFDFF2BF) else Color(0xFFFFBABA)
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(color)
                    .padding(8.dp)
            ) { Text(texto) }
            Spacer(Modifier.height(8.dp))
        }

        if (empleadoEditando == null) {
            empleados.forEach { emp ->
                Button(
                    onClick = {
                        // 🔹 Cargar empleado completo antes de editar
                        scope.launch {
                            try {
                                val resp = apiService.getEmpleadoById(emp._id)
                                if (resp.isSuccessful) {
                                    val remoteEmp = resp.body()
                                    if (remoteEmp != null) {
                                        empleadoEditando = remoteEmp.toUI()
                                    } else {
                                        mensaje = "error" to "Empleado no encontrado"
                                    }
                                } else {
                                    mensaje = "error" to "Error al cargar empleado: ${resp.code()}"
                                }
                            } catch (e: Exception) {
                                mensaje = "error" to (e.message ?: "Error al cargar empleado")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text("${emp.nombre ?: ""} ${emp.apellido ?: ""}")
                }
            }
        }

        empleadoEditando?.let { emp ->
            Spacer(Modifier.height(8.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Editando: ${emp.nombre}", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))

                    val campos = listOf(
                        "Nombre" to emp.nombre,
                        "Apellido" to emp.apellido,
                        "Código" to emp.codigo,
                        "Email" to emp.email,
                        "Teléfono" to emp.telefono,
                        "Dirección" to emp.direccion,
                        "Rol" to emp.rol,
                        "Fecha" to emp.fecha,
                        "Ciudad" to emp.ciudad
                    )

                    campos.forEach { (label, valor) ->
                        OutlinedTextField(
                            value = valor ?: "",
                            onValueChange = { nuevo ->
                                empleadoEditando = when (label.lowercase()) {
                                    "nombre" -> emp.copy(nombre = nuevo)
                                    "apellido" -> emp.copy(apellido = nuevo)
                                    "código" -> emp.copy(codigo = nuevo)
                                    "email" -> emp.copy(email = nuevo)
                                    "teléfono" -> emp.copy(telefono = nuevo)
                                    "dirección" -> emp.copy(direccion = nuevo)
                                    "rol" -> emp.copy(rol = nuevo)
                                    "fecha" -> emp.copy(fecha = nuevo)
                                    "ciudad" -> emp.copy(ciudad = nuevo)
                                    else -> emp
                                }
                            },
                            label = { Text(label) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(onClick = {
                            scope.launch {
                                try {
                                    val response =
                                        apiService.actualizarEmpleado(emp._id, emp.toRemote(empresaId))
                                    if (response.isSuccessful) {
                                        mensaje = "exito" to "Empleado actualizado correctamente ✅"
                                        empleadoEditando = null
                                        val reload = apiService.getEmpleados(empresaId)
                                        val lista = reload.body() ?: emptyList<EmpleadoAsistencia>()
                                        empleados = lista.map { it.toUI() }
                                    } else {
                                        mensaje = "error" to "Error al actualizar empleado"
                                    }
                                } catch (e: Exception) {
                                    mensaje = "error" to (e.message ?: "Error al guardar")
                                }
                            }
                        }) { Text("Guardar") }

                        OutlinedButton(onClick = { empleadoEditando = null }) { Text("Cancelar") }
                    }

                    Spacer(Modifier.height(12.dp))

                    // 🔹 Botón eliminar empleado
                    Button(
                        onClick = {
                            scope.launch {
                                try {
                                    val resp = apiService.eliminarEmpleado(emp._id)
                                    mensaje = if (resp.isSuccessful) {
                                        empleados = empleados.filter { it._id != emp._id }
                                        empleadoEditando = null
                                        "exito" to "Empleado eliminado correctamente ✅"
                                    } else "error" to "Error al eliminar empleado: ${resp.code()}"
                                } catch (e: Exception) {
                                    mensaje = "error" to (e.message ?: "Error al eliminar")
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Eliminar Empleado") }

                    Spacer(Modifier.height(8.dp))

                    // 🔹 Botón para gestionar certificados
                    Button(
                        onClick = {
                            personaSeleccionada = emp
                            mostrarCertificados = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Gestionar Certificados") }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onVolver,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
            modifier = Modifier.fillMaxWidth()
        ) { Text("← Volver al menú") }
    }
}
