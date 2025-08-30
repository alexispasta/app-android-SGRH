package com.example.sgrh.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sgrh.ui.models.Empleado

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmpleadoDetalleForm(
    empleado: Empleado,
    onCerrar: () -> Unit,
    onGuardar: (Empleado) -> Unit
) {
    // Usamos _id en vez de id
    val idValue = empleado._id
    var nombre by remember { mutableStateOf(empleado.nombre ?: "") }
    var apellido by remember { mutableStateOf(empleado.apellido ?: "") }
    var fecha by remember { mutableStateOf(empleado.fecha ?: "") }
    var rol by remember { mutableStateOf(empleado.rol ?: "") }
    var email by remember { mutableStateOf(empleado.email ?: "") }
    var telefono by remember { mutableStateOf(empleado.telefono ?: "") }
    var direccion by remember { mutableStateOf(empleado.direccion ?: "") }
    var codigo by remember { mutableStateOf(empleado.codigo ?: "") }
    var ciudad by remember { mutableStateOf(empleado.ciudad ?: "") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Información del empleado", style = MaterialTheme.typography.titleMedium)

        // ID (no editable)
        OutlinedTextField(
            value = idValue,
            onValueChange = { /* no editable */ },
            label = { Text("ID del empleado (no editable)") },
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = apellido,
            onValueChange = { apellido = it },
            label = { Text("Apellido") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = telefono,
            onValueChange = { telefono = it },
            label = { Text("Teléfono") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = direccion,
            onValueChange = { direccion = it },
            label = { Text("Dirección") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = codigo,
            onValueChange = { codigo = it },
            label = { Text("Código") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = fecha,
            onValueChange = { fecha = it },
            label = { Text("Fecha (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = rol,
            onValueChange = { rol = it },
            label = { Text("Rol") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = ciudad,
            onValueChange = { ciudad = it },
            label = { Text("Ciudad") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                val actualizado = empleado.copy(
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
                onGuardar(actualizado)
            }) {
                Text("Guardar cambios")
            }

            OutlinedButton(onClick = onCerrar) {
                Text("Volver")
            }
        }
    }
}
