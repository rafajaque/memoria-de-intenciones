package cl.rafit.memoriadeintenciones

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first

class ThemePreferences(private val dataStore: DataStore<Preferences>) {
    private val themeKey = stringPreferencesKey("theme")

    suspend fun saveTheme(theme: AppTheme) {
        dataStore.edit { preferences -> preferences[themeKey] = theme.name }
    }

    suspend fun readTheme(): AppTheme {
        val savedName = dataStore.data.first()[themeKey]
        return AppTheme.entries.firstOrNull { it.name == savedName } ?: AppTheme.MADERA
    }
}
