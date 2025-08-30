package com.example.sgrh.data.remote

import retrofit2.Response
import retrofit2.http.*

data class LoginRequest(val email: String, val password: String)

data class Usuario(
    val _id: String,
    val rol: String,
    val empresaId: String?
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
    val empleadoId: EmpleadoReporte,
    val createdAt: String
)

data class ReporteRequest(
    val asunto: String,
    val descripcion: String,
    val empleadoId: String,
    val empresaId: String
)

data class Informe(
    val _id: String,
    val nombre: String,
    val descripcion: String?,
    val createdAt: String
)

data class InformeRequest(
    val nombre: String,
    val descripcion: String?,
    val empresaId: String
)

data class Permiso(
    val _id: String,
    val empleadoId: String,
    val empleadoNombre: String,
    val motivo: String,
    val descripcion: String?,
    val estado: String,
    val createdAt: String
)

data class PermisoRequest(
    val empleadoId: String,
    val empleadoNombre: String,
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

interface ApiService {
    @POST("/api/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("/api/personas/empresa/{empresaId}")
    suspend fun getEmpleados(@Path("empresaId") empresaId: String): Response<List<EmpleadoAsistencia>>

    @GET("/api/asistencia/historial/{empresaId}")
    suspend fun getHistorial(@Path("empresaId") empresaId: String): Response<List<String>>

    @GET("/api/asistencia/{empresaId}/{fecha}")
    suspend fun getAsistenciaPorFecha(
        @Path("empresaId") empresaId: String,
        @Path("fecha") fecha: String
    ): Response<List<AsistenciaRequest>>

    @POST("/api/asistencia")
    suspend fun guardarAsistencia(@Body registros: List<AsistenciaRequest>): Response<GenericResponse>

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

    @GET("/api/informes/empresa/{empresaId}")
    suspend fun getInformesPorEmpresa(@Path("empresaId") empresaId: String): Response<List<Informe>>

    @POST("/api/informes")
    suspend fun crearInforme(@Body request: InformeRequest): Response<Informe>

    @GET("/api/permisos/empleado/{empleadoId}")
    suspend fun getPermisosPorEmpleado(@Path("empleadoId") empleadoId: String): Response<List<Permiso>>

    @POST("/api/permisos")
    suspend fun crearPermiso(@Body request: PermisoRequest): Response<GenericResponse>

    @PUT("/api/permisos/{id}")
    suspend fun actualizarPermiso(@Path("id") id: String, @Body request: Map<String, String>): Response<GenericResponse>

    @GET("/api/permisos/empresa/{empresaId}")
    suspend fun getPermisosPorEmpresa(@Path("empresaId") empresaId: String): Response<List<Permiso>>

    @PUT("/api/personas/{id}")
    suspend fun actualizarEmpleado(@Path("id") id: String, @Body empleado: Empleado): Response<GenericResponse>

    @GET("/api/empresas/{id}")
    suspend fun getEmpresaById(@Path("id") empresaId: String): Response<Empresa>

    @PUT("/api/empresas/{id}")
    suspend fun actualizarEmpresa(
        @Path("id") empresaId: String,
        @Body empresa: Empresa
    ): Response<GenericResponse>

}
