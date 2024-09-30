package ui.preferences

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.Flow

@Suppress("no_actual_for_expect")
expect object DataStore {
    @Composable
    fun autoSave(): Flow<Boolean>

    @Composable
    fun darkMode(): Flow<Boolean>

    @Composable
    fun gridNotRow(): Flow<Boolean>
}