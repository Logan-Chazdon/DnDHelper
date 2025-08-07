package services

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dataStore
import io.ktor.client.plugins.cookies.*
import io.ktor.http.*
import kotlinx.coroutines.flow.first


/**
 * This only stores our user session for access to the backend.
 * */
class PreferenceCookiesStorage(val context: Context) : CookiesStorage {
    private val key = stringPreferencesKey("USER_SESSION")

    override suspend fun addCookie(requestUrl: Url, cookie: Cookie) {
        context.dataStore.edit { data ->
            data[key] = cookie.value
        }
    }

    override fun close() {

    }

    override suspend fun get(requestUrl: Url): List<Cookie> {
        val cookie = context.dataStore.data.first()[key]
        return listOf(
            Cookie(name = "USER_SESSION", value = cookie ?: "")
        )
    }
}