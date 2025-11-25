package com.reflect.app.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.favoriteDataStore by preferencesDataStore("favorite_prefs")

object FavoriteManager {
    private val favKey = stringSetPreferencesKey("favorites")

    suspend fun getFavorites(context: Context): Set<String> {
        val prefs = context.favoriteDataStore.data.first()
        return prefs[favKey] ?: emptySet()
    }

    suspend fun toggle(context: Context, id: String) {
        context.favoriteDataStore.edit { prefs ->
            val set = prefs[favKey]?.toMutableSet() ?: mutableSetOf()
            if (set.contains(id)) set.remove(id) else set.add(id)
            prefs[favKey] = set
        }
    }
}