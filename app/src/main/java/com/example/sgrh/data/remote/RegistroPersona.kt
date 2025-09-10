// RegistroPersona.kt
package com.example.sgrh.data.remote

data class RegistroPersona(
    val nombre: String,
    val apellido: String,
    val codigo: String, // 🔹 Identificación obligatoria
    val email: String,
    val password: String,
    val telefono: String?,
    val direccion: String?,
    val rol: String,
    val fecha: String?,
    val ciudad: String?,
    val empresaId: String
)

