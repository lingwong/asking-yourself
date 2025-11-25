package com.reflect.app.notify

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.reflect.app.data.ReminderScheduler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        GlobalScope.launch { ReminderScheduler.scheduleAll(context) }
    }
}

// no-op