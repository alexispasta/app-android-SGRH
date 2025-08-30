package com.example.sgrh.ui.pages.supervisor

import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.example.sgrh.ui.components.GestionAsistenciaScreen
import com.example.sgrh.ui.components.GestionNominaScreen
import com.example.sgrh.ui.components.GestionReportes
import com.example.sgrh.ui.components.GestionInformes
import com.example.sgrh.ui.components.GestionPermisos
import com.example.sgrh.ui.components.MenuOpcionesSupervisor
import com.example.sgrh.ui.components.EmpleadoReporte
import com.example.sgrh.ui.components.Reporte
import com.example.sgrh.ui.components.Informe
import com.example.sgrh.ui.components.Permiso

@Composable
fun SupervisorHomeScreen(
    usuarioId: String = "",
    empresaId: String = ""   // ✅ añadimos empresaId
) {
    var opcionSeleccionada by remember { mutableStateOf<String?>(null) }

    // -------- Datos de ejemplo empleados (para reportes) --------
    val empleadosReportesEjemplo = listOf(
        EmpleadoReporte(
            _id = "1",
            nombre = "Juan",
            apellido = "Pérez",
            codigo = "SUP001"
        ),
        EmpleadoReporte(
            _id = "2",
            nombre = "María",
            apellido = "García",
            codigo = "SUP002"
        )
    )

    // -------- Datos de ejemplo historial reportes --------
    val historialReportesEjemplo = listOf(
        Reporte(
            _id = "R1",
            asunto = "Llegada tardía",
            descripcion = "Empleado llegó 30 min tarde",
            empleadoId = "1",
            createdAt = "2025-08-24"
        )
    )

    // -------- Datos de ejemplo informes --------
    val informesEjemplo = listOf(
        Informe(
            _id = "I1",
            nombre = "Informe semanal",
            descripcion = "Resumen de asistencia semanal",
            createdAt = "2025-08-20"
        )
    )

    // -------- Datos de ejemplo permisos --------
    val permisosEjemplo = listOf(
        Permiso(
            _id = "P1",
            empleadoNombre = "Carlos López",
            motivo = "Cita médica",
            createdAt = "2025-08-22",
            estado = "pendiente"
        )
    )

    when (opcionSeleccionada) {
        "asistencia" -> GestionAsistenciaScreen(
            empresaId = empresaId,   // ✅ ahora sí se pasa
            onVolver = { opcionSeleccionada = null }
        )

        "nomina" -> GestionNominaScreen(
            onVolver = { opcionSeleccionada = null },
            empresaId = empresaId // 🔹 PASAMOS empresaId
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

        else -> MenuOpcionesSupervisor { seleccion -> opcionSeleccionada = seleccion }
    }
}
