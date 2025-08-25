package com.example.sgrh.ui.models

// Modelo con campos nullable para evitar incompatibilidades al editar/cargar
data class Empleado(
    val id: String,
    val nombre: String? = null,
    val apellido: String? = null,
    val email: String? = null,
    val telefono: String? = null,
    val codigo: String? = null,
    val rol: String? = null,
    val fecha: String? = null,
    val salario: Int? = null,
    val cargo: String? = null,
    val eps: String? = null
)
