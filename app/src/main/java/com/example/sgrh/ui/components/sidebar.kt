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
fun Sidebar(
    navController: NavHostController,
    onLogout: () -> Unit,
    rol: String? = "empleado" // se puede pasar desde el login
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
            .fillMaxHeight()
            .width(220.dp)
            .background(Color.DarkGray)
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "SGRH",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Divider(color = Color.LightGray, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))

            SidebarItem("Página de inicio") {
                navController.navigate(rutaInicio) {
                    popUpTo(rutaInicio) { inclusive = true }
                }
            }
            SidebarItem("Quejas y sugerencias") {
                navController.navigate("quejas")
            }
            SidebarItem("Información de Cuenta") {
                navController.navigate("informacion")
            }
        }

        SidebarItem("Salir") {
            onLogout()
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
        }
    }
}

@Composable
fun SidebarItem(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        color = Color.White,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() }
            .background(Color.Transparent, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    )
}
