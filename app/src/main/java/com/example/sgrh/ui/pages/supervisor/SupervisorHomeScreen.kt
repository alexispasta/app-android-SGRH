package com.example.sgrh.ui.pages.supervisor

import androidx.compose.runtime.*
import com.example.sgrh.data.remote.RetrofitClient
import com.example.sgrh.ui.components.*

@Composable
fun SupervisorHomeScreen(
    usuarioId: String = "",
    empresaId: String = ""   // recibimos empresaId
) {
    var opcionSeleccionada by remember { mutableStateOf<String?>(null) }



    val permisosEjemplo = listOf(
        Permiso("P1", "Carlos López", "Cita médica", "2025-08-22", "pendiente")
    )

    when (opcionSeleccionada) {
        "asistencia" -> GestionAsistenciaScreen(
            empresaId = empresaId,
            onVolver = { opcionSeleccionada = null }
        )

        "nomina" -> GestionNominaScreen(
            empresaId = empresaId,
            onVolver = { opcionSeleccionada = null }
        )

        "reportes" -> GestionReportes(
            empresaId = empresaId,
            apiService = RetrofitClient.api,
            onVolver = { opcionSeleccionada = null }
        )

        "informes" -> GestionInformes(
            empresaId = empresaId,
            apiService = RetrofitClient.api,
            onRevisar = { informe ->
                println("👀 Revisando informe: ${informe._id} - ${informe.nombre}")
            },
            onVolver = { opcionSeleccionada = null }
        )


        "permisos" -> GestionPermisos(
            permisos = permisosEjemplo,
            onAccion = { id, nuevoEstado ->
                println("📌 Permiso $id actualizado a $nuevoEstado")
            },
            onVolver = { opcionSeleccionada = null }
        )

        else -> MenuOpcionesSupervisor { seleccion -> opcionSeleccionada = seleccion }
    }
}
