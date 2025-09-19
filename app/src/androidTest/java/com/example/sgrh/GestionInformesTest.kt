package com.example.sgrh.ui.components

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.sgrh.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InformesScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun cargaInformes_yMuestraLista() {
        composeTestRule.onNodeWithText("Gestión de Informes").assertExists()
    }

    @Test
    fun puedeEntrarADetalle_yVolver() {
        // Crear un informe
        composeTestRule.onNodeWithText("Nombre del informe").performTextInput("Informe Test")
        composeTestRule.onNodeWithText("Descripción").performTextInput("Descripción Test")
        composeTestRule.onNodeWithText("Crear Informe").performClick()

        // Ahora sí debería existir "Consultar"
        composeTestRule.onNodeWithText("Consultar").performClick()
        composeTestRule.onNodeWithText("Detalles del Informe").assertExists()

        // Vuelve al listado
        composeTestRule.onNodeWithText("← Volver").performClick()
        composeTestRule.onNodeWithText("Gestión de Informes").assertExists()
    }
}
