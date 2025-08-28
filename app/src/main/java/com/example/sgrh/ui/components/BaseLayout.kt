package com.example.sgrh.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun Topbar(
    navController: NavHostController,
    onLogout: () -> Unit,
    rol: String = "empleado"
) {
    val rutasInicio = mapOf(
        "empleado" to "empleadoInicio",
        "rrhh" to "rrhhInicio",
        "gerente" to "gerenteInicio",
        "supervisor" to "supervisorInicio"
    )
    val rutaInicio = rutasInicio[rol] ?: "empleadoInicio"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.DarkGray)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        // Logo / título arriba
        Text(
            text = "SGRH",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        // Menú de navegación debajo
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TopbarItem("Inicio") {
                navController.navigate(rutaInicio) {
                    launchSingleTop = true  // evita duplicados y respeta el rol actual
                }
            }
            TopbarItem("Quejas") {
                navController.navigate("quejas") {
                    launchSingleTop = true
                }
            }
            TopbarItem("Cuenta") {
                navController.navigate("informacion") {
                    launchSingleTop = true
                }
            }
            TopbarItem("Salir") {
                onLogout()
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }
}

@Composable
fun TopbarItem(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        color = Color.White,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier
            .clickable { onClick() }
            .background(Color.Transparent, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    )
}
