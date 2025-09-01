package de.lshorizon.pawplan.ui.screens.auth

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import de.lshorizon.pawplan.navigation.AppDestinations
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()
    lateinit var navController: NavController

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = confirmPassword)
    }

    fun registerUser() {
        viewModelScope.launch {
            if (_uiState.value.password != _uiState.value.confirmPassword) {
                _uiState.value = _uiState.value.copy(error = "Passwords do not match")
                return@launch
            }
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                Firebase.auth.createUserWithEmailAndPassword(_uiState.value.email, _uiState.value.password).await()
                navController.navigate(AppDestinations.HOME_ROUTE) {
                    popUpTo(AppDestinations.LOGIN_ROUTE) { inclusive = true }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun loginUser() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                Firebase.auth.signInWithEmailAndPassword(_uiState.value.email, _uiState.value.password).await()
                navController.navigate(AppDestinations.HOME_ROUTE) {
                    popUpTo(AppDestinations.LOGIN_ROUTE) { inclusive = true }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun resetPassword() {
        viewModelScope.launch {
            val email = _uiState.value.email
            if (email.isBlank()) {
                _uiState.value = _uiState.value.copy(error = "Enter your email to reset password")
                return@launch
            }
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                Firebase.auth.sendPasswordResetEmail(email).await()
                _uiState.value = _uiState.value.copy(error = "Password reset email sent")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun signInWithGoogleIdToken(idToken: String, navController: NavController) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                Firebase.auth.signInWithCredential(credential).await()
                navController.navigate(AppDestinations.HOME_ROUTE) {
                    popUpTo(AppDestinations.LOGIN_ROUTE) { inclusive = true }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun setLoading(loading: Boolean) {
        _uiState.value = _uiState.value.copy(isLoading = loading)
    }

    fun showError(message: String?) {
        _uiState.value = _uiState.value.copy(error = message, isLoading = false)
    }

    fun clearError() {
        if (_uiState.value.error != null) {
            _uiState.value = _uiState.value.copy(error = null)
        }
    }
}

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)
