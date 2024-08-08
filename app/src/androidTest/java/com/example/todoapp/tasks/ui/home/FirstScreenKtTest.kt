package com.example.todoapp.tasks.ui.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.todoapp.R
import com.example.todoapp.tasks.utils.ResourceProvider
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class FirstScreenKtTest{
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    @Inject
    lateinit var resourceProvider: ResourceProvider

    private lateinit var navController: NavHostController

    @Before
    fun setup() {
        hiltRule.inject()

        composeTestRule.setContent {
            navController = rememberNavController()
            NavHost(navController = navController, startDestination = "logoAndButtons") {
                composable("logoAndButtons") { LogoAndButtons(navHostController = navController) }
                composable("loginScreen") { /* Fake screen content */ }
                composable("registerScreen") { /* Fake screen content */ }
            }
        }
    }

    @Test
    fun logoAndButtons_displayedCorrectly() {
        // Verifica que el logo esté presente
        composeTestRule.onNodeWithContentDescription("App logo").assertIsDisplayed()

        // Verifica que los botones estén presentes y tengan el texto correcto
        composeTestRule.onNodeWithText(resourceProvider.getString(R.string.loginButton)).assertIsDisplayed()
        composeTestRule.onNodeWithText(resourceProvider.getString(R.string.RegistButton)).assertIsDisplayed()
    }

    @Test
    fun loginButton_navigatesToLoginScreen() {
        // Simula el clic en el botón de Login
        composeTestRule.onNodeWithText(resourceProvider.getString(R.string.loginButton)).performClick()

        // Verifica que la navegación a la pantalla de login se realizó
        composeTestRule.waitForIdle()  // Espera a que la navegación ocurra
        assert(navController.currentDestination?.route == "loginScreen")
    }

    @Test
    fun registerButton_navigatesToRegisterScreen() {
        // Simula el clic en el botón de Register
        composeTestRule.onNodeWithText(resourceProvider.getString(R.string.RegistButton)).performClick()

        // Verifica que la navegación a la pantalla de registro se realizó
        composeTestRule.waitForIdle()  // Espera a que la navegación ocurra
        assert(navController.currentDestination?.route == "registerScreen")
    }
}