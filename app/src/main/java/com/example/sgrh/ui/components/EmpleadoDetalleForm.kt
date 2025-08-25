package com.example.sgrh.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType
import com.example.sgrh.ui.models.Empleado

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmpleadoDetalleForm(
    empleado: Empleado,
    onCerrar: () -> Unit,
    onGuardar: (Empleado) -> Unit
) {
    // Normalizamos valores nullable a defaults para evitar errores de tipo
    val idValue = empleado.id
    var nombre by remember { mutableStateOf(empleado.nombre ?: "") }
    var apellido by remember { mutableStateOf(empleado.apellido ?: "") }
    var fecha by remember { mutableStateOf(empleado.fecha ?: "") }
    var rol by remember { mutableStateOf(empleado.rol ?: "") }
    var email by remember { mutableStateOf(empleado.email ?: "") }
    var salarioStr by remember { mutableStateOf((empleado.salario ?: 0).toString()) }
    var cargo by remember { mutableStateOf(empleado.cargo ?: "") }
    var eps by remember { mutableStateOf(empleado.eps ?: "") }

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
            label = { Text("Apellidos / Documento") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = fecha,
            onValueChange = { fecha = it },
            label = { Text("Fecha (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth()
        )

        // Dropdown para rol/estado (ejemplo simple)
        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = rol,
                onValueChange = { /* read only */ },
                label = { Text("Rol / Estado") },
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                listOf("activo", "inactivo").forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion) },
                        onClick = {
                            rol = opcion
                            expanded = false
                        }
                    )
                }
            }
        }



        OutlinedTextField(
            value = cargo,
            onValueChange = { cargo = it },
            label = { Text("Cargo") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = eps,
            onValueChange = { eps = it },
            label = { Text("EPS") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                val salarioInt = salarioStr.toIntOrNull() ?: (empleado.salario ?: 0)

                val actualizado = empleado.copy(
                    nombre = nombre,
                    apellido = apellido,
                    fecha = if (fecha.isBlank()) null else fecha,
                    rol = rol,
                    email = email,
                    salario = salarioInt,
                    cargo = cargo,
                    eps = eps
                )
                onGuardar(actualizado)
            }) {
                Text("Guardar cambios")
            }

            OutlinedButton(onClick = onCerrar) {
                Text("Volver")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { /* abrir file picker */ }) { Text("Cargar documentos") }
            OutlinedButton(onClick = { /* descargar documentos */ }) { Text("Descargar documentos") }
        }
    }
}
