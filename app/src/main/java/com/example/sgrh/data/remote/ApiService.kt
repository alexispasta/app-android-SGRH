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
}
