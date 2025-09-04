package de.lshorizon.pawplan.ui.screens.settings

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import de.lshorizon.pawplan.R
import de.lshorizon.pawplan.data.AppTheme
import de.lshorizon.pawplan.data.SettingsRepository
import de.lshorizon.pawplan.data.UserProfileRepository
import de.lshorizon.pawplan.navigation.AppDestinations
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(navController: NavController) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val settingsRepo = remember { SettingsRepository(context) }
    val settings by settingsRepo.state.collectAsState(initial = de.lshorizon.pawplan.data.SettingsState())
    val profileRepo = remember { UserProfileRepository() }
    val scope = rememberCoroutineScope()

    var nameInput by remember { mutableStateOf("") }
    LaunchedEffect(Unit) { runCatching { nameInput = profileRepo.getUserName() } }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { SectionTitle(stringResource(R.string.settings_section_account)) }
        item {
            Card(shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(stringResource(R.string.settings_profile_name), style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text(stringResource(R.string.settings_name_label)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = { scope.launch { profileRepo.setUserName(nameInput.trim()) } },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = de.lshorizon.pawplan.ui.theme.RegisterButtonBlue,
                            contentColor = Color.White
                        )
                    ) { Text(stringResource(R.string.save)) }
                }
            }
        }

        item { SectionTitle(stringResource(R.string.settings_section_appearance)) }
        item {
            SettingCard(icon = Icons.Outlined.Palette, title = stringResource(R.string.settings_theme_title)) {
                SegmentedButtonRow {
                    SegmentedButton(
                        selected = settings.appTheme == AppTheme.LIGHT,
                        onClick = { scope.launch { settingsRepo.setTheme(AppTheme.LIGHT) } },
                        label = { Text(stringResource(R.string.settings_theme_light)) }
                    )
                    SegmentedButton(
                        selected = settings.appTheme == AppTheme.DARK,
                        onClick = { scope.launch { settingsRepo.setTheme(AppTheme.DARK) } },
                        label = { Text(stringResource(R.string.settings_theme_dark)) }
                    )
                    SegmentedButton(
                        selected = settings.appTheme == AppTheme.SYSTEM,
                        onClick = { scope.launch { settingsRepo.setTheme(AppTheme.SYSTEM) } },
                        label = { Text(stringResource(R.string.settings_theme_system)) }
                    )
                }
            }
        }
        item {
            SettingSwitch(
                icon = Icons.Outlined.ColorLens,
                title = stringResource(R.string.settings_dynamic_color),
                checked = settings.dynamicColor,
                onCheckedChange = { scope.launch { settingsRepo.setDynamicColor(it) } }
            )
        }

        item { SectionTitle(stringResource(R.string.settings_section_notifications)) }
        item {
            SettingSwitch(
                icon = Icons.Outlined.NotificationsActive,
                title = stringResource(R.string.settings_notifications_enable),
                checked = settings.notificationsEnabled,
                onCheckedChange = { scope.launch { settingsRepo.setNotificationsEnabled(it) } }
            )
        }

        item { SectionTitle(stringResource(R.string.settings_section_privacy)) }
        item {
            SettingSwitch(
                icon = Icons.Outlined.Security,
                title = stringResource(R.string.settings_confirm_before_delete),
                checked = settings.confirmBeforeDelete,
                onCheckedChange = { scope.launch { settingsRepo.setConfirmBeforeDelete(it) } }
            )
        }
        item {
            SettingSwitch(
                icon = Icons.Outlined.Delete,
                title = stringResource(R.string.settings_wifi_only_uploads),
                checked = settings.wifiOnlyUploads,
                onCheckedChange = { scope.launch { settingsRepo.setWifiOnlyUploads(it) } }
            )
        }

        item { SectionTitle(stringResource(R.string.settings_section_language)) }
        item {
            SettingCard(icon = Icons.Outlined.Language, title = stringResource(R.string.settings_language_title)) {
                SegmentedButtonRow {
                    SegmentedButton(
                        selected = settings.language == "de",
                        onClick = { scope.launch { settingsRepo.setLanguage("de") } },
                        label = { Text("DE") }
                    )
                    SegmentedButton(
                        selected = settings.language == "en",
                        onClick = { scope.launch { settingsRepo.setLanguage("en") } },
                        label = { Text("EN") }
                    )
                    SegmentedButton(
                        selected = settings.language == "system",
                        onClick = { scope.launch { settingsRepo.setLanguage("system") } },
                        label = { Text(stringResource(R.string.settings_language_system)) }
                    )
                }
                Text(
                    text = stringResource(R.string.settings_language_note),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        item { LogoutRow(navController) }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleLarge)
}

@Composable
private fun SettingCard(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, content: @Composable () -> Unit) {
    Card(shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(title, style = MaterialTheme.typography.titleMedium)
            }
            content()
        }
    }
}

@Composable
private fun SettingSwitch(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(12.dp))
                Text(title, style = MaterialTheme.typography.titleMedium)
            }
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}

@Composable
private fun LogoutRow(navController: NavController) {
    var showConfirm by remember { mutableStateOf(false) }
    Card(shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.ExitToApp, contentDescription = null, tint = de.lshorizon.pawplan.ui.theme.DangerRed)
                Spacer(Modifier.width(12.dp))
                Text(stringResource(R.string.logout), style = MaterialTheme.typography.titleMedium, color = de.lshorizon.pawplan.ui.theme.DangerRed)
            }
            Button(
                onClick = { showConfirm = true },
                colors = ButtonDefaults.buttonColors(containerColor = de.lshorizon.pawplan.ui.theme.DangerRed, contentColor = Color.White)
            ) { Text(stringResource(R.string.logout)) }
        }
    }

    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            confirmButton = {
                TextButton(onClick = {
                    showConfirm = false
                    Firebase.auth.signOut()
                    navController.navigate(AppDestinations.LOGIN_ROUTE) {
                        popUpTo(0) { inclusive = true }
                    }
                }) { Text(stringResource(R.string.logout), color = de.lshorizon.pawplan.ui.theme.DangerRed) }
            },
            dismissButton = { TextButton(onClick = { showConfirm = false }) { Text(stringResource(R.string.cancel)) } },
            title = { Text(stringResource(R.string.logout_question)) },
            text = { Text(stringResource(R.string.logout_confirm_text)) }
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
