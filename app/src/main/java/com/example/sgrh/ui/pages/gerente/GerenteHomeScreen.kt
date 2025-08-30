package com.example.sgrh.ui.pages.gerente

import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.example.sgrh.ui.components.GestionAsistenciaScreen
import com.example.sgrh.ui.components.GestionNominaScreen
import com.example.sgrh.ui.components.GestionReportes
import com.example.sgrh.ui.components.GestionInformes
import com.example.sgrh.ui.components.ConfiguracionSistema
import com.example.sgrh.ui.components.GestionPermisos
import com.example.sgrh.ui.components.Permiso
import com.example.sgrh.ui.components.RegistrarPersonaScreen
import com.example.sgrh.ui.components.EmpleadosTablaScreen
import com.example.sgrh.ui.components.Empresa
import com.example.sgrh.ui.components.MenuOpcionesGerente
import com.example.sgrh.ui.components.EmpleadoReporte
import com.example.sgrh.ui.components.Reporte
import com.example.sgrh.ui.components.Informe

@Composable
fun GerenteHomeScreen(
    usuarioId: String = "",
    empresaId: String = ""   // ✅ ahora también recibe empresaId
) {
    var opcionSeleccionada by remember { mutableStateOf<String?>(null) }

    // -------- Datos de ejemplo empleados (para reportes) --------
    val empleadosReportesEjemplo = listOf(
        EmpleadoReporte("1", "Juan", "Pérez", "EMP001"),
        EmpleadoReporte("2", "María", "García", "EMP002")
    )

    // -------- Datos de ejemplo historial reportes --------
    val historialReportesEjemplo = listOf(
        Reporte("R1", "Falta injustificada", "No se presentó el 12/08", "1", "2025-08-24")
    )

    // -------- Datos de ejemplo informes --------
    val informesEjemplo = listOf(
        Informe("I1", "Informe de desempeño Q1", "Resumen del primer trimestre", "2025-03-31"),
        Informe("I2", "Informe de capacitación", "Cursos completados por el equipo", "2025-06-15")
    )

    // -------- Datos de ejemplo permisos --------
    val permisosEjemplo = listOf(
        Permiso("P1", "Carlos López", "Cita médica", "2025-08-20", "pendiente"),
        Permiso("P2", "Ana Torres", "Viaje familiar", "2025-08-18", "aprobado")
    )

    when (opcionSeleccionada) {
        "empleados" -> EmpleadosTablaScreen(
            onVolver = { opcionSeleccionada = null }
        )

        "asistencia" -> GestionAsistenciaScreen(
            empresaId = empresaId,   // ✅ ahora se pasa empresaId
            onVolver = { opcionSeleccionada = null }
        )

        "nomina" -> GestionNominaScreen(
            onVolver = { opcionSeleccionada = null }
        )

        "reportes" -> GestionReportes(
            empleados = empleadosReportesEjemplo,
            historial = historialReportesEjemplo,
            onEnviarReporte = { empleadoId, asunto, descripcion ->
                println("📌 Reporte enviado: $empleadoId - $asunto - $descripcion")
            },
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

        "registrarPersona" -> RegistrarPersonaScreen(
            onVolver = { opcionSeleccionada = null }
        )

        else -> MenuOpcionesGerente { seleccion -> opcionSeleccionada = seleccion }
    }
}
