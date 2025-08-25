package com.example.sgrh.ui.pages.empleado

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sgrh.ui.components.InformacionCuentaForm
import com.example.sgrh.ui.components.PermisosEmpleadoScreen
import com.example.sgrh.ui.components.RegistroCertificacion
import com.example.sgrh.ui.components.MenuOpcionesEmpleado   // ✅ usamos el componente modular

@Composable
fun EmpleadoHomeScreen(usuarioId: String = "") {   // 👈 recibe el id del usuario logueado
    var opcionSeleccionada by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Panel del Empleado", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        when (opcionSeleccionada) {
            "consultar" -> {
                // ✅ ahora sí pasamos el id
                InformacionCuentaForm(
                    usuarioId = usuarioId,
                    onBack = { opcionSeleccionada = null }
                )
            }
            "permisos" -> {
                PermisosEmpleadoScreen(onVolver = { opcionSeleccionada = null })
            }
            "certificacion" -> {
                RegistroCertificacion(onVolver = { opcionSeleccionada = null })
            }
            else -> {
                MenuOpcionesEmpleado { seleccion -> opcionSeleccionada = seleccion }
            }
        }
    }
}

