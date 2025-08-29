package com.example.sgrh.data.remote

import retrofit2.http.Body
import retrofit2.http.POST

// DTOs
data class LoginRequest(
    val email: String,
    val password: String
)

data class Usuario(
    val _id: String,
    val rol: String,
    val empresaId: String?
)

data class LoginResponse(
    val usuario: Usuario?,
    val message: String?
)



// Retrofit service
interface ApiService {
    @POST("/api/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse
}
