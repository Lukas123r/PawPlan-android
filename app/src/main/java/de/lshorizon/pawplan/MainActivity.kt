package de.lshorizon.pawplan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import de.lshorizon.pawplan.navigation.PawPlanNavHost
import de.lshorizon.pawplan.ui.screens.auth.AuthViewModel
import de.lshorizon.pawplan.ui.theme.PawPlanTheme
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()
        setContent {
            PawPlanTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PawPlanNavHost(
                        authViewModel = authViewModel,
                        onGoogleSignInClick = { beginGoogleSignIn() }
                    )
                }
            }
        }
    }

    private fun beginGoogleSignIn() {
        lifecycleScope.launch {
            authViewModel.setLoading(true)
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(getString(R.string.default_web_client_id))
                .setAutoSelectEnabled(true)
                .build()
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()
            val credentialManager = CredentialManager.create(this@MainActivity)
            try {
                val result = credentialManager.getCredential(request = request, context = this@MainActivity)
                val credential = result.credential
                val idToken = when (credential) {
                    is androidx.credentials.CustomCredential -> {
                        if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                            GoogleIdTokenCredential.createFrom(credential.data).idToken
                        } else null
                    }
                    else -> null
                }
                if (!idToken.isNullOrEmpty()) {
                    authViewModel.signInWithGoogleIdToken(idToken, authViewModel.navController)
                } else {
                    authViewModel.showError("No Google ID token returned")
                }
            } catch (e: GetCredentialException) {
                val msg = when (e) {
                    is androidx.credentials.exceptions.NoCredentialException -> "No account available"
                    is androidx.credentials.exceptions.GetCredentialCancellationException -> "Sign-in canceled"
                    is androidx.credentials.exceptions.GetCredentialProviderConfigurationException -> "Provider not configured"
                    is androidx.credentials.exceptions.GetCredentialInterruptedException -> "Sign-in interrupted"
                    is androidx.credentials.exceptions.GetCredentialUnknownException -> "Unknown sign-in error"
                    is androidx.credentials.exceptions.GetCredentialUnsupportedException -> "Unsupported credential type"
                    else -> e.message ?: "Sign-in failed"
                }
                authViewModel.showError(msg)
            } catch (t: Throwable) {
                authViewModel.showError(t.message ?: "Sign-in failed")
            } finally {
                authViewModel.setLoading(false)
            }
        }
    }
}
