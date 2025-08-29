package com.example.sgrh.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class LoginRequest(val email: String, val password: String)
data class Usuario(val _id: String, val rol: String, val empresaId: String?)
data class LoginResponse(val usuario: Usuario?, val message: String?)

interface ApiService {
    @POST("/api/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}
