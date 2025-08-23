// RolesSelectorView.kt
package com.example.sgrh.ui.pages.roles

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RolesSelectorView(
    onSeleccionar: (String) -> Unit,
    onVolver: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Elija el rol con que desea ingresar", style = MaterialTheme.typography.headlineSmall)

            Button(onClick = { onSeleccionar("empleado") }, modifier = Modifier.fillMaxWidth()) {
                Text("Empleado")
            }
            Button(onClick = { onSeleccionar("rrhh") }, modifier = Modifier.fillMaxWidth()) {
                Text("RRHH")
            }
            Button(onClick = { onSeleccionar("gerente") }, modifier = Modifier.fillMaxWidth()) {
                Text("Gerente")
            }
            Button(onClick = { onSeleccionar("supervisor") }, modifier = Modifier.fillMaxWidth()) {
                Text("Supervisor")
            }

            TextButton(onClick = onVolver) { Text("← Volver al inicio de sesión") }
        }
    }
}
