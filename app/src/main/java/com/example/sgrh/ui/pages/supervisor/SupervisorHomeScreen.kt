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


        "permisos" -> PermisosEmpleadoScreen(
            apiService = RetrofitClient.api,
            empleadoId = usuarioId,
            empleadoNombre = "Pedro Sánchez", // o traer de usuario
            empresaId = empresaId,
            onVolver = { opcionSeleccionada = null }
        )


        else -> MenuOpcionesSupervisor { seleccion -> opcionSeleccionada = seleccion }
    }
}
