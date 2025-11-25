package com.reflect.app.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.reflect.app.data.ReminderPrefs
import com.reflect.app.data.ReminderScheduler
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

@Composable
fun ReminderSettingsScreen() {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var minutes by remember { mutableStateOf(20 * 60) }
    var mask by remember { mutableStateOf(0b0111111) }
    var timeText by remember { mutableStateOf("20:00") }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }

    LaunchedEffect(Unit) {
        minutes = ReminderPrefs.getTimeMinutes(ctx)
        mask = ReminderPrefs.getDaysMask(ctx)
        timeText = String.format("%02d:%02d", minutes / 60, minutes % 60)
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(value = timeText, onValueChange = {
                timeText = it
                val parts = it.split(":")
                if (parts.size == 2) {
                    val h = parts[0].toIntOrNull()
                    val m = parts[1].toIntOrNull()
                    if (h != null && m != null && h in 0..23 && m in 0..59) {
                        minutes = h * 60 + m
                    }
                }
            }, label = { Text("提醒时间(HH:MM)") }, modifier = Modifier.weight(1f))
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ElevatedButton(onClick = { mask = 0b1111111 }) { Text("每日") }
            ElevatedButton(onClick = { mask = 0b0111111 }) { Text("工作日") }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DayToggle("一", mask, 0) { mask = it }
            DayToggle("二", mask, 1) { mask = it }
            DayToggle("三", mask, 2) { mask = it }
            DayToggle("四", mask, 3) { mask = it }
            DayToggle("五", mask, 4) { mask = it }
            DayToggle("六", mask, 5) { mask = it }
            DayToggle("日", mask, 6) { mask = it }
        }
        Button(onClick = {
            scope.launch {
                ReminderPrefs.setTimeMinutes(ctx, minutes)
                ReminderPrefs.setDaysMask(ctx, mask)
                if (Build.VERSION.SDK_INT >= 33) {
                    launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                ReminderScheduler.scheduleAll(ctx)
            }
        }, modifier = Modifier.padding(top = 12.dp)) { Text("保存并启用提醒") }
    }
}

@Composable
private fun DayToggle(label: String, mask: Int, index: Int, onChange: (Int) -> Unit) {
    val on = ((mask shr index) and 1) == 1
    ElevatedButton(onClick = {
        val newMask = if (on) mask and (1 shl index).inv() else mask or (1 shl index)
        onChange(newMask)
    }) { Text(label) }
}