package com.reflect.app.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.reflect.app.notify.ReminderReceiver
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

object ReminderScheduler {
    suspend fun scheduleAll(context: Context) {
        scheduleNext(context)
    }

    suspend fun scheduleNext(context: Context) {
        val minutes = ReminderPrefs.getTimeMinutes(context)
        val mask = ReminderPrefs.getDaysMask(context)
        val next = computeNext(LocalDateTime.now(), minutes, mask)
        val millis = next.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pi = pendingIntent(context)
        am.cancel(pi)
        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, millis, pi)
    }

    private fun pendingIntent(context: Context): PendingIntent {
        val i = Intent(context, ReminderReceiver::class.java)
        return PendingIntent.getBroadcast(context, 1001, i, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    private fun computeNext(now: LocalDateTime, minutes: Int, mask: Int): LocalDateTime {
        val targetTime = LocalTime.of(minutes / 60, minutes % 60)
        var date = now.toLocalDate()
        var candidate = LocalDateTime.of(date, targetTime)
        if (candidate.isBefore(now)) {
            date = date.plusDays(1)
            candidate = LocalDateTime.of(date, targetTime)
        }
        for (i in 0..7) {
            val dow = candidate.dayOfWeek.value
            val idx = (dow + 6) % 7
            if (((mask shr idx) and 1) == 1) return candidate
            date = date.plusDays(1)
            candidate = LocalDateTime.of(date, targetTime)
        }
        return candidate
    }
}