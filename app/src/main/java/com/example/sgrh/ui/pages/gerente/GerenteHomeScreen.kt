package com.example.sgrh.ui.pages.gerente

import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.example.sgrh.data.remote.RetrofitClient
import com.example.sgrh.ui.components.*
import com.example.sgrh.ui.components.MenuOpcionesGerente

@Composable
fun GerenteHomeScreen(
    usuarioId: String = "",
    empresaId: String = ""
) {
    var opcionSeleccionada by remember { mutableStateOf<String?>(null) }

    // -------- Datos de ejemplo informes y permisos --------
    val informesEjemplo = listOf(
        Informe("I1", "Informe de desempeño Q1", "Resumen del primer trimestre", "2025-03-31"),
        Informe("I2", "Informe de capacitación", "Cursos completados por el equipo", "2025-06-15")
    )
    val permisosEjemplo = listOf(
        Permiso("P1", "Carlos López", "Cita médica", "2025-08-20", "pendiente"),
        Permiso("P2", "Ana Torres", "Viaje familiar", "2025-08-18", "aprobado")
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
            onGuardar = { /* TODO guardar cambios */ },
            onVolver = { opcionSeleccionada = null }
        )

        "registrarPersona" -> RegistrarPersonaScreen(onVolver = { opcionSeleccionada = null })

        else -> MenuOpcionesGerente { seleccion -> opcionSeleccionada = seleccion }
    }
}
