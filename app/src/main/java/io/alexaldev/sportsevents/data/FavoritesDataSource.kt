package io.alexaldev.sportsevents.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class FavoritesDataSource(
    private val context: Context
) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "favorites")
    private val FAVORITES_KEY = stringSetPreferencesKey("favorite_ids")

    val favoriteFlow: Flow<Set<String>> = context.dataStore.data
        .map { preferences ->
            preferences[FAVORITES_KEY] ?: emptySet()
        }

    suspend fun favoriteToggled(id: String) {
        context.dataStore.edit { preferences ->
            val currentFavorites = preferences[FAVORITES_KEY]?.toMutableSet() ?: mutableSetOf()
            if (id in currentFavorites) {
                currentFavorites.remove(id)
            } else {
                currentFavorites.add(id)
            }
            preferences[FAVORITES_KEY] = currentFavorites
        }
    }

    suspend fun getAll(): Set<String> {
        val preferences = context.dataStore.data.first()
        return preferences[FAVORITES_KEY] ?: emptySet()
    }
}