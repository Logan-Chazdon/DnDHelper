package ui.preferences

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.booleanPreferencesKey
import dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform


actual object DataStore {
    @Composable
    actual fun autoSave(): Flow<Boolean> {
        return LocalContext.current.dataStore.data.transform {
            it[booleanPreferencesKey("auto_save")]
        }
    }

    @Composable
    actual fun darkMode(): Flow<Boolean> {
        return LocalContext.current.dataStore.data.transform {
            it[booleanPreferencesKey("dark_mode")]
        }
    }

    @Composable
    actual fun gridNotRow(): Flow<Boolean> {
        return LocalContext.current.dataStore.data.transform {
            it[booleanPreferencesKey("grid_not_row")]
        }
    }
}