package de.lshorizon.pawplan.ui.screens.splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import android.content.res.Configuration
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import de.lshorizon.pawplan.data.OnboardingRepository
import de.lshorizon.pawplan.navigation.AppDestinations
import de.lshorizon.pawplan.data.UserProfileRepository
import kotlinx.coroutines.flow.first

@Composable
fun SplashRoute(onNavigate: (String) -> Unit) {
    val context = LocalContext.current
    LaunchedEffect(true) {
        val onboardingRepo = OnboardingRepository(context)
        val done = onboardingRepo.isOnboardingDone.first()
        val user = Firebase.auth.currentUser
        val target = when {
            !done -> AppDestinations.ONBOARDING_ROUTE
            user == null -> AppDestinations.LOGIN_ROUTE
            else -> {
                // User signed in: ensure Firestore name exists (no fallback)
                val profileRepo = UserProfileRepository()
                val name = try { profileRepo.getUserName() } catch (e: Exception) { null }
                if (name.isNullOrBlank()) AppDestinations.PROFILE_SETUP_ROUTE else AppDestinations.HOME_ROUTE
            }
        }
        onNavigate(target)
    }
}

// Lightweight previews to avoid running side-effects in SplashRoute
@Preview(name = "Light Mode", showBackground = true)
@Composable
private fun SplashPreview() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Splash") }
}

@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun SplashPreviewDark() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Splash") }
}
