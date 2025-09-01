@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package de.lshorizon.pawplan.navigation

import androidx.compose.foundation.layout.*
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
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.FilterChip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview
import android.content.res.Configuration
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import de.lshorizon.pawplan.ui.screens.auth.AuthViewModel
import de.lshorizon.pawplan.ui.screens.auth.LoginScreen
import de.lshorizon.pawplan.ui.screens.auth.RegisterScreen
import de.lshorizon.pawplan.ui.screens.pets.PetListScreen
import de.lshorizon.pawplan.ui.screens.pets.PetDetailScreen
import de.lshorizon.pawplan.ui.screens.pets.EditPetScreen
import de.lshorizon.pawplan.ui.screens.pets.PetViewModel
import de.lshorizon.pawplan.ui.screens.pets.PetSearchHeader
import de.lshorizon.pawplan.ui.screens.pets.Species
import de.lshorizon.pawplan.ui.screens.planner.PlannerScreen
import de.lshorizon.pawplan.ui.screens.settings.SettingsScreen
import de.lshorizon.pawplan.ui.theme.PawPlanTheme

object AppDestinations {
    const val HOME_ROUTE = "home"
    const val SPLASH_ROUTE = "splash"
    const val ONBOARDING_ROUTE = "onboarding"
    const val LOGIN_ROUTE = "login"
    const val REGISTER_ROUTE = "register"
    const val PET_LIST_ROUTE = "pet_list"
    const val PET_DETAIL_ROUTE = "pet_detail/{id}"
    const val PET_EDIT_ROUTE = "pet_edit"
    const val PET_EDIT_WITH_ID_ROUTE = "pet_edit/{id}"
    const val PLANNER_ROUTE = "planner"
    const val SETTINGS_ROUTE = "settings"
    const val DOCUMENTS_ROUTE = "documents"
}

@Composable
fun PawPlanNavHost(
    authViewModel: AuthViewModel,
    onGoogleSignInClick: () -> Unit,
    startDestination: String = AppDestinations.SPLASH_ROUTE
) {
    val navController = rememberNavController()
    authViewModel.navController = navController
    val authUiState by authViewModel.uiState.collectAsState()

    NavHost(navController = navController, startDestination = startDestination) {
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
            var petSearchQuery by rememberSaveable { mutableStateOf("") }
            var selectedSpecies by rememberSaveable { mutableStateOf<Species?>(null) }
            MainContainer(
                currentRoute = AppDestinations.PET_LIST_ROUTE,
                navController = navController,
                petSearchQuery = petSearchQuery,
                onPetSearchQueryChange = { q -> petSearchQuery = q },
                selectedSpecies = selectedSpecies,
                onSelectedSpeciesChange = { species -> selectedSpecies = species }
            ) {
                val petViewModel: PetViewModel = viewModel()
                PetListScreen(
                    navController = navController,
                    petViewModel = petViewModel,
                    externalQuery = petSearchQuery,
                    externalSelectedSpecies = selectedSpecies
                )
            }
        }
        composable(AppDestinations.PET_DETAIL_ROUTE) { backStackEntry ->
            MainContainer(currentRoute = AppDestinations.PET_LIST_ROUTE, navController = navController) {
                val petViewModel: PetViewModel = viewModel()
                val id = backStackEntry.arguments?.getString("id")?.toIntOrNull()
                if (id != null) {
                    PetDetailScreen(navController = navController, petId = id, petViewModel = petViewModel)
                }
            }
        }
        composable(AppDestinations.PET_EDIT_ROUTE) {
            MainContainer(currentRoute = AppDestinations.PET_LIST_ROUTE, navController = navController) {
                val petViewModel: PetViewModel = viewModel()
                EditPetScreen(navController = navController, petViewModel = petViewModel, petId = null)
            }
        }
        composable(AppDestinations.PET_EDIT_WITH_ID_ROUTE) { backStackEntry ->
            MainContainer(currentRoute = AppDestinations.PET_LIST_ROUTE, navController = navController) {
                val petViewModel: PetViewModel = viewModel()
                val id = backStackEntry.arguments?.getString("id")?.toIntOrNull()
                EditPetScreen(navController = navController, petViewModel = petViewModel, petId = id)
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
    petSearchQuery: String? = null,
    onPetSearchQueryChange: ((String) -> Unit)? = null,
    selectedSpecies: Species? = null,
    onSelectedSpeciesChange: ((Species?) -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val (name, setName) = remember { mutableStateOf("") }
    LaunchedEffect(context) {
        try {
            de.lshorizon.pawplan.data.OnboardingRepository(context).userName.collect { setName(it) }
        } catch (_: Throwable) {
            setName("Preview")
        }
    }
    val greeting = if (name.isNotBlank()) "Welcome back, $name" else "Welcome back"

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                        actionIconContentColor = MaterialTheme.colorScheme.onBackground
                    ),
                    title = { Text(greeting, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold) },
                    actions = {
                        IconButton(onClick = { navController.navigate(AppDestinations.SETTINGS_ROUTE) }) {
                            Icon(
                                imageVector = Icons.Outlined.AccountCircle,
                                contentDescription = "Account"
                            )
                        }
                    }
                )
                if (currentRoute == AppDestinations.PET_LIST_ROUTE) {
                    PetSearchHeader(
                        query = petSearchQuery ?: "",
                        onQueryChange = { onPetSearchQueryChange?.invoke(it) }
                    )
                    Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                        FilterChip(selected = selectedSpecies == null, onClick = { onSelectedSpeciesChange?.invoke(null) }, label = { Text("All") })
                        Spacer(Modifier.size(8.dp))
                        FilterChip(selected = selectedSpecies == de.lshorizon.pawplan.ui.screens.pets.Species.DOG, onClick = { onSelectedSpeciesChange?.invoke(de.lshorizon.pawplan.ui.screens.pets.Species.DOG) }, label = { Text("Dogs") })
                        Spacer(Modifier.size(8.dp))
                        FilterChip(selected = selectedSpecies == de.lshorizon.pawplan.ui.screens.pets.Species.CAT, onClick = { onSelectedSpeciesChange?.invoke(de.lshorizon.pawplan.ui.screens.pets.Species.CAT) }, label = { Text("Cats") })
                        Spacer(Modifier.size(8.dp))
                        FilterChip(selected = selectedSpecies == de.lshorizon.pawplan.ui.screens.pets.Species.OTHER, onClick = { onSelectedSpeciesChange?.invoke(de.lshorizon.pawplan.ui.screens.pets.Species.OTHER) }, label = { Text("Other") })
                    }
                }
            }
        },
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.only(
            WindowInsetsSides.Start + WindowInsetsSides.End + WindowInsetsSides.Bottom
        ),
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

@Preview(name = "Light Mode", showBackground = true)
@Composable
private fun PawPlanNavHostPreview() {
    PawPlanTheme {
        PawPlanNavHost(
            authViewModel = AuthViewModel(),
            onGoogleSignInClick = {},
            startDestination = AppDestinations.PET_LIST_ROUTE
        )
    }
}

@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun PawPlanNavHostPreviewDark() {
    PawPlanTheme(darkTheme = true) {
        PawPlanNavHost(
            authViewModel = AuthViewModel(),
            onGoogleSignInClick = {},
            startDestination = AppDestinations.PET_LIST_ROUTE
        )
    }
}
