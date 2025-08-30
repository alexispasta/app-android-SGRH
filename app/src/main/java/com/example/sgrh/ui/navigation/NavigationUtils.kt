package com.example.sgrh.ui.navigation

import androidx.navigation.NavController

fun navigateByRole(navController: NavController, rol: String, userId: String?, empresaId: String?) {
    val uid = userId ?: ""
    val eid = empresaId ?: ""

    when (rol.lowercase()) {
        "gerente" -> navController.navigate("gerenteInicio/$uid/$eid") {
            popUpTo("login") { inclusive = true }
        }
        "rrhh" -> navController.navigate("rrhhInicio/$uid/$eid") {
            popUpTo("login") { inclusive = true }
        }
        "supervisor" -> navController.navigate("supervisorInicio/$uid/$eid") {
            popUpTo("login") { inclusive = true }
        }
        "empleado" -> navController.navigate("empleadoInicio/$uid/$eid") {
            popUpTo("login") { inclusive = true }
        }
        
    }
}
