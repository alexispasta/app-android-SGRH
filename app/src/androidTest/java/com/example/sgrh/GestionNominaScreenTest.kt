package com.example.sgrh

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.sgrh.data.remote.*
import com.example.sgrh.ui.components.GestionNominaScreen
import kotlinx.coroutines.runBlocking
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

class GestionNominaScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // 🔹 Fake ApiService para nómina
    private val fakeApiService = object : ApiService {

        private val empleados = listOf(
            EmpleadoAsistencia(_id = "1", nombre = "Ana", apellido = "López", documento = "111", codigo = "111"),
            EmpleadoAsistencia(_id = "2", nombre = "Carlos", apellido = "Pérez", documento = "222", codigo = "222")
        )

        private val nominas = mutableListOf<Nomina>()

        override suspend fun getEmpleados(empresaId: String): Response<List<EmpleadoAsistencia>> {
            return Response.success(empleados)
        }

        override suspend fun getNominas(empresaId: String): Response<List<Nomina>> {
            return Response.success(nominas.toList())
        }

        override suspend fun crearNomina(nomina: Nomina): Response<Nomina> {
            val nuevo = nomina.copy(_id = (nominas.size + 1).toString())
            nominas.add(nuevo)
            return Response.success(nuevo)
        }

        override suspend fun actualizarNomina(id: String, nomina: Nomina): Response<Nomina> {
            val index = nominas.indexOfFirst { it._id == id }
            if (index != -1) {
                nominas[index] = nomina
            }
            return Response.success(nomina)
        }

        // 🚫 No usados en este test
        override suspend fun login(request: LoginRequest) = TODO()
        override suspend fun getHistorial(empresaId: String) = TODO()
        override suspend fun getAsistenciaPorFecha(empresaId: String, fecha: String) = TODO()
        override suspend fun guardarAsistencia(registros: List<AsistenciaRequest>) = TODO()
        override suspend fun eliminarAsistenciaPorFecha(empresaId: String, fecha: String) = TODO()
        override suspend fun eliminarHistorialCompleto(empresaId: String) = TODO()
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
        override suspend fun actualizarPermiso(id: String, request: Map<String, String>) = TODO()
        override suspend fun getPermisosPorEmpresa(empresaId: String) = TODO()
        override suspend fun eliminarPermiso(id: String) = TODO()
        override suspend fun eliminarTodosPermisos(empresaId: String) = TODO()
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
        override suspend fun registrarCertificado(
            personaId: RequestBody,
            empresaId: RequestBody,
            nombre: RequestBody,
            archivo: MultipartBody.Part
        ) = TODO()
        override suspend fun eliminarCertificado(id: String) = TODO()
    }

    @Test
    fun muestraEmpleadosYAbreFormulario() {
        runBlocking {
            composeTestRule.setContent {
                GestionNominaScreen(
                    onVolver = {},
                    empresaId = "empresa123",
                    apiService = fakeApiService // 👈 se inyecta el fake
                )
            }

            // 🔹 Verifica que aparecen los botones de empleados
            composeTestRule.onNodeWithText("Ana López").assertIsDisplayed()
            composeTestRule.onNodeWithText("Carlos Pérez").assertIsDisplayed()

            // 🔹 Click en un empleado
            composeTestRule.onNodeWithText("Ana López").performClick()

            // 🔹 Ahora debería mostrarse el formulario con "Información de Nómina"
            composeTestRule.onNodeWithText("Información de Nómina").assertIsDisplayed()

            // 🔹 Cambiar campo "Cuenta Bancaria"
            composeTestRule.onNodeWithText("Cuenta Bancaria").performTextInput("1234567890")

            // 🔹 Guardar cambios
            composeTestRule.onNodeWithText("Guardar Cambios").performClick()
        }
    }
}
