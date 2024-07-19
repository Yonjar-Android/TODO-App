package com.example.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.todoapp.tasks.ui.BackgroundScreen
import com.example.todoapp.tasks.ui.home.LogoAndButtons
import com.example.todoapp.tasks.ui.auth.login.LoginScreen
import com.example.todoapp.tasks.ui.auth.login.LoginViewModel
import com.example.todoapp.tasks.ui.auth.register.RegisterScreen
import com.example.todoapp.tasks.ui.auth.register.RegisterViewModel
import com.example.todoapp.tasks.ui.auth.restPassword.ResetPasswordScreen
import com.example.todoapp.tasks.ui.auth.restPassword.ResetPasswordViewModel
import com.example.todoapp.tasks.ui.taskScreen.MainTaskScreen
import com.example.todoapp.ui.theme.TodoAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val registerViewModel: RegisterViewModel by viewModels()

    private val loginViewModel: LoginViewModel by viewModels()

    private val resetPasswordViewModel: ResetPasswordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TodoAppTheme {

                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "logoAndButtons") {

                    composable("logoAndButtons") { BackgroundScreen(image = R.drawable.app_bg) { LogoAndButtons(navController) } }

                    composable("registerScreen") {
                        BackgroundScreen(image = R.drawable.app_bg) {
                            RegisterScreen(
                                navController,
                                registerViewModel
                            )
                        }
                    }

                    composable("loginScreen") {
                        BackgroundScreen(image = R.drawable.app_bg) {
                            LoginScreen(
                                loginViewModel,
                                navController
                            )
                        }
                    }

                    composable("resetPasswordScreen"){
                        BackgroundScreen(image = R.drawable.app_bg) {
                            ResetPasswordScreen(resetPasswordViewModel, navController)
                        }
                    }

                    composable(route = "mainTaskScreen/{email}", arguments = listOf(
                        navArgument("email") {
                            type = NavType.StringType
                        })
                    ) { backStackEntry ->
                        val email = backStackEntry.arguments?.getString("email")

                       BackgroundScreen(image = R.drawable.main_bg) {
                           MainTaskScreen(navHostController = navController, email = email ?: "")
                       }
                    }
                }
            }
        }
    }
}

