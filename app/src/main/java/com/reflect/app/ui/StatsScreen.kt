package com.reflect.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ElevatedCard
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.Icon
import androidx.compose.material3.AssistChip
import com.reflect.app.data.AchievementsCalculator
import com.reflect.app.data.StatsCalculator
import com.reflect.app.model.ReviewRecord
import com.reflect.app.ui.charts.LineChart
import com.reflect.app.ui.charts.BarChart
import com.reflect.app.ui.charts.PieChart
import com.reflect.app.ui.charts.ChartPalette

@Composable
fun StatsScreen(history: List<ReviewRecord>) {
    val stats = StatsCalculator.compute(history)
    val achievements = AchievementsCalculator.unlocked(history)
    val last30 = buildLast30(history)
    LazyColumn(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("复盘总天数", stats.totalDays.toString(), modifier = Modifier.weight(1f))
                StatCard("连续打卡天数", stats.consecutiveDays.toString(), modifier = Modifier.weight(1f))
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("本月完成率", "${stats.monthlyRate}%", modifier = Modifier.weight(1f))
                StatCard("本年完成率", "${stats.yearlyRate}%", modifier = Modifier.weight(1f))
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("问题解决率", "${stats.solvedRate}%", modifier = Modifier.weight(1f))
            }
        }
        item {
            ElevatedCard(shape = RoundedCornerShape(12.dp), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(imageVector = Icons.Filled.Today, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text("近30天每日复盘次数", style = MaterialTheme.typography.titleMedium)
                    }
                    Spacer(Modifier.height(8.dp))
                    LineChart(labels = last30.first, values = last30.second, lineColor = ChartPalette[0], title = "")
                }
            }
        }
        item {
            ElevatedCard(shape = RoundedCornerShape(12.dp), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(imageVector = Icons.Filled.Insights, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text("模式使用频率", style = MaterialTheme.typography.titleMedium)
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        stats.modeUsage.entries.forEach { e ->
                            val label = when (e.key.name) {
                                "ExperienceSummary" -> "快速模式"
                                "FreeDiary" -> "日记模式"
                                "Questions" -> "问题模式"
                                else -> e.key.name
                            }
                            AssistChip(onClick = {}, label = { Text("$label: ${e.value}") })
                        }
                    }
                }
            }
        }
        item {
            ElevatedCard(shape = RoundedCornerShape(12.dp), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(imageVector = Icons.Filled.PieChart, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text("模式占比", style = MaterialTheme.typography.titleMedium)
                    }
                    Spacer(Modifier.height(8.dp))
                    PieChart(data = stats.modeUsage.entries.map { it.key.name to it.value }, colors = ChartPalette, title = "")
                }
            }
        }
        item { Text("问题分类回答频次") }
        items(stats.categoryFrequency.entries.toList()) { e ->
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Row(modifier = Modifier.padding(12.dp)) {
                    Text("${e.key}")
                    Spacer(Modifier.weight(1f))
                    Text("${e.value}")
                }
            }
        }
        item {
            ElevatedCard(shape = RoundedCornerShape(12.dp), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(imageVector = Icons.Filled.BarChart, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text("分类回答柱状图", style = MaterialTheme.typography.titleMedium)
                    }
                    Spacer(Modifier.height(8.dp))
                    BarChart(data = stats.categoryFrequency.entries.map { it.key to it.value }, color = ChartPalette[1], title = "")
                }
            }
        }
        item { Text("已解锁成就", style = MaterialTheme.typography.titleMedium) }
        items(achievements) { a ->
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Row(modifier = Modifier.padding(12.dp)) {
                    Text(a.name)
                }
            }
        }
    }
}

@Composable
private fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleLarge)
        }
    }
}

private fun buildLast30(history: List<ReviewRecord>): Pair<List<String>, List<Int>> {
    val today = java.time.LocalDate.now()
    val map = history.groupBy { it.date }.mapValues { it.value.size }
    val labels = mutableListOf<String>()
    val values = mutableListOf<Int>()
    for (i in 29 downTo 0) {
        val d = today.minusDays(i.toLong())
        labels.add(d.dayOfMonth.toString())
        values.add(map[d] ?: 0)
    }
    return labels to values
}