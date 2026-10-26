package de.briefampel.app.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import de.briefampel.app.domain.Sender
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.store by preferencesDataStore(name = "briefampel")

/** Einwilligung und Absender – beides bleibt nur auf diesem Gerät. */
class LocalStore(private val context: Context) {
    private object Keys {
        val consent = booleanPreferencesKey("consent_v1")
        val name = stringPreferencesKey("sender_name")
        val street = stringPreferencesKey("sender_street")
        val city = stringPreferencesKey("sender_city")
        val email = stringPreferencesKey("sender_email")
    }

    val consent: Flow<Boolean> = context.store.data.map { it[Keys.consent] ?: false }

    val sender: Flow<Sender> = context.store.data.map {
        Sender(it[Keys.name].orEmpty(), it[Keys.street].orEmpty(), it[Keys.city].orEmpty(), it[Keys.email].orEmpty())
    }

    suspend fun giveConsent() {
        context.store.edit { it[Keys.consent] = true }
    }

    suspend fun saveSender(sender: Sender) {
        context.store.edit {
            it[Keys.name] = sender.name
            it[Keys.street] = sender.street
            it[Keys.city] = sender.city
            it[Keys.email] = sender.email
        }
    }
}
