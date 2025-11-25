package com.reflect.app.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import java.time.LocalDate

private val Context.refreshDataStore by preferencesDataStore("refresh_prefs")

object RefreshManager {
    suspend fun getCount(context: Context, date: LocalDate): Int {
        val key = intPreferencesKey("refresh_" + date.toEpochDay())
        val prefs = context.refreshDataStore.data.first()
        return prefs[key] ?: 0
    }

    suspend fun tryIncrement(context: Context, date: LocalDate): Int {
        val key = intPreferencesKey("refresh_" + date.toEpochDay())
        var newValue = 0
        context.refreshDataStore.edit { prefs ->
            val current = prefs[key] ?: 0
            if (current < 3) {
                newValue = current + 1
                prefs[key] = newValue
            } else {
                newValue = current
            }
        }
        return newValue
    }
}