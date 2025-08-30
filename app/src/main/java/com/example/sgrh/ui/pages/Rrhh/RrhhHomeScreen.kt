package com.example.sgrh.ui.pages.rrhh

import androidx.compose.runtime.*
import com.example.sgrh.data.remote.RetrofitClient
import com.example.sgrh.ui.components.*

@Composable
fun RrhhHomeScreen(
    usuarioId: String = "",
    empresaId: String = ""
) {
    var opcionSeleccionada by remember { mutableStateOf<String?>(null) }



    when (opcionSeleccionada) {
        "empleados" -> EmpleadosTablaScreen(onVolver = { opcionSeleccionada = null })

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


        "sistema" -> ConfiguracionSistema(
            empresaInicial = Empresa(),
            onGuardar = { /* TODO */ },
            onVolver = { opcionSeleccionada = null }
        )

        "registrarPersona" -> RegistrarPersonaScreen(onVolver = { opcionSeleccionada = null })

        else -> MenuOpcionesRrhh { seleccion -> opcionSeleccionada = seleccion }
    }
}
