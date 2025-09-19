package com.example.sgrh

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.sgrh.data.remote.*
import com.example.sgrh.ui.components.InformesScreen
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class MainActivity : ComponentActivity() {

    // 🔹 Fake ApiService para pruebas locales (solo Informes)
    private val fakeApiService = object : ApiService {
        private val informes = mutableListOf<Informe>(
            Informe(
                _id = "1",
                nombre = "Informe inicial",
                descripcion = "Prueba",
                empresaId = "empresa123",
                createdAt = "2025-09-19",
                updatedAt = "2025-09-19"
            )
        )

        override suspend fun getInformesPorEmpresa(empresaId: String): Response<List<Informe>> {
            return Response.success(informes.toList())
        }

        override suspend fun crearInforme(request: InformeRequest): Response<Informe> {
            val nuevo = Informe(
                _id = (informes.size + 1).toString(),
                nombre = request.nombre,
                descripcion = request.descripcion,
                empresaId = "empresa123", // ⚡ agrega este si Informe lo pide
                createdAt = "2025-09-19",
                updatedAt = "2025-09-19"  // ⚡ agrega este si es obligatorio
            )
            informes.add(nuevo)
            return Response.success(nuevo)
        }

        override suspend fun eliminarInforme(id: String): Response<GenericResponse> {
            val eliminado = informes.removeIf { it._id == id }
            return Response.success(
                GenericResponse(
                    message = if (eliminado) "Eliminado" else "No encontrado"
                )
            )
        }

        override suspend fun eliminarInformesPorEmpresa(empresaId: String): Response<GenericResponse> {
            val count = informes.size
            informes.clear()
            return Response.success(
                GenericResponse(
                    message = "Eliminados $count"
                )
            )
        }


        // 🔹 Métodos no usados en Informes -> los dejamos como TODO()
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            InformesScreen(
                empresaId = "empresa123",
                apiService = fakeApiService,
                onVolverMenu = {}
            )
        }
    }
}

