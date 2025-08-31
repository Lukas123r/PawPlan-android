package de.lshorizon.pawplan.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.lshorizon.pawplan.ui.screens.auth.LoginScreen
import de.lshorizon.pawplan.ui.screens.auth.LoginUiState
import de.lshorizon.pawplan.ui.screens.auth.RegisterScreen
import de.lshorizon.pawplan.ui.screens.pets.PetListScreen
import de.lshorizon.pawplan.ui.screens.planner.PlannerScreen
import de.lshorizon.pawplan.ui.screens.settings.SettingsScreen

object AppDestinations {
    const val LOGIN_ROUTE = "login"
    const val REGISTER_ROUTE = "register"
    const val PET_LIST_ROUTE = "pet_list"
    const val PLANNER_ROUTE = "planner"
    const val SETTINGS_ROUTE = "settings"
}

@Composable
fun PawPlanNavHost() {
    val navController = rememberNavController()
    var uiState by remember { mutableStateOf(LoginUiState()) }

    NavHost(navController = navController, startDestination = AppDestinations.LOGIN_ROUTE) {
        composable(AppDestinations.LOGIN_ROUTE) {
            LoginScreen(
                uiState = uiState,
                onEmailChange = { uiState = uiState.copy(email = it) },
                onPasswordChange = { uiState = uiState.copy(password = it) },
                onLoginClick = {
                    // TODO: Implement login logic
                    navController.navigate(AppDestinations.PET_LIST_ROUTE) {
                        popUpTo(AppDestinations.LOGIN_ROUTE) { inclusive = true }
                    }
                },
                onGoogleSignInClick = { /* TODO: Implement Google Sign-In */ },
                onForgotPasswordClick = { /* TODO: Implement forgot password */ },
                onRegisterClick = { navController.navigate(AppDestinations.REGISTER_ROUTE) }
            )
        }
        composable(AppDestinations.REGISTER_ROUTE) {
            var registerUiState by remember { mutableStateOf(de.lshorizon.pawplan.ui.screens.auth.RegisterUiState()) }
            RegisterScreen(
                uiState = registerUiState,
                onEmailChange = { registerUiState = registerUiState.copy(email = it) },
                onPasswordChange = { registerUiState = registerUiState.copy(password = it) },
                onConfirmPasswordChange = { registerUiState = registerUiState.copy(confirmPassword = it) },
                onRegisterClick = {
                    // TODO: Implement register logic
                    navController.navigate(AppDestinations.PET_LIST_ROUTE) {
                        popUpTo(AppDestinations.LOGIN_ROUTE) { inclusive = true }
                    }
                },
                onGoogleSignInClick = { /* TODO: Implement Google Sign-In */ },
                onLoginClick = { navController.popBackStack() }
            )
        }
        composable(AppDestinations.PET_LIST_ROUTE) {
            PetListScreen(navController = navController)
        }
        composable(AppDestinations.PLANNER_ROUTE) {
            PlannerScreen(navController = navController)
        }
        composable(AppDestinations.SETTINGS_ROUTE) {
            SettingsScreen(navController = navController)
        }
    }
}