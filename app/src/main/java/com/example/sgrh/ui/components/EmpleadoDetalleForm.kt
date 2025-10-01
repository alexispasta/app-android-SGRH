package com.example.sgrh.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sgrh.data.remote.Empleado
import com.example.sgrh.data.remote.RetrofitClient
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmpleadoDetalleForm(
    usuarioId: String,
    onCerrar: () -> Unit
) {
    var empleado by remember { mutableStateOf<Empleado?>(null) }
    var mensaje by remember { mutableStateOf("") }

    // 🔹 Cargar datos del empleado
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()), // ✅ scroll habilitado
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

        // ✅ Campos solo lectura (no editables)
        OutlinedTextField(value = emp.nombre ?: "", onValueChange = {}, label = { Text("Nombre") }, enabled = false, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = emp.apellido ?: "", onValueChange = {}, label = { Text("Apellido") }, enabled = false, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = emp.email ?: "", onValueChange = {}, label = { Text("Email") }, enabled = false, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = emp.telefono ?: "", onValueChange = {}, label = { Text("Teléfono") }, enabled = false, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = emp.direccion ?: "", onValueChange = {}, label = { Text("Dirección") }, enabled = false, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = emp.codigo ?: "", onValueChange = {}, label = { Text("Código") }, enabled = false, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = emp.fecha ?: "", onValueChange = {}, label = { Text("Fecha (YYYY-MM-DD)") }, enabled = false, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = emp.rol ?: "", onValueChange = {}, label = { Text("Rol") }, enabled = false, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = emp.ciudad ?: "", onValueChange = {}, label = { Text("Ciudad") }, enabled = false, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(24.dp))

        // ✅ Botón volver
        Button(
            onClick = onCerrar,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("← Volver", color = Color.White)
        }
    }
}
