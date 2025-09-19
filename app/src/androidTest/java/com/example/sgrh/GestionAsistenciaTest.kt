package com.example.sgrh.ui.components

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

class GestionAsistenciaTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun pantalla_se_muestra_correctamente() {
        composeTestRule.setContent {
            GestionAsistenciaScreen(onVolver = {}, empresaId = "empresa123")
        }
        composeTestRule.onNodeWithText("Gestión de Asistencia").assertIsDisplayed()
    }

    @Test
    fun boton_historial_cambia_texto() {
        composeTestRule.setContent {
            GestionAsistenciaScreen(onVolver = {}, empresaId = "empresa123")
        }
        composeTestRule.onNodeWithText("Ver historial").performClick()
        composeTestRule.onNodeWithText("Ocultar historial").assertIsDisplayed()
    }

    @Test
    fun muestra_mensaje_si_no_hay_empleados() {
        composeTestRule.setContent {
            GestionAsistenciaScreen(onVolver = {}, empresaId = "empresa123")
        }
        composeTestRule.onNodeWithText("No hay empleados registrados").assertIsDisplayed()
    }

    @Test
    fun boton_volver_funciona() {
        var llamado = false
        composeTestRule.setContent {
            GestionAsistenciaScreen(onVolver = { llamado = true }, empresaId = "empresa123")
        }
        composeTestRule.onNodeWithText("← Volver al Menú").performClick()
        assert(llamado)
    }
}
