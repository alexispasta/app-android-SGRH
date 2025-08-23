package com.example.sgrh.ui.pages.roles

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun RolesSelectorScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Elija el rol con que desea ingresar", style = MaterialTheme.typography.headlineSmall)

            Button(onClick = {
                navController.navigate("empleadoInicio") {
                    popUpTo("login") { inclusive = true }
                    launchSingleTop = true
                }
            }, modifier = Modifier.fillMaxWidth()) { Text("Empleado") }

            Button(onClick = {
                navController.navigate("rrhhInicio") {
                    popUpTo("login") { inclusive = true }
                    launchSingleTop = true
                }
            }, modifier = Modifier.fillMaxWidth()) { Text("RRHH") }

            Button(onClick = {
                navController.navigate("gerenteInicio") {
                    popUpTo("login") { inclusive = true }
                    launchSingleTop = true
                }
            }, modifier = Modifier.fillMaxWidth()) { Text("Gerente") }

            Button(onClick = {
                navController.navigate("supervisorInicio") {
                    popUpTo("login") { inclusive = true }
                    launchSingleTop = true
                }
            }, modifier = Modifier.fillMaxWidth()) { Text("Supervisor") }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = { navController.navigate("login") }) {
                Text("← Volver al inicio de sesión")
            }
        }
    }
}
