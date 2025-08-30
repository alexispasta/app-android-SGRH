package com.example.sgrh.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.sgrh.ui.pages.login.LoginScreen
import com.example.sgrh.ui.pages.gerente.GerenteHomeScreen
import com.example.sgrh.ui.pages.rrhh.RrhhHomeScreen
import com.example.sgrh.ui.pages.supervisor.SupervisorHomeScreen
import com.example.sgrh.ui.pages.empleado.EmpleadoHomeScreen
import com.example.sgrh.ui.components.BaseLayout

@Composable
fun AppNavigation(navController: NavHostController, onLogout: () -> Unit) {
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController = navController)
        }

        composable("gerenteInicio/{usuarioId}/{empresaId}") { backStackEntry ->
            val usuarioId = backStackEntry.arguments?.getString("usuarioId") ?: ""
            val empresaId = backStackEntry.arguments?.getString("empresaId") ?: ""
            BaseLayout(navController = navController, rol = "gerente", onLogout = onLogout) {
                GerenteHomeScreen(usuarioId = usuarioId, empresaId = empresaId)
            }
        }

        composable("rrhhInicio/{usuarioId}/{empresaId}") { backStackEntry ->
            val usuarioId = backStackEntry.arguments?.getString("usuarioId") ?: ""
            val empresaId = backStackEntry.arguments?.getString("empresaId") ?: ""
            BaseLayout(navController = navController, rol = "rrhh", onLogout = onLogout) {
                RrhhHomeScreen(usuarioId = usuarioId, empresaId = empresaId)
            }
        }

        composable("supervisorInicio/{usuarioId}/{empresaId}") { backStackEntry ->
            val usuarioId = backStackEntry.arguments?.getString("usuarioId") ?: ""
            val empresaId = backStackEntry.arguments?.getString("empresaId") ?: ""
            BaseLayout(navController = navController, rol = "supervisor", onLogout = onLogout) {
                SupervisorHomeScreen(usuarioId = usuarioId, empresaId = empresaId)
            }
        }

        // ✅ Nueva ruta para empleado
        composable("empleadoInicio/{usuarioId}/{empresaId}") { backStackEntry ->
            val usuarioId = backStackEntry.arguments?.getString("usuarioId") ?: ""
            val empresaId = backStackEntry.arguments?.getString("empresaId") ?: ""
            BaseLayout(navController = navController, rol = "empleado", onLogout = onLogout) {
                EmpleadoHomeScreen(usuarioId = usuarioId, empresaId = empresaId)
            }
        }
    }
}
