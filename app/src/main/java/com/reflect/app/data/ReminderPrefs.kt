package com.reflect.app.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.reminderPrefs by preferencesDataStore("reminder_prefs")

object ReminderPrefs {
    private val timeKey = intPreferencesKey("time_minutes")
    private val daysMaskKey = intPreferencesKey("days_mask")

    suspend fun getTimeMinutes(context: Context): Int {
        val prefs = context.reminderPrefs.data.first()
        return prefs[timeKey] ?: (20 * 60)
    }

    suspend fun setTimeMinutes(context: Context, minutes: Int) {
        context.reminderPrefs.edit { it[timeKey] = minutes }
    }

    suspend fun getDaysMask(context: Context): Int {
        val prefs = context.reminderPrefs.data.first()
        return prefs[daysMaskKey] ?: 0b0111111
    }

    suspend fun setDaysMask(context: Context, mask: Int) {
        context.reminderPrefs.edit { it[daysMaskKey] = mask }
    }
}