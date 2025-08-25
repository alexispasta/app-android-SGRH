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
import com.example.sgrh.ui.components.EmpleadoReporte   // <- el modelo específico para reportes
import com.example.sgrh.ui.components.Reporte
import com.example.sgrh.ui.components.Informe

@Composable
fun GerenteHomeScreen(usuarioId: String = "") {
    var opcionSeleccionada by remember { mutableStateOf<String?>(null) }

    // -------- Datos de ejemplo empleados (para reportes) --------
    val empleadosReportesEjemplo = listOf(
        EmpleadoReporte(
            _id = "1",
            nombre = "Juan",
            apellido = "Pérez",
            codigo = "EMP001"
        ),
        EmpleadoReporte(
            _id = "2",
            nombre = "María",
            apellido = "García",
            codigo = "EMP002"
        )
    )

    // -------- Datos de ejemplo historial reportes --------
    val historialReportesEjemplo = listOf(
        Reporte(
            _id = "R1",
            asunto = "Falta injustificada",
            descripcion = "No se presentó el 12/08",
            empleadoId = "1",
            createdAt = "2025-08-24"
        )
    )

    // -------- Datos de ejemplo informes --------
    val informesEjemplo = listOf(
        Informe(
            _id = "I1",
            nombre = "Informe de desempeño Q1",
            descripcion = "Resumen del primer trimestre",
            createdAt = "2025-03-31"
        ),
        Informe(
            _id = "I2",
            nombre = "Informe de capacitación",
            descripcion = "Cursos completados por el equipo",
            createdAt = "2025-06-15"
        )
    )

    // -------- Datos de ejemplo permisos --------
    val permisosEjemplo = listOf(
        Permiso(
            _id = "P1",
            empleadoNombre = "Carlos López",
            motivo = "Cita médica",
            createdAt = "2025-08-20",
            estado = "pendiente"
        ),
        Permiso(
            _id = "P2",
            empleadoNombre = "Ana Torres",
            motivo = "Viaje familiar",
            createdAt = "2025-08-18",
            estado = "aprobado"
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

        else -> MenuOpcionesGerente { seleccion -> opcionSeleccionada = seleccion }
    }
}
