package com.example.sgrh.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sgrh.data.remote.Empleado
import com.example.sgrh.data.remote.RetrofitClient
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmpleadoDetalleForm(
    usuarioId: String,
    onCerrar: () -> Unit
) {
    var empleado by remember { mutableStateOf<Empleado?>(null) }
    var mensaje by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    // 🔹 Cargar datos del empleado al iniciar
    LaunchedEffect(usuarioId) {
        try {
            val response = RetrofitClient.api.getEmpleadoById(usuarioId)
            if (response.isSuccessful) {
                empleado = response.body()
            } else {
                mensaje = "❌ Error al cargar datos: ${response.code()}"
            }
        } catch (e: Exception) {
            mensaje = "❌ ${e.message}"
        }
    }

    if (empleado == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (mensaje.isEmpty()) {
                CircularProgressIndicator()
            } else {
                Text(mensaje, color = MaterialTheme.colorScheme.error)
            }
        }
        return
    }

    val emp = empleado!!
    var nombre by remember { mutableStateOf(emp.nombre ?: "") }
    var apellido by remember { mutableStateOf(emp.apellido ?: "") }
    var fecha by remember { mutableStateOf(emp.fecha ?: "") }
    var rol by remember { mutableStateOf(emp.rol ?: "") }
    var email by remember { mutableStateOf(emp.email ?: "") }
    var telefono by remember { mutableStateOf(emp.telefono ?: "") }
    var direccion by remember { mutableStateOf(emp.direccion ?: "") }
    var codigo by remember { mutableStateOf(emp.codigo ?: "") }
    var ciudad by remember { mutableStateOf(emp.ciudad ?: "") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Información del empleado", style = MaterialTheme.typography.titleMedium)

        if (mensaje.isNotEmpty()) {
            Text(
                mensaje,
                color = if (mensaje.startsWith("✅")) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.error
            )
        }

        OutlinedTextField(
            value = emp._id,
            onValueChange = {},
            label = { Text("ID del empleado (no editable)") },
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = apellido, onValueChange = { apellido = it }, label = { Text("Apellido") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = telefono, onValueChange = { telefono = it }, label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = direccion, onValueChange = { direccion = it }, label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = codigo, onValueChange = { codigo = it }, label = { Text("Código") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = fecha, onValueChange = { fecha = it }, label = { Text("Fecha (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = rol, onValueChange = { rol = it }, label = { Text("Rol") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = ciudad, onValueChange = { ciudad = it }, label = { Text("Ciudad") }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                scope.launch {
                    try {
                        val actualizado = emp.copy(
                            nombre = nombre,
                            apellido = apellido,
                            email = email,
                            telefono = telefono,
                            direccion = direccion,
                            codigo = codigo,
                            fecha = if (fecha.isBlank()) null else fecha,
                            rol = rol,
                            ciudad = ciudad
                        )
                        val response = RetrofitClient.api.actualizarEmpleado(emp._id, actualizado)
                        if (response.isSuccessful) {
                            mensaje = "✅ Datos actualizados correctamente"
                            empleado = actualizado
                        } else {
                            mensaje = "❌ Error al actualizar: ${response.code()}"
                        }
                    } catch (e: Exception) {
                        mensaje = "❌ ${e.message}"
                    }
                }
            }) {
                Text("Guardar cambios")
            }

            OutlinedButton(onClick = onCerrar) {
                Text("Volver")
            }
        }
    }
}
