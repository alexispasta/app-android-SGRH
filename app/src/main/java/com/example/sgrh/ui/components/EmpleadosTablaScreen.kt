package com.example.sgrh.ui.pages.gerente

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sgrh.data.remote.ApiService
import com.example.sgrh.data.remote.EmpleadoAsistencia
import com.example.sgrh.data.remote.Empleado as RemoteEmpleado
import com.example.sgrh.ui.models.Empleado
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

@Composable
fun EmpleadosTablaScreen(
    apiService: ApiService,
    empresaId: String,
    onVolver: () -> Unit
) {
    var empleados by remember { mutableStateOf(listOf<Empleado>()) }
    var empleadoEditando by remember { mutableStateOf<Empleado?>(null) }
    var mensaje by remember { mutableStateOf<Pair<String, String>?>(null) }
    val scope = rememberCoroutineScope()

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
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

        if (empleados.isEmpty()) {
            Text(
                text = if (empresaId.isNotEmpty()) "No hay empleados" else "Sin empresa asignada",
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            LazyColumn {
                item {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .background(Color.DarkGray)
                            .padding(8.dp)
                    ) {
                        listOf("Nombre", "Apellido", "Código", "Acción")
                            .forEach { header ->
                                Text(
                                    text = header,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                    }
                }
                items(empleados) { empleado ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        listOf(
                            empleado.nombre ?: "",
                            empleado.apellido ?: "",
                            empleado.codigo ?: ""
                        ).forEach { campo ->
                            Text(campo, modifier = Modifier.weight(1f))
                        }
                        Button(
                            onClick = { empleadoEditando = empleado.copy() },
                            modifier = Modifier.weight(1f)
                        ) { Text("Editar") }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        empleadoEditando?.let { emp ->
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .background(Color(0xFFF0F0F0))
                    .padding(12.dp)
            ) {
                Text("Editando: ${emp.nombre}", fontWeight = FontWeight.Bold)
                listOf(
                    "nombre" to emp.nombre,
                    "apellido" to emp.apellido,
                    "codigo" to emp.codigo
                ).forEach { (campo, valor) ->
                    OutlinedTextField(
                        value = valor ?: "",
                        onValueChange = { nuevo ->
                            empleadoEditando = when (campo) {
                                "nombre" -> emp.copy(nombre = nuevo)
                                "apellido" -> emp.copy(apellido = nuevo)
                                "codigo" -> emp.copy(codigo = nuevo)
                                else -> emp
                            }
                        },
                        label = { Text(campo.replaceFirstChar { it.uppercase() }) },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    )
                }

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(onClick = {
                        scope.launch {
                            try {
                                val response = apiService.actualizarEmpleado(emp._id, emp.toRemote(empresaId))
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
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = { empleadoEditando = null }) { Text("Cancelar") }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onVolver,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
        ) { Text("← Volver al menú") }
    }
}
