package de.lshorizon.pawplan.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import android.content.res.Configuration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import de.lshorizon.pawplan.navigation.AppDestinations
import de.lshorizon.pawplan.data.OnboardingRepository
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height

@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val repo = remember { OnboardingRepository(context) }
    val scope = rememberCoroutineScope()
    val userName by repo.userName.collectAsState(initial = "")
    val (nameInput, setNameInput) = remember { mutableStateOf("") }
    LaunchedEffect(userName) { setNameInput(userName) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
            item {
                NameSetting(
                    name = nameInput,
                    onNameChange = setNameInput,
                    onSave = {
                        scope.launch { repo.setUserName(nameInput.trim()) }
                    }
                )
            }
            item {
                SettingsItem(title = "Language", description = "English") {
                    // TODO: Implement language selection
                }
            }
            item {
                SettingsItem(title = "Theme", description = "System Default") {
                    // TODO: Implement theme selection
                }
            }
            item {
                LogoutButton {
                    navController.navigate(AppDestinations.LOGIN_ROUTE) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }
}

@Composable
fun SettingsItem(title: String, description: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = title, style = MaterialTheme.typography.headlineSmall)
                Text(text = description, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun NameSetting(name: String, onNameChange: (String) -> Unit, onSave: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = stringResource(id = de.lshorizon.pawplan.R.string.settings_profile_name), style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text(text = stringResource(id = de.lshorizon.pawplan.R.string.settings_name_label)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = onSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = de.lshorizon.pawplan.ui.theme.RegisterButtonBlue,
                    contentColor = androidx.compose.ui.graphics.Color.White
                )
            ) {
                Text(stringResource(id = de.lshorizon.pawplan.R.string.save))
            }
        }
    }
}

@Composable
fun LogoutButton(onLogout: () -> Unit) {
    var showConfirm by remember { mutableStateOf(false) }
    Button(
        onClick = { showConfirm = true },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = de.lshorizon.pawplan.ui.theme.DangerRed,
            contentColor = androidx.compose.ui.graphics.Color.White
        )
    ) { Text("Logout") }

    if (showConfirm) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showConfirm = false },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = {
                    showConfirm = false
                    onLogout()
                }) { androidx.compose.material3.Text("Logout", color = de.lshorizon.pawplan.ui.theme.DangerRed) }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { showConfirm = false }) {
                    androidx.compose.material3.Text("Cancel")
                }
            },
            title = { androidx.compose.material3.Text("Logout?") },
            text = { androidx.compose.material3.Text("Are you sure you want to log out?") }
        )
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen(navController = rememberNavController())
}

@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun SettingsScreenPreviewDark() {
    SettingsScreen(navController = rememberNavController())
}
