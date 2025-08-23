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
import com.example.sgrh.ui.pages.roles.RolesSelectorScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { rol ->
                    // Si el rol es "roles" (modo pruebas), abrimos el selector;
                    // en caso contrario mapeamos al destino correspondiente.
                    if (rol == "roles") {
                        navController.navigate("roles") {
                            popUpTo("login") { inclusive = true }
                            launchSingleTop = true
                        }
                        return@LoginScreen
                    }

                    val destino = when (rol) {
                        "empleado" -> "empleadoInicio"
                        "gerente" -> "gerenteInicio"
                        "supervisor" -> "supervisorInicio"
                        "rrhh" -> "rrhhInicio"
                        else -> "empleadoInicio"
                    }
                    navController.navigate(destino) {
                        popUpTo("login") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // Selector de roles
        composable("roles") {
            RolesSelectorScreen(navController = navController)
        }

        // Pantallas por rol
        composable("empleadoInicio") { EmpleadoHomeScreen() }
        composable("gerenteInicio") { GerenteHomeScreen() }
        composable("supervisorInicio") { SupervisorHomeScreen() }
        composable("rrhhInicio") { RrhhHomeScreen() }

        // Otras pantallas
        composable("registrarEmpresa") { RegistrarEmpresaScreen() }
        composable("registrarPersona") { /* TODO */ }
    }
}
