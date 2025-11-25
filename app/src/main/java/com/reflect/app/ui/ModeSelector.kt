package com.reflect.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.FilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.unit.dp
import com.reflect.app.model.ReviewMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModeSelector(mode: ReviewMode, onModeChange: (ReviewMode) -> Unit, onNavigateReminder: () -> Unit) {
    val highlightMode = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<ReviewMode?>(null) }
    val scope = rememberCoroutineScope()
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)) {
        val colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant,
            selectedContainerColor = androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer
        )
        val normalColors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant,
            selectedContainerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant
        )
        val currentColors = { sel: Boolean -> if (sel && highlightMode.value == mode) colors else normalColors }
        FilterChip(selected = mode == ReviewMode.ExperienceSummary, onClick = {
            onModeChange(ReviewMode.ExperienceSummary)
            highlightMode.value = ReviewMode.ExperienceSummary
            scope.launch {
                kotlinx.coroutines.delay(1000)
                highlightMode.value = null
            }
        }, label = { Text("快速模式") }, colors = currentColors(mode == ReviewMode.ExperienceSummary))
        FilterChip(selected = mode == ReviewMode.FreeDiary, onClick = {
            onModeChange(ReviewMode.FreeDiary)
            highlightMode.value = ReviewMode.FreeDiary
            scope.launch {
                kotlinx.coroutines.delay(1000)
                highlightMode.value = null
            }
        }, label = { Text("日记模式") }, colors = currentColors(mode == ReviewMode.FreeDiary))
        FilterChip(selected = mode == ReviewMode.Questions, onClick = {
            onModeChange(ReviewMode.Questions)
            highlightMode.value = ReviewMode.Questions
            scope.launch {
                kotlinx.coroutines.delay(1000)
                highlightMode.value = null
            }
        }, label = { Text("问题模式") }, colors = currentColors(mode == ReviewMode.Questions))
    }
    Spacer(modifier = Modifier.height(4.dp))
}