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
import com.example.sgrh.components.QuejasSugerenciasForm
import com.example.sgrh.ui.components.InformacionCuentaForm // ⚠️ asegúrate que exista

@Composable
fun AppNavigation(navController: NavHostController) {

    // 🔹 Acción de logout global
    val onLogout: () -> Unit = {
        navController.navigate("login") {
            popUpTo(0) { inclusive = true }
        }
    }

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        // Login
        composable("login") {
            LoginScreen(navController = navController)
        }

        // Selector de roles
        composable("roles") {
            RolesSelectorScreen(navController = navController)
        }

        // Pantallas por rol envueltas en BaseLayout
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

        // Pantalla de Quejas y Sugerencias
        composable("quejas/{rol}") { backStackEntry ->
            val rol = backStackEntry.arguments?.getString("rol") ?: "empleado"
            BaseLayout(navController = navController, rol = rol, onLogout = onLogout) {
                QuejasSugerenciasForm(
                    onBack = { navController.popBackStack() }
                )
            }
        }

        // Pantalla de información de cuenta
        composable("informacion/{rol}/{usuarioId}") { backStackEntry ->
            val rol = backStackEntry.arguments?.getString("rol") ?: "empleado"
            val usuarioId = backStackEntry.arguments?.getString("usuarioId") ?: ""
            BaseLayout(navController = navController, rol = rol, onLogout = onLogout) {
                InformacionCuentaForm(
                    usuarioId = usuarioId,
                    onBack = { navController.popBackStack() }
                )
            }
        }

        // Otras pantallas
        composable("registrarEmpresa") { RegistrarEmpresaScreen() }
        composable("registrarPersona") { /* TODO */ }
    }
}
