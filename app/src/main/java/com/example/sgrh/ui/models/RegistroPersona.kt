// RegistroPersona.kt
package com.example.sgrh.ui.models

data class RegistroPersona(
    val nombre: String,
    val apellido: String,
    val email: String,
    val password: String,
    val telefono: String? = null,
    val direccion: String? = null,
    val codigo: String? = "",
    val rol: String,
    val fecha: String? = null,
    val ciudad: String? = null,
    val empresaId: String
)
