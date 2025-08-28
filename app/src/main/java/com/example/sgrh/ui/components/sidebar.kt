package com.example.sgrh.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseLayout(
    navController: NavHostController,
    rol: String, // 🔹 ahora obligatorio para que no se pierda el rol
    onLogout: () -> Unit,
    content: @Composable () -> Unit
) {
    Scaffold(
        topBar = {
            Topbar(
                navController = navController,
                onLogout = onLogout,
                rol = rol
            )
        }
    ) { paddingValues ->
        // Contenido dinámico debajo del TopBar
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            content()
        }
    }
}
