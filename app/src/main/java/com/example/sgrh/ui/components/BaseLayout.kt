package com.example.sgrh.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.compose.ui.unit.dp

/**
 * Layout base para todas las pantallas con Sidebar.
 * Recibe:
 * - navController → para navegar entre pantallas
 * - rol → empleado, rrhh, gerente, supervisor
 * - onLogout → callback cuando se cierra sesión
 * - content → contenido específico de cada pantalla
 */
@Composable
fun BaseLayout(
    navController: NavHostController,
    rol: String,
    onLogout: () -> Unit,
    content: @Composable () -> Unit
) {
    Row(modifier = Modifier.fillMaxSize()) {
        // Sidebar fijo a la izquierda
        Sidebar(
            navController = navController,
            onLogout = onLogout,
            rol = rol
        )

        // Contenido dinámico a la derecha
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            content()
        }
    }
}
