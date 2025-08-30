package com.example.sgrh.ui.models

data class Empleado(
    val _id: String,
    val nombre: String? = null,
    val apellido: String? = null,
    val email: String? = null,
    val telefono: String? = null,
    val direccion: String? = null,
    val codigo: String? = null,
    val rol: String? = null,
    val fecha: String? = null,
    val ciudad: String? = null
)
