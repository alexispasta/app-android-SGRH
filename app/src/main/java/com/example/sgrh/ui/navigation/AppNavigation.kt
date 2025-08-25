package com.example.sgrh.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.sgrh.ui.components.BaseLayout
import com.example.sgrh.ui.pages.login.LoginScreen
import com.example.sgrh.ui.pages.empleado.EmpleadoHomeScreen
import com.example.sgrh.ui.pages.gerente.GerenteHomeScreen
import com.example.sgrh.ui.pages.supervisor.SupervisorHomeScreen
import com.example.sgrh.ui.pages.rrhh.RrhhHomeScreen
import com.example.sgrh.ui.pages.registro.RegistrarEmpresaScreen
import com.example.sgrh.ui.pages.roles.RolesSelectorScreen

@Composable
fun AppNavigation(navController: NavHostController) {

    // 🔹 Definimos la acción de logout global
    val onLogout: () -> Unit = {
        // Aquí puedes limpiar datos guardados, tokens, etc.
        navController.navigate("login") {
            popUpTo(0) { inclusive = true }
        }
    }

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { rol ->
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

        // Pantallas por rol, ahora envueltas en BaseLayout ✅
        composable("empleadoInicio") {
            BaseLayout(navController = navController, rol = "empleado", onLogout = onLogout) {
                EmpleadoHomeScreen()
            }
        }

        composable("gerenteInicio") {
            BaseLayout(navController = navController, rol = "gerente", onLogout = onLogout) {
                GerenteHomeScreen()
            }
        }

        composable("supervisorInicio") {
            BaseLayout(navController = navController, rol = "supervisor", onLogout = onLogout) {
                SupervisorHomeScreen()
            }
        }

        composable("rrhhInicio") {
            BaseLayout(navController = navController, rol = "rrhh", onLogout = onLogout) {
                RrhhHomeScreen()
            }
        }

        // Otras pantallas
        composable("registrarEmpresa") { RegistrarEmpresaScreen() }
        composable("registrarPersona") { /* TODO */ }
    }
}
