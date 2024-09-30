package ui.preferences

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

actual object DataStore {
    @Composable
    actual fun autoSave(): Flow<Boolean> {
        return flow {
            emit(true) //TODO
        }
    }

    @Composable
    actual fun darkMode(): Flow<Boolean> {
        return flow {
            emit(false) //TODO
        }
    }

    @Composable
    actual fun gridNotRow(): Flow<Boolean> {
        return flow {
            emit(false) //TODO
        }
    }
}