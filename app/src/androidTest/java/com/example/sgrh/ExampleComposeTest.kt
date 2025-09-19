package com.example.sgrh

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.assertIsDisplayed
import org.junit.Rule
import org.junit.Test

class ExampleComposeTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun pruebaTextoVisible() {
        composeTestRule.onNodeWithTag("textoHola").assertIsDisplayed()
    }
}
