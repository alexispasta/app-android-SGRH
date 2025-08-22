package com.example.sgrh.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.sgrh.ui.pages.login.LoginScreen
import com.example.sgrh.ui.pages.empleado.EmpleadoHomeScreen
import com.example.sgrh.ui.pages.gerente.GerenteHomeScreen
import com.example.sgrh.ui.pages.supervisor.SupervisorHomeScreen
import com.example.sgrh.ui.pages.Rrhh.RrhhHomeScreen
import com.example.sgrh.ui.pages.registro.RegistrarEmpresaScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "login" // Pantalla inicial
    ) {
        // 🔹 Pantalla de Login
        composable("login") {
            LoginScreen(
                onLoginSuccess = { rol ->
                    val destino = when (rol) {
                        "empleado" -> "empleadoInicio"
                        "gerente" -> "gerenteInicio"
                        "supervisor" -> "supervisorInicio"
                        "rrhh" -> "rrhhInicio"
                        else -> "empleadoInicio"
                    }
                    navController.navigate(destino) {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // 🔹 Pantallas de inicio por rol
        composable("empleadoInicio") { EmpleadoHomeScreen() }
        composable("gerenteInicio") { GerenteHomeScreen() }
        composable("supervisorInicio") { SupervisorHomeScreen() }
        composable("rrhhInicio") { RrhhHomeScreen() }

        // 🔹 Otras pantallas
        composable("registrarEmpresa") { RegistrarEmpresaScreen() }
        composable("registrarPersona") { /* Aquí creas RegistrarPersonaScreen() cuando lo tengas */ }
    }
}
