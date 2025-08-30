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
import com.example.sgrh.ui.components.MenuOpcionesEmpleado
import com.example.sgrh.data.remote.RetrofitClient


@Composable
fun EmpleadoHomeScreen(
    usuarioId: String = "",
    empresaId: String = ""   // ✅ ahora también recibe empresaId
) {
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
                InformacionCuentaForm(
                    usuarioId = usuarioId,
                    onBack = { opcionSeleccionada = null }
                )
            }
            "permisos" -> PermisosEmpleadoScreen(
                apiService = RetrofitClient.api,
                empleadoId = usuarioId,
                empleadoNombre = "Pedro Sánchez", // o traer de usuario
                empresaId = empresaId,
                onVolver = { opcionSeleccionada = null }
            )

            "certificacion" -> {
                RegistroCertificacion(
                    onVolver = { opcionSeleccionada = null }
                )
            }
            else -> {
                MenuOpcionesEmpleado { seleccion -> opcionSeleccionada = seleccion }
            }
        }
    }
}
