package com.example.sgrh

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import kotlinx.coroutines.runBlocking
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.junit.Rule
import org.junit.Test
import retrofit2.Response
import com.example.sgrh.data.remote.*
import com.example.sgrh.ui.components.GestionPermisos

@OptIn(ExperimentalTestApi::class)
class GestionPermisosTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // 🔹 Datos fake iniciales
    private val permisosFake = mutableListOf(
        Permiso(
            _id = "1",
            personaId = "p1",
            personaNombre = "Ana López",
            motivo = "Cita médica",
            estado = "pendiente",
            empresaId = "empresa123",
            createdAt = "2025-09-19",
            descripcion = "Consulta médica de rutina",
            updatedAt = "2025-09-19"
        ),
        Permiso(
            _id = "2",
            personaId = "p2",
            personaNombre = "Carlos Pérez",
            motivo = "Vacaciones",
            estado = "pendiente",
            empresaId = "empresa123",
            createdAt = "2025-09-18",
            descripcion = "Solicitud de vacaciones",
            updatedAt = "2025-09-18"
        )
    )

    // 🔹 Fake ApiService
    private val fakeApiService = object : ApiService {
        override suspend fun getPermisosPorEmpresa(empresaId: String): Response<List<Permiso>> =
            Response.success(permisosFake.toList())

        override suspend fun actualizarPermiso(id: String, request: Map<String, String>): Response<GenericResponse> {
            val index = permisosFake.indexOfFirst { it._id == id }
            return if (index != -1) {
                permisosFake[index] = permisosFake[index].copy(estado = request["estado"] ?: "pendiente")
                Response.success(GenericResponse(message = "Actualizado"))
            } else {
                Response.error(404, okhttp3.ResponseBody.create(null, "Not Found"))
            }
        }

        override suspend fun eliminarPermiso(id: String): Response<Unit> {
            permisosFake.removeIf { it._id == id }
            return Response.success(Unit)
        }

        override suspend fun eliminarTodosPermisos(empresaId: String): Response<Unit> {
            permisosFake.clear()
            return Response.success(Unit)
        }

        // 🚫 Métodos no usados
        override suspend fun login(request: LoginRequest) = TODO()
        override suspend fun getEmpleados(empresaId: String) = TODO()
        override suspend fun getHistorial(empresaId: String) = TODO()
        override suspend fun getAsistenciaPorFecha(empresaId: String, fecha: String) = TODO()
        override suspend fun guardarAsistencia(registros: List<AsistenciaRequest>) = TODO()
        override suspend fun eliminarAsistenciaPorFecha(empresaId: String, fecha: String) = TODO()
        override suspend fun eliminarHistorialCompleto(empresaId: String) = TODO()
        override suspend fun getNominas(empresaId: String) = TODO()
        override suspend fun crearNomina(nomina: Nomina) = TODO()
        override suspend fun actualizarNomina(id: String, nomina: Nomina) = TODO()
        override suspend fun eliminarNomina(id: String) = TODO()
        override suspend fun getReportesPorEmpresa(empresaId: String) = TODO()
        override suspend fun crearReporte(reporte: ReporteRequest) = TODO()
        override suspend fun getReporteById(id: String) = TODO()
        override suspend fun eliminarReporte(id: String) = TODO()
        override suspend fun eliminarTodosReportes(empresaId: String) = TODO()
        override suspend fun getInforme(id: String) = TODO()
        override suspend fun getInformesPorEmpresa(empresaId: String) = TODO()
        override suspend fun crearInforme(request: InformeRequest) = TODO()
        override suspend fun eliminarInforme(id: String) = TODO()
        override suspend fun eliminarInformesPorEmpresa(empresaId: String) = TODO()
        override suspend fun getPermisosPorEmpleado(empleadoId: String) = TODO()
        override suspend fun crearPermiso(request: PermisoRequest) = TODO()
        override suspend fun actualizarEmpleado(id: String, empleado: Empleado) = TODO()
        override suspend fun getEmpresaById(empresaId: String) = TODO()
        override suspend fun actualizarEmpresa(empresaId: String, empresa: Empresa) = TODO()
        override suspend fun crearEmpleado(persona: RegistroPersona) = TODO()
        override suspend fun getEmpleadoById(empleadoId: String) = TODO()
        override suspend fun eliminarEmpleado(id: String) = TODO()
        override suspend fun enviarQueja(request: QuejaRequest) = TODO()
        override suspend fun registrarEmpresa(request: RegistrarEmpresaRequest) = TODO()
        override suspend fun eliminarEmpresa(empresaId: String) = TODO()
        override suspend fun getCertificadosPorPersona(personaId: String) = TODO()
        override suspend fun registrarCertificado(personaId: RequestBody, empresaId: RequestBody, nombre: RequestBody, archivo: MultipartBody.Part) = TODO()
        override suspend fun eliminarCertificado(id: String) = TODO()
    }

    // 🔹 Tests corregidos
    @Test
    fun cargaPermisosYAprobarlos() {
        runBlocking {
            composeTestRule.setContent {
                GestionPermisos(fakeApiService, "empresa123") {}
            }

            // Esperar que los permisos se carguen
            composeTestRule.waitUntil(timeoutMillis = 10_000) {
                composeTestRule.onAllNodesWithText("Ana López").fetchSemanticsNodes().isNotEmpty()
            }

            // Asegurarse de que Compose haya recomposed
            composeTestRule.waitForIdle()

            composeTestRule.onNodeWithText("Ana López").assertIsDisplayed()
            composeTestRule.onNodeWithText("Carlos Pérez").assertIsDisplayed()

            // Aprobar primer permiso
            composeTestRule.onAllNodesWithText("Aprobar")[0].performClick()

            // Esperar mensaje de aprobación
            composeTestRule.waitUntil(timeoutMillis = 10_000) {
                composeTestRule.onAllNodesWithText("Permiso aprobado correctamente").fetchSemanticsNodes().isNotEmpty()
            }

            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithText("Permiso aprobado correctamente").assertIsDisplayed()
        }
    }


    @Test
    fun rechazarPermiso() {
        runBlocking {
            composeTestRule.setContent {
                GestionPermisos(fakeApiService, "empresa123") {}
            }

            composeTestRule.waitUntil(timeoutMillis = 5_000) {
                composeTestRule.onAllNodesWithText("Rechazar").fetchSemanticsNodes().isNotEmpty()
            }

            composeTestRule.onAllNodesWithText("Rechazar")[0].performClick()

            composeTestRule.waitUntil(timeoutMillis = 5_000) {
                composeTestRule.onAllNodesWithText("Permiso rechazado correctamente").fetchSemanticsNodes().isNotEmpty()
            }

            composeTestRule.onNodeWithText("Permiso rechazado correctamente").assertIsDisplayed()
        }
    }

    @Test
    fun eliminarPermiso() {
        runBlocking {
            composeTestRule.setContent {
                GestionPermisos(fakeApiService, "empresa123") {}
            }

            composeTestRule.waitUntil(timeoutMillis = 5_000) {
                composeTestRule.onAllNodesWithText("Eliminar").fetchSemanticsNodes().isNotEmpty()
            }

            composeTestRule.onAllNodesWithText("Eliminar")[0].performClick()

            composeTestRule.waitUntil(timeoutMillis = 5_000) {
                composeTestRule.onAllNodesWithText("Permiso eliminado correctamente").fetchSemanticsNodes().isNotEmpty()
            }

            composeTestRule.onNodeWithText("Permiso eliminado correctamente").assertIsDisplayed()
        }
    }

    @Test
    fun eliminarTodosPermisos() {
        runBlocking {
            composeTestRule.setContent {
                GestionPermisos(fakeApiService, "empresa123") {}
            }

            // Esperar que el botón aparezca
            composeTestRule.waitUntil(timeoutMillis = 10_000) {
                composeTestRule.onAllNodesWithText("🗑 Eliminar TODOS los permisos").fetchSemanticsNodes().isNotEmpty()
            }

            // Click en eliminar todos
            composeTestRule.onNodeWithText("🗑 Eliminar TODOS los permisos").performClick()

            // Esperar a que Compose recomposite
            composeTestRule.waitForIdle()

            // Esperar mensaje de confirmación
            composeTestRule.waitUntil(timeoutMillis = 10_000) {
                composeTestRule.onAllNodesWithText("✅ Todos los permisos eliminados").fetchSemanticsNodes().isNotEmpty()
            }

            composeTestRule.onNodeWithText("✅ Todos los permisos eliminados").assertIsDisplayed()

            // Esperar recomposición antes de contar
            composeTestRule.waitForIdle()
            composeTestRule.onAllNodesWithText("Empleado:").assertCountEquals(0)
        }
    }

}
