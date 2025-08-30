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

    // --- Datos de ejemplo informes y permisos ---
    val informesEjemplo = listOf(
        Informe("I3", "Informe de RRHH Q2", "Resumen segundo trimestre", "2025-06-30")
    )

    val permisosEjemplo = listOf(
        Permiso("P3", "Mario Sánchez", "Vacaciones", "2025-08-10", "pendiente")
    )

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
            informes = informesEjemplo,
            onCrearInforme = { nombre, descripcion ->
                println("📌 Informe creado: $nombre - $descripcion")
            },
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

        "sistema" -> ConfiguracionSistema(
            empresaInicial = Empresa(),
            onGuardar = { /* TODO */ },
            onVolver = { opcionSeleccionada = null }
        )

        "registrarPersona" -> RegistrarPersonaScreen(onVolver = { opcionSeleccionada = null })

        else -> MenuOpcionesRrhh { seleccion -> opcionSeleccionada = seleccion }
    }
}
