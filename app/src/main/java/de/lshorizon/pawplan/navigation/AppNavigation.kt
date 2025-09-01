@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package de.lshorizon.pawplan.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.lshorizon.pawplan.ui.screens.auth.AuthViewModel
import de.lshorizon.pawplan.ui.screens.auth.LoginScreen
import de.lshorizon.pawplan.ui.screens.auth.RegisterScreen
import de.lshorizon.pawplan.ui.screens.pets.PetListScreen
import de.lshorizon.pawplan.ui.screens.planner.PlannerScreen
import de.lshorizon.pawplan.ui.screens.settings.SettingsScreen

object AppDestinations {
    const val HOME_ROUTE = "home"
    const val SPLASH_ROUTE = "splash"
    const val ONBOARDING_ROUTE = "onboarding"
    const val LOGIN_ROUTE = "login"
    const val REGISTER_ROUTE = "register"
    const val PET_LIST_ROUTE = "pet_list"
    const val PLANNER_ROUTE = "planner"
    const val SETTINGS_ROUTE = "settings"
    const val DOCUMENTS_ROUTE = "documents"
}

@Composable
fun PawPlanNavHost(
    authViewModel: AuthViewModel,
    onGoogleSignInClick: () -> Unit
) {
    val navController = rememberNavController()
    authViewModel.navController = navController
    val authUiState by authViewModel.uiState.collectAsState()

    NavHost(navController = navController, startDestination = AppDestinations.SPLASH_ROUTE) {
        composable(AppDestinations.SPLASH_ROUTE) {
            de.lshorizon.pawplan.ui.screens.splash.SplashRoute(
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(AppDestinations.SPLASH_ROUTE) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(AppDestinations.ONBOARDING_ROUTE) {
            de.lshorizon.pawplan.ui.screens.onboarding.OnboardingScreen(
                onFinish = {
                    navController.navigate(AppDestinations.LOGIN_ROUTE) {
                        popUpTo(AppDestinations.ONBOARDING_ROUTE) { inclusive = true }
                    }
                }
            )
        }
        composable(AppDestinations.LOGIN_ROUTE) {
            LoginScreen(
                uiState = authUiState,
                onEmailChange = { authViewModel.onEmailChange(it) },
                onPasswordChange = { authViewModel.onPasswordChange(it) },
                onLoginClick = { authViewModel.loginUser() },
                onGoogleSignInClick = onGoogleSignInClick,
                onForgotPasswordClick = { authViewModel.resetPassword() },
                onRegisterClick = { navController.navigate(AppDestinations.REGISTER_ROUTE) }
            )
        }
        composable(AppDestinations.REGISTER_ROUTE) {
            RegisterScreen(
                uiState = authUiState,
                onEmailChange = { authViewModel.onEmailChange(it) },
                onPasswordChange = { authViewModel.onPasswordChange(it) },
                onConfirmPasswordChange = { authViewModel.onConfirmPasswordChange(it) },
                onRegisterClick = { authViewModel.registerUser() },
                onGoogleSignInClick = onGoogleSignInClick,
                onLoginClick = { navController.popBackStack() }
            )
        }
        composable(AppDestinations.HOME_ROUTE) {
            MainContainer(currentRoute = AppDestinations.HOME_ROUTE, navController = navController) {
                de.lshorizon.pawplan.ui.screens.home.HomeScreen(
                    onAddPet = { navController.navigate(AppDestinations.PET_LIST_ROUTE) },
                    onAddReminder = { navController.navigate(AppDestinations.PLANNER_ROUTE) },
                    onUploadDocument = { navController.navigate(AppDestinations.DOCUMENTS_ROUTE) },
                    onOpenPet = { navController.navigate(AppDestinations.PET_LIST_ROUTE) },
                    onOpenDocuments = { navController.navigate(AppDestinations.DOCUMENTS_ROUTE) }
                )
            }
        }
        composable(AppDestinations.PET_LIST_ROUTE) {
            MainContainer(currentRoute = AppDestinations.PET_LIST_ROUTE, navController = navController) {
                PetListScreen(navController = navController)
            }
        }
        composable(AppDestinations.PLANNER_ROUTE) {
            MainContainer(currentRoute = AppDestinations.PLANNER_ROUTE, navController = navController) {
                PlannerScreen(navController = navController)
            }
        }
        composable(AppDestinations.DOCUMENTS_ROUTE) {
            MainContainer(currentRoute = AppDestinations.DOCUMENTS_ROUTE, navController = navController) {
                de.lshorizon.pawplan.ui.screens.documents.DocumentsScreen(navController = navController)
            }
        }
        composable(AppDestinations.SETTINGS_ROUTE) {
            MainContainer(currentRoute = AppDestinations.SETTINGS_ROUTE, navController = navController) {
                SettingsScreen(navController = navController)
            }
        }
    }
}

@Composable
private fun MainContainer(
    currentRoute: String,
    navController: NavController,
    content: @Composable () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground
                ),
                title = { Text("PawPlan") },
                actions = {
                    IconButton(onClick = { navController.navigate(AppDestinations.SETTINGS_ROUTE) }) {
                        Icon(
                            imageVector = Icons.Outlined.AccountCircle,
                            contentDescription = "Account"
                        )
                    }
                }
            )
        },
        // No persistent FAB here; per-screen FABs only
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                bottomItems.forEach { item ->
                    val selectedColor = when (item.route) {
                        AppDestinations.HOME_ROUTE -> de.lshorizon.pawplan.ui.theme.PrimaryBlue
                        AppDestinations.PET_LIST_ROUTE -> de.lshorizon.pawplan.ui.theme.AccentOrange
                        AppDestinations.PLANNER_ROUTE -> de.lshorizon.pawplan.ui.theme.SecondaryGreen
                        AppDestinations.DOCUMENTS_ROUTE -> de.lshorizon.pawplan.ui.theme.PrimaryBlue.copy(alpha = 0.92f)
                        AppDestinations.SETTINGS_ROUTE -> MaterialTheme.colorScheme.onBackground
                        else -> MaterialTheme.colorScheme.primary
                    }
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            if (currentRoute != item.route) {
                                navController.navigate(item.route) {
                                    popUpTo(AppDestinations.HOME_ROUTE) { inclusive = false }
                                    launchSingleTop = true
                                }
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = selectedColor,
                            selectedTextColor = selectedColor,
                            indicatorColor = Color.Transparent,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            content()
        }
    }
}

private data class BottomItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

private val bottomItems = listOf(
    BottomItem(AppDestinations.HOME_ROUTE, Icons.Outlined.Home, "Home"),
    BottomItem(AppDestinations.PET_LIST_ROUTE, Icons.Outlined.Pets, "Pets"),
    BottomItem(AppDestinations.PLANNER_ROUTE, Icons.Outlined.Event, "Planner"),
    BottomItem(AppDestinations.DOCUMENTS_ROUTE, Icons.Outlined.Description, "Documents"),
    BottomItem(AppDestinations.SETTINGS_ROUTE, Icons.Outlined.Settings, "Settings")
)
