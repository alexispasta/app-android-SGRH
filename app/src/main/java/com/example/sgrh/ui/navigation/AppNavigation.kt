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
import com.example.sgrh.ui.components.InformacionCuentaForm

@Composable
fun AppNavigation(navController: NavHostController) {

    val onLogout: () -> Unit = {
        navController.navigate("login") {
            popUpTo(0) { inclusive = true }
        }
    }

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(navController = navController)
        }

        composable("roles") {
            RolesSelectorScreen(navController = navController)
        }

        composable("empleadoInicio") {
            BaseLayout(navController = navController, rol = "empleado", onLogout = onLogout) {
                EmpleadoHomeScreen()
            }
        }

        composable("gerenteInicio/{usuarioId}/{empresaId}") { backStackEntry ->
            val usuarioId = backStackEntry.arguments?.getString("usuarioId") ?: ""
            val empresaId = backStackEntry.arguments?.getString("empresaId") ?: ""

            BaseLayout(navController = navController, rol = "gerente", onLogout = onLogout) {
                GerenteHomeScreen(usuarioId = usuarioId, empresaId = empresaId)  // ✅ ahora recibe ambos
            }
        }


        composable("supervisorInicio/{usuarioId}/{empresaId}") { backStackEntry ->
            val usuarioId = backStackEntry.arguments?.getString("usuarioId") ?: ""
            val empresaId = backStackEntry.arguments?.getString("empresaId") ?: ""

            BaseLayout(navController = navController, rol = "supervisor", onLogout = onLogout) {
                SupervisorHomeScreen(usuarioId = usuarioId, empresaId = empresaId)
            }
        }


        // ✅ rrhhInicio ahora recibe usuarioId y empresaId reales
        composable("rrhhInicio/{usuarioId}/{empresaId}") { backStackEntry ->
            val usuarioId = backStackEntry.arguments?.getString("usuarioId") ?: ""
            val empresaId = backStackEntry.arguments?.getString("empresaId") ?: ""

            BaseLayout(navController = navController, rol = "rrhh", onLogout = onLogout) {
                RrhhHomeScreen(usuarioId = usuarioId, empresaId = empresaId)
            }
        }

        composable("quejas/{rol}") { backStackEntry ->
            val rol = backStackEntry.arguments?.getString("rol") ?: "empleado"
            BaseLayout(navController = navController, rol = rol, onLogout = onLogout) {
                QuejasSugerenciasForm(onBack = { navController.popBackStack() })
            }
        }

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

        composable("registrarEmpresa") { RegistrarEmpresaScreen() }
        composable("registrarPersona") { /* TODO */ }
    }
}
