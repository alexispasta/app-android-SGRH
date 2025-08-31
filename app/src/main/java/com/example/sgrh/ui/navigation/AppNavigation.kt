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
import com.example.sgrh.components.QuejasSugerenciasForm
import com.example.sgrh.ui.components.ConsultarInformacionScreen


@Composable
fun AppNavigation(navController: NavHostController, onLogout: () -> Unit) {
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController = navController)
        }

        composable("gerenteInicio/{usuarioId}/{empresaId}") { backStackEntry ->
            val usuarioId = backStackEntry.arguments?.getString("usuarioId") ?: ""
            val empresaId = backStackEntry.arguments?.getString("empresaId") ?: ""
            BaseLayout(
                navController = navController,
                rol = "gerente",
                usuarioId = usuarioId,
                empresaId = empresaId,
                onLogout = onLogout
            ) {
                GerenteHomeScreen(usuarioId = usuarioId, empresaId = empresaId)
            }
        }

        composable("rrhhInicio/{usuarioId}/{empresaId}") { backStackEntry ->
            val usuarioId = backStackEntry.arguments?.getString("usuarioId") ?: ""
            val empresaId = backStackEntry.arguments?.getString("empresaId") ?: ""
            BaseLayout(
                navController = navController,
                rol = "rrhh",
                usuarioId = usuarioId,
                empresaId = empresaId,
                onLogout = onLogout
            ) {
                RrhhHomeScreen(usuarioId = usuarioId, empresaId = empresaId)
            }
        }

        composable("supervisorInicio/{usuarioId}/{empresaId}") { backStackEntry ->
            val usuarioId = backStackEntry.arguments?.getString("usuarioId") ?: ""
            val empresaId = backStackEntry.arguments?.getString("empresaId") ?: ""
            BaseLayout(
                navController = navController,
                rol = "supervisor",
                usuarioId = usuarioId,
                empresaId = empresaId,
                onLogout = onLogout
            ) {
                SupervisorHomeScreen(usuarioId = usuarioId, empresaId = empresaId)
            }
        }

        composable("empleadoInicio/{usuarioId}/{empresaId}") { backStackEntry ->
            val usuarioId = backStackEntry.arguments?.getString("usuarioId") ?: ""
            val empresaId = backStackEntry.arguments?.getString("empresaId") ?: ""
            BaseLayout(
                navController = navController,
                rol = "empleado",
                usuarioId = usuarioId,
                empresaId = empresaId,
                onLogout = onLogout
            ) {
                EmpleadoHomeScreen(usuarioId = usuarioId, empresaId = empresaId)
            }
        }

        composable("quejas") {
            QuejasSugerenciasForm(onBack = { navController.popBackStack() })
        }

        composable("informacion/{usuarioId}") { backStackEntry ->
            val usuarioId = backStackEntry.arguments?.getString("usuarioId") ?: ""
            ConsultarInformacionScreen(
                onVolver = { navController.popBackStack() },
                usuarioId = usuarioId
            )
        }
    }
}
