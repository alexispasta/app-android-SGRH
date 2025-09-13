package com.example.sgrh.data.remote

import retrofit2.Response
import retrofit2.http.*
import okhttp3.MultipartBody
import okhttp3.RequestBody


data class LoginRequest(val email: String, val password: String)

data class Usuario(
    val _id: String,
    val rol: String,
    val empresaId: String?
)

data class ApiResponse(
    val message: String
)

data class LoginResponse(
    val usuario: Usuario?,
    val message: String?,
    val error: String?
)

data class EmpleadoAsistencia(
    val _id: String,
    val nombre: String,
    val apellido: String,
    val codigo: String?,
    val documento: String?
)

data class AsistenciaRequest(
    val documento: String,
    val fecha: String,
    val estado: String,
    val empresaId: String
)

data class GenericResponse(val message: String)

// 🔹 Modelos de Nómina
data class EmpleadoNomina(
    val _id: String,
    val nombre: String,
    val apellido: String,
    val codigo: String
)

data class Nomina(
    val _id: String? = null,
    val nombre: String,
    val cedula: String,
    val cuenta: String,
    val salario: Double,
    val auxilio: Double,
    val horasExtra: Double,
    val bonificacion: Double,
    val descuentos: Double,
    val empresaId: String
)

// 🔹 Modelo de Reportes
data class EmpleadoReporte(
    val _id: String,
    val nombre: String,
    val apellido: String,
    val codigo: String
)

data class Reporte(
    val _id: String,
    val asunto: String,
    val descripcion: String,
    val personaId: EmpleadoReporte,   // 🔹 corregido
    val createdAt: String
)

data class ReporteRequest(
    val asunto: String,
    val descripcion: String,
    val personaId: String,   // ✅ cambiado de empleadoId a personaId
    val empresaId: String
)

// 🔹 Informes
data class Informe(
    val _id: String,
    val nombre: String,
    val descripcion: String?,
    val empresaId: String,
    val createdAt: String,
    val updatedAt: String
)

data class InformeRequest(
    val nombre: String,
    val descripcion: String?,
    val empresaId: String
)

data class Permiso(
    val _id: String,
    val personaId: String,       // 🔹 coincide con backend
    val personaNombre: String,   // 🔹 coincide con backend
    val motivo: String,
    val descripcion: String?,
    val estado: String,
    val empresaId: String,       // 🔹 también lo devuelve el backend
    val createdAt: String,
    val updatedAt: String        // 🔹 lo agrega mongoose automáticamente
)


data class PermisoRequest(
    val personaId: String,       // 🔹 debe ser personaId, no empleadoId
    val personaNombre: String,   // opcional (backend ya lo reconstruye, pero puede ir)
    val motivo: String,
    val descripcion: String?,
    val estado: String = "pendiente",
    val empresaId: String
)


data class Empleado(
    val _id: String,
    val nombre: String,
    val apellido: String,
    val email: String,
    val telefono: String?,
    val direccion: String?,
    val codigo: String?,
    val rol: String,
    val fecha: String?,
    val ciudad: String?,
    val empresaId: String
)

data class Empresa(
    var nombre: String = "",
    var pais: String = "",
    var correo: String = "",
    var ciudad: String = "",
    var telefono: String = "",
    var direccion: String = ""
)

data class QuejaRequest(
    val asunto: String,
    val mensaje: String
)

// 🔹 RegistrarEmpresaRequest.kt
data class RegistrarEmpresaRequest(
    val nombreEmpresa: String,
    val correoEmpresa: String,
    val pais: String,
    val telefonoEmpresa: String,
    val direccionEmpresa: String,

    val nombrePersona: String,
    val apellido: String,
    val email: String,
    val passwordPersona: String,
    val telefonoPersona: String,
    val direccionPersona: String,
    val fecha: String,
    val ciudad: String,
    val codigo: String   // Identificación del gerente
)


data class RegistrarEmpresaResponse(
    val message: String,
    val empresaId: String?
)

data class Certificado(
    val _id: String,
    val personaId: String,
    val empresaId: String,
    val nombre: String,
    val archivoUrl: String?,
    val fecha: String
)


interface ApiService {
    @POST("/api/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("/api/personas/empresa/{empresaId}")
    suspend fun getEmpleados(@Path("empresaId") empresaId: String): Response<List<EmpleadoAsistencia>>

    // ---------------- ASISTENCIA ----------------
    @GET("/api/asistencia/historial/{empresaId}")
    suspend fun getHistorial(@Path("empresaId") empresaId: String): Response<List<String>>

    @GET("/api/asistencia/{empresaId}/{fecha}")
    suspend fun getAsistenciaPorFecha(
        @Path("empresaId") empresaId: String,
        @Path("fecha") fecha: String
    ): Response<List<AsistenciaRequest>>

    @POST("/api/asistencia")
    suspend fun guardarAsistencia(@Body registros: List<AsistenciaRequest>): Response<GenericResponse>

    // 🆕 Eliminar asistencia de una fecha específica
    @DELETE("/api/asistencia/{empresaId}/{fecha}")
    suspend fun eliminarAsistenciaPorFecha(
        @Path("empresaId") empresaId: String,
        @Path("fecha") fecha: String
    ): Response<ApiResponse>

    // 🆕 Eliminar historial completo
    @DELETE("/api/asistencia/historial/{empresaId}")
    suspend fun eliminarHistorialCompleto(@Path("empresaId") empresaId: String): Response<ApiResponse>

    // 🔹 Endpoints de Nómina
    @GET("/api/nomina/empresa/{empresaId}")
    suspend fun getNominas(@Path("empresaId") empresaId: String): Response<List<Nomina>>

    @POST("/api/nomina")
    suspend fun crearNomina(@Body nomina: Nomina): Response<Nomina>

    @PUT("/api/nomina/{id}")
    suspend fun actualizarNomina(@Path("id") id: String, @Body nomina: Nomina): Response<Nomina>

    @DELETE("/api/nomina/{id}")
    suspend fun eliminarNomina(@Path("id") id: String): Response<GenericResponse>

    // 🔹 Endpoints de Reportes
    @GET("/api/reportes/empresa/{empresaId}")
    suspend fun getReportesPorEmpresa(@Path("empresaId") empresaId: String): Response<List<Reporte>>

    @POST("/api/reportes")
    suspend fun crearReporte(@Body reporte: ReporteRequest): Response<Reporte>

    // 🆕 Consultar un reporte por ID
    @GET("/api/reportes/{id}")
    suspend fun getReporteById(@Path("id") id: String): Response<Reporte>

    // 🆕 Eliminar un reporte por ID
    @DELETE("/api/reportes/{id}")
    suspend fun eliminarReporte(@Path("id") id: String): Response<GenericResponse>

    // 🆕 Eliminar todos los reportes de la empresa
    @DELETE("/api/reportes/empresa/{empresaId}")
    suspend fun eliminarTodosReportes(@Path("empresaId") empresaId: String): Response<GenericResponse>


    // ---------------- INFORMES ----------------
    @GET("/api/informes/empresa/{empresaId}")
    suspend fun getInformesPorEmpresa(@Path("empresaId") empresaId: String): Response<List<Informe>>

    @GET("/api/informes/{id}")
    suspend fun getInforme(@Path("id") id: String): Response<Informe>

    @POST("/api/informes")
    suspend fun crearInforme(@Body request: InformeRequest): Response<Informe>

    @DELETE("/api/informes/{id}")
    suspend fun eliminarInforme(@Path("id") id: String): Response<GenericResponse>

    @DELETE("/api/informes/empresa/{empresaId}")
    suspend fun eliminarInformesPorEmpresa(@Path("empresaId") empresaId: String): Response<GenericResponse>

    @GET("/api/permisos/empleado/{empleadoId}")
    suspend fun getPermisosPorEmpleado(@Path("empleadoId") empleadoId: String): Response<List<Permiso>>

    @POST("/api/permisos")
    suspend fun crearPermiso(@Body request: PermisoRequest): Response<GenericResponse>

    @PUT("/api/permisos/{id}")
    suspend fun actualizarPermiso(@Path("id") id: String, @Body request: Map<String, String>): Response<GenericResponse>

    @GET("/api/permisos/empresa/{empresaId}")
    suspend fun getPermisosPorEmpresa(@Path("empresaId") empresaId: String): Response<List<Permiso>>

    @DELETE("/api/permisos/{id}")
    suspend fun eliminarPermiso(@Path("id") id: String): Response<Unit>

    @DELETE("/api/permisos/empresa/{empresaId}")
    suspend fun eliminarTodosPermisos(@Path("empresaId") empresaId: String): Response<Unit>



    @PUT("/api/personas/{id}")
    suspend fun actualizarEmpleado(@Path("id") id: String, @Body empleado: Empleado): Response<GenericResponse>

    @GET("/api/empresas/{id}")
    suspend fun getEmpresaById(@Path("id") empresaId: String): Response<Empresa>

    @PUT("/api/empresas/{id}")
    suspend fun actualizarEmpresa(
        @Path("id") empresaId: String,
        @Body empresa: Empresa
    ): Response<GenericResponse>

    @POST("/api/personas")
    suspend fun crearEmpleado(@Body persona: RegistroPersona): Response<GenericResponse>

    @GET("/api/personas/{empleadoId}")
    suspend fun getEmpleadoById(@Path("empleadoId") empleadoId: String): Response<Empleado>

    @DELETE("/api/personas/{id}")
    suspend fun eliminarEmpleado(@Path("id") id: String): Response<ApiResponse>


    @POST("/api/quejas")
    suspend fun enviarQueja(@Body request: QuejaRequest): Response<GenericResponse>

    @POST("/api/empresas")
    suspend fun registrarEmpresa(
        @Body request: RegistrarEmpresaRequest
    ): Response<RegistrarEmpresaResponse>

    @DELETE("/api/empresas/{id}")
    suspend fun eliminarEmpresa(@Path("id") empresaId: String): Response<ApiResponse>

    @GET("/api/certificados/persona/{personaId}")
    suspend fun getCertificadosPorPersona(@Path("personaId") personaId: String): Response<List<Certificado>>

    @Multipart
    @POST("/api/certificados")
    suspend fun registrarCertificado(
        @Part("personaId") personaId: RequestBody,
        @Part("empresaId") empresaId: RequestBody,
        @Part("nombre") nombre: RequestBody,
        @Part archivo: MultipartBody.Part
    ): Response<Certificado>

    @DELETE("/api/certificados/{id}")
    suspend fun eliminarCertificado(@Path("id") id: String): Response<Unit>

}
