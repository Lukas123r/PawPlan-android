package de.lshorizon.pawplan.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "settings_prefs")

enum class AppTheme { SYSTEM, LIGHT, DARK }

data class SettingsState(
    val appTheme: AppTheme = AppTheme.SYSTEM,
    val dynamicColor: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val language: String = "system", // "system" | "de" | "en"
    val confirmBeforeDelete: Boolean = true,
    val wifiOnlyUploads: Boolean = false,
)

class SettingsRepository(private val context: Context) {
    private val KEY_THEME = stringPreferencesKey("app_theme")
    private val KEY_DYNAMIC = booleanPreferencesKey("dynamic_color")
    private val KEY_NOTIFICATIONS = booleanPreferencesKey("notifications_enabled")
    private val KEY_LANGUAGE = stringPreferencesKey("language")
    private val KEY_CONFIRM_DELETE = booleanPreferencesKey("confirm_before_delete")
    private val KEY_WIFI_ONLY = booleanPreferencesKey("wifi_only_uploads")

    val state: Flow<SettingsState> = context.settingsDataStore.data.map { p ->
        SettingsState(
            appTheme = when (p[KEY_THEME]) {
                "light" -> AppTheme.LIGHT
                "dark" -> AppTheme.DARK
                else -> AppTheme.SYSTEM
            },
            dynamicColor = p[KEY_DYNAMIC] ?: false,
            notificationsEnabled = p[KEY_NOTIFICATIONS] ?: true,
            language = p[KEY_LANGUAGE] ?: "system",
            confirmBeforeDelete = p[KEY_CONFIRM_DELETE] ?: true,
            wifiOnlyUploads = p[KEY_WIFI_ONLY] ?: false,
        )
    }

    suspend fun setTheme(theme: AppTheme) {
        context.settingsDataStore.edit { it[KEY_THEME] = when (theme) {
            AppTheme.SYSTEM -> "system"
            AppTheme.LIGHT -> "light"
            AppTheme.DARK -> "dark"
        } }
    }

    suspend fun setDynamicColor(enabled: Boolean) {
        context.settingsDataStore.edit { it[KEY_DYNAMIC] = enabled }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[KEY_NOTIFICATIONS] = enabled }
    }

    suspend fun setLanguage(lang: String) {
        // expected: "system", "de", "en"
        context.settingsDataStore.edit { it[KEY_LANGUAGE] = lang }
    }

    suspend fun setConfirmBeforeDelete(enabled: Boolean) {
        context.settingsDataStore.edit { it[KEY_CONFIRM_DELETE] = enabled }
    }

    suspend fun setWifiOnlyUploads(enabled: Boolean) {
        context.settingsDataStore.edit { it[KEY_WIFI_ONLY] = enabled }
    }
}

