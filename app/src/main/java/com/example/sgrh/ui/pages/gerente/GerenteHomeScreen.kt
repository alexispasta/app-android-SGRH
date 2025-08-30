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




    when (opcionSeleccionada) {
        "empleados" -> EmpleadosTablaScreen(
            apiService = RetrofitClient.api,   // 🔹 Necesario
            empresaId = empresaId,             // 🔹 Necesario
            onVolver = { opcionSeleccionada = null }
        )

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
            apiService = RetrofitClient.api,
            empresaId = empresaId, // el ID de la empresa del usuario logueado
            onVolver = { opcionSeleccionada = null }
        )



        "sistema" -> ConfiguracionSistema(
            empresaId = empresaId,           // ✅ Usamos el ID de la empresa que ya llega como parámetro
            apiService = RetrofitClient.api, // ✅ Tu instancia de ApiService
            onVolver = { opcionSeleccionada = null }
        )



        "registrarPersona" -> RegistrarPersonaScreen(
            apiService = RetrofitClient.api, // 🔹 Instancia de ApiService
            empresaId = empresaId,            // 🔹 ID de la empresa del gerente
            onVolver = { opcionSeleccionada = null }
        )

        else -> MenuOpcionesGerente { seleccion -> opcionSeleccionada = seleccion }

    }
}
