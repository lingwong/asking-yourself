package com.reflect.app.ui

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

enum class AppScreen { Review, Flash, Stats, Reminder, My, MyQuestions, History }

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(current: AppScreen) {
    CenterAlignedTopAppBar(
        title = {
            val showDate = current != AppScreen.History && current != AppScreen.My
            val label = if (showDate) LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")) else ""
            androidx.compose.material3.Text(text = label, color = MaterialTheme.colorScheme.onSurface)
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Composable
fun BottomNav(current: AppScreen, onNavigate: (AppScreen) -> Unit) {
    NavigationBar {
        NavigationBarItem(selected = current == AppScreen.Review, onClick = { onNavigate(AppScreen.Review) }, icon = { Icon(Icons.Filled.Today, null) }, label = { androidx.compose.material3.Text("今日") })
        NavigationBarItem(selected = current == AppScreen.Flash, onClick = { onNavigate(AppScreen.Flash) }, icon = { androidx.compose.material3.Text("🤔") }, label = { androidx.compose.material3.Text("为什么呢？") })
        NavigationBarItem(selected = current == AppScreen.History, onClick = { onNavigate(AppScreen.History) }, icon = { Icon(Icons.Filled.History, null) }, label = { androidx.compose.material3.Text("历史") })
        NavigationBarItem(selected = current == AppScreen.My, onClick = { onNavigate(AppScreen.My) }, icon = { Icon(Icons.Filled.Settings, null) }, label = { androidx.compose.material3.Text("我的") })
    }
}
