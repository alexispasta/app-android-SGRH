package com.example.sgrh.ui.pages.rrhh

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
import com.example.sgrh.ui.components.MenuOpcionesRrhh
import com.example.sgrh.ui.components.EmpleadoReporte
import com.example.sgrh.ui.components.Reporte
import com.example.sgrh.ui.components.Informe

@Composable
fun RrhhHomeScreen(usuarioId: String = "") {
    var opcionSeleccionada by remember { mutableStateOf<String?>(null) }

    // -------- Datos de ejemplo empleados (para reportes) --------
    val empleadosReportesEjemplo = listOf(
        EmpleadoReporte(
            _id = "1",
            nombre = "Pedro",
            apellido = "Ramírez",
            codigo = "EMP101"
        ),
        EmpleadoReporte(
            _id = "2",
            nombre = "Lucía",
            apellido = "Fernández",
            codigo = "EMP102"
        )
    )

    // -------- Datos de ejemplo historial reportes --------
    val historialReportesEjemplo = listOf(
        Reporte(
            _id = "R2",
            asunto = "Llegada tardía",
            descripcion = "Empleado llegó tarde el 22/08",
            empleadoId = "1",
            createdAt = "2025-08-22"
        )
    )

    // -------- Datos de ejemplo informes --------
    val informesEjemplo = listOf(
        Informe(
            _id = "I3",
            nombre = "Informe de RRHH Q2",
            descripcion = "Resumen segundo trimestre",
            createdAt = "2025-06-30"
        )
    )

    // -------- Datos de ejemplo permisos --------
    val permisosEjemplo = listOf(
        Permiso(
            _id = "P3",
            empleadoNombre = "Mario Sánchez",
            motivo = "Vacaciones",
            createdAt = "2025-08-10",
            estado = "pendiente"
        )
    )

    when (opcionSeleccionada) {
        "empleados" -> EmpleadosTablaScreen(
            onVolver = { opcionSeleccionada = null }
        )

        "asistencia" -> GestionAsistenciaScreen(
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

        else -> MenuOpcionesRrhh { seleccion -> opcionSeleccionada = seleccion }
    }
}
