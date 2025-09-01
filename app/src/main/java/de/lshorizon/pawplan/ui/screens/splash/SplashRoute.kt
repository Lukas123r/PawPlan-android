package de.lshorizon.pawplan.ui.screens.splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import de.lshorizon.pawplan.data.OnboardingRepository
import de.lshorizon.pawplan.navigation.AppDestinations
import kotlinx.coroutines.flow.first

@Composable
fun SplashRoute(onNavigate: (String) -> Unit) {
    val context = LocalContext.current
    LaunchedEffect(true) {
        val repo = OnboardingRepository(context)
        val done = repo.isOnboardingDone.first()
        val user = Firebase.auth.currentUser
        val target = when {
            !done -> AppDestinations.ONBOARDING_ROUTE
            user != null -> AppDestinations.HOME_ROUTE
            else -> AppDestinations.LOGIN_ROUTE
        }
        onNavigate(target)
    }
}

