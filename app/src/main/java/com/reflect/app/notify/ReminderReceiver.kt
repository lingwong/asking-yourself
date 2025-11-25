package com.reflect.app.notify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.reflect.app.R
import com.reflect.app.data.ReminderScheduler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        ensureChannel(context)
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("每日复盘")
            .setContentText("到提醒时间了，来完成今天的复盘吧")
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        NotificationManagerCompat.from(context).notify(NOTIF_ID, builder.build())
        GlobalScope.launch { ReminderScheduler.scheduleNext(context) }
    }

    private fun ensureChannel(context: Context) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(CHANNEL_ID, "复盘提醒", NotificationManager.IMPORTANCE_HIGH)
        channel.enableLights(true)
        channel.lightColor = Color.GREEN
        nm.createNotificationChannel(channel)
    }

    companion object {
        private const val CHANNEL_ID = "review_remind"
        private const val NOTIF_ID = 1001
    }
}