package com.reflect.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.Modifier
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import android.widget.Toast
import com.reflect.app.share.ShareExporter
import com.reflect.app.share.PdfExporter
import com.reflect.app.model.ReviewMode
import com.reflect.app.model.ReviewRecord
import com.reflect.app.data.ReviewStorageRepository
import com.reflect.app.ui.AppScreen
import com.reflect.app.ui.BottomNav
import com.reflect.app.ui.DailyReviewScreen
import com.reflect.app.ui.ModeSelector
import com.reflect.app.ui.StatsScreen
import com.reflect.app.ui.ReminderSettingsScreen
import com.reflect.app.ui.CustomQuestionsScreen
import com.reflect.app.ui.FlashQuestionsScreen
import com.reflect.app.data.FlashQuestionRepository
import com.reflect.app.data.local.FlashQuestionEntity
import com.reflect.app.ui.theme.AppTheme
import java.time.LocalDate
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    App()
                }
            }
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun App() {
    var mode by remember { mutableStateOf(ReviewMode.ExperienceSummary) }
    val history = remember { mutableStateListOf<ReviewRecord>() }
    var screen by remember { mutableStateOf(AppScreen.Review) }
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        ReviewStorageRepository.init(ctx)
        val list = ReviewStorageRepository.getAll()
        history.clear()
        history.addAll(list)
    }

    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(topBar = { com.reflect.app.ui.AppTopBar(current = screen) }, bottomBar = { BottomNav(current = screen, onNavigate = { screen = it }) }, snackbarHost = { SnackbarHost(snackbarHostState) }) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            when (screen) {
                AppScreen.Review -> {
                    ModeSelector(mode = mode, onModeChange = { mode = it }, onNavigateReminder = { })
                    DailyReviewScreen(
                        date = LocalDate.now(),
                        mode = mode,
                        history = history,
                        onSave = { record ->
                            scope.launch {
                                ReviewStorageRepository.insert(record)
                                history.add(record)
                                snackbarHostState.showSnackbar("保存成功")
                            }
                        }
                    )
                }
                AppScreen.Flash -> {
                    FlashQuestionsScreen()
                }
                AppScreen.Reminder -> {
                    ReminderSettingsScreen()
                }
                AppScreen.My -> {
                    SettingsScreen(
                        onOpenHistory = { screen = AppScreen.History },
                        onOpenStats = { screen = AppScreen.Stats },
                        onOpenReminder = { screen = AppScreen.Reminder },
                        onOpenQuestions = { screen = AppScreen.MyQuestions }
                    )
                }
                AppScreen.MyQuestions -> {
                    com.reflect.app.ui.CustomQuestionsScreen()
                }
                AppScreen.History -> {
                    HistoryScreen(history)
                }
                AppScreen.Stats -> {
                    StatsScreen(history)
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(onOpenHistory: () -> Unit, onOpenStats: () -> Unit, onOpenReminder: () -> Unit, onOpenQuestions: () -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "我的")
        ElevatedButton(onClick = onOpenStats, modifier = Modifier.padding(top = 8.dp)) { Text("统计与成就") }
        ElevatedButton(onClick = onOpenReminder, modifier = Modifier.padding(top = 8.dp)) { Text("提醒设置") }
        ElevatedButton(onClick = onOpenQuestions, modifier = Modifier.padding(top = 8.dp)) { Text("题库管理") }
    }
}

 


@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(history: SnapshotStateList<ReviewRecord>) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val clipboard = LocalClipboardManager.current
    var flashList by remember { mutableStateOf<List<FlashQuestionEntity>>(emptyList()) }
    LaunchedEffect(Unit) {
        FlashQuestionRepository.init(ctx)
        flashList = FlashQuestionRepository.getAll()
    }
    var filter by remember { mutableStateOf("") }
    var startDateText by remember { mutableStateOf("") }
    var endDateText by remember { mutableStateOf("") }
    var openStartPicker by remember { mutableStateOf(false) }
    var openEndPicker by remember { mutableStateOf(false) }
    var modeFilter by remember { mutableStateOf("全部") }
    var editingIndex by remember { mutableIntStateOf(-1) }
    var viewingIndex by remember { mutableIntStateOf(-1) }
    val editAnswers = remember { mutableStateListOf<String>() }
    var openSearch by remember { mutableStateOf(false) }
    var flashViewingIndex by remember { mutableIntStateOf(-1) }
    var editingFlash by remember { mutableStateOf(false) }
    var flashEditText by remember { mutableStateOf("") }
    Column(modifier = Modifier.padding(16.dp)) {
        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
            ElevatedButton(onClick = { openSearch = true }) { Text("搜索") }
        }
        val filtered = history.filter { r ->
            val text = r.answers.joinToString("\n").lowercase()
            val f = filter.lowercase()
            val matchText = r.date.toString().contains(f) || r.mode.name.lowercase().contains(f) || text.contains(f)
            val matchMode = modeFilter == "全部" || r.mode.name == modeFilter
            val sd = runCatching { LocalDate.parse(startDateText) }.getOrNull()
            val ed = runCatching { LocalDate.parse(endDateText) }.getOrNull()
            val matchDate = (sd == null || !r.date.isBefore(sd)) && (ed == null || !r.date.isAfter(ed))
            matchText && matchMode && matchDate
        }
        val sd = runCatching { LocalDate.parse(startDateText) }.getOrNull()
        val ed = runCatching { LocalDate.parse(endDateText) }.getOrNull()
        val f = filter.lowercase()
        data class CombinedItem(val ts: Long, val label: String, val text: String, val reviewIndex: Int?, val flashIndex: Int?)
        val zone = java.time.ZoneId.systemDefault()
        val reviewItems = filtered.mapIndexed { i, r ->
            val ts = r.createdAtMillis
            val first = r.answers.joinToString("\n").substringBefore('\n')
            val label = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(java.util.Date(r.createdAtMillis))
            CombinedItem(ts, label, first.ifBlank { "(无内容)" }, i, null)
        }
        val flashItems = flashList.filter { e ->
            val matchText = e.text.lowercase().contains(f)
            val d = java.time.Instant.ofEpochMilli(e.createdAtMillis).atZone(zone).toLocalDate()
            val matchDate = (sd == null || !d.isBefore(sd)) && (ed == null || !d.isAfter(ed))
            matchText && (modeFilter == "全部") && matchDate
        }.mapIndexed { i, e ->
            val first = e.text.substringBefore('\n')
            val label = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(java.util.Date(e.createdAtMillis))
            CombinedItem(e.createdAtMillis, label, first.ifBlank { "(无内容)" }, null, i)
        }
        val combined = (reviewItems + flashItems).sortedByDescending { it.ts }
        Text(text = "共有 ${combined.size} 条记录", modifier = Modifier.padding(vertical = 8.dp))
        LazyColumn {
            items(combined.size) { i ->
                val item = combined[i]
                androidx.compose.material3.ElevatedCard(shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp), modifier = Modifier.padding(bottom = 12.dp).clickable {
                    item.reviewIndex?.let { viewingIndex = it }
                    item.flashIndex?.let { flashViewingIndex = it }
                }) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "时间：${item.label}  ${item.text}", style = androidx.compose.material3.MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(96.dp))
        if (openSearch) {
            AlertDialog(
                onDismissRequest = { openSearch = false },
                confirmButton = { TextButton(onClick = { openSearch = false }) { Text("应用") } },
                dismissButton = {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(onClick = {
                            filter = ""
                            startDateText = ""
                            endDateText = ""
                            modeFilter = "全部"
                        }) { Text("重置") }
                        TextButton(onClick = { openSearch = false }) { Text("取消") }
                    }
                },
                text = {
                    Column {
                        OutlinedTextField(value = filter, onValueChange = { filter = it }, label = { Text("搜索关键词") }, modifier = Modifier.fillMaxWidth())
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                            OutlinedTextField(value = startDateText, onValueChange = {}, label = { Text("开始日期") }, readOnly = true, modifier = Modifier.weight(1f).clickable { openStartPicker = true })
                            OutlinedTextField(value = endDateText, onValueChange = {}, label = { Text("结束日期") }, readOnly = true, modifier = Modifier.weight(1f).clickable { openEndPicker = true })
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                            androidx.compose.material3.AssistChip(onClick = { modeFilter = "全部" }, label = { Text("全部") })
                            androidx.compose.material3.AssistChip(onClick = { modeFilter = ReviewMode.FreeDiary.name }, label = { Text("日记模式") })
                            androidx.compose.material3.AssistChip(onClick = { modeFilter = ReviewMode.ExperienceSummary.name }, label = { Text("快速模式") })
                            androidx.compose.material3.AssistChip(onClick = { modeFilter = ReviewMode.Questions.name }, label = { Text("问题模式") })
                        }
                    }
                }
            )
        }
        if (openStartPicker) {
            androidx.compose.material3.DatePickerDialog(onDismissRequest = { openStartPicker = false }, confirmButton = {
                TextButton(onClick = {
                    openStartPicker = false
                }) { Text("确定") }
            }, dismissButton = { TextButton(onClick = { openStartPicker = false }) { Text("取消") } }) {
                val state = androidx.compose.material3.rememberDatePickerState()
                androidx.compose.material3.DatePicker(state = state)
                val millis = state.selectedDateMillis
                if (millis != null) {
                    val date = java.time.Instant.ofEpochMilli(millis).atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                    startDateText = date.toString()
                }
            }
        }
        if (openEndPicker) {
            androidx.compose.material3.DatePickerDialog(onDismissRequest = { openEndPicker = false }, confirmButton = {
                TextButton(onClick = {
                    openEndPicker = false
                }) { Text("确定") }
            }, dismissButton = { TextButton(onClick = { openEndPicker = false }) { Text("取消") } }) {
                val state = androidx.compose.material3.rememberDatePickerState()
                androidx.compose.material3.DatePicker(state = state)
                val millis = state.selectedDateMillis
                if (millis != null) {
                    val date = java.time.Instant.ofEpochMilli(millis).atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                    endDateText = date.toString()
                }
            }
        }
        if (editingIndex >= 0) {
            val rec = filtered[editingIndex]
            AlertDialog(
                onDismissRequest = { editingIndex = -1 },
                confirmButton = {
                    TextButton(onClick = {
                        scope.launch {
                            val newRec = ReviewRecord(rec.date, rec.mode, editAnswers.toList(), rec.questionIds, rec.createdAtMillis)
                            ReviewStorageRepository.replace(rec, newRec)
                            val list = ReviewStorageRepository.getAll()
                            history.clear()
                            history.addAll(list)
                            editingIndex = -1
                        }
                    }) { Text("保存") }
                },
                dismissButton = {
                    TextButton(onClick = { editingIndex = -1 }) { Text("取消") }
                },
                text = {
                    if (rec.mode == ReviewMode.FreeDiary) {
                        OutlinedTextField(value = editAnswers.getOrNull(0) ?: "", onValueChange = { v ->
                            if (editAnswers.isEmpty()) editAnswers.add(v) else editAnswers[0] = v
                        }, label = { Text("内容") })
                    } else {
                        LazyColumn {
                            items(count = editAnswers.size) { idx ->
                                OutlinedTextField(value = editAnswers[idx], onValueChange = { v -> editAnswers[idx] = v }, label = { Text("回答" + (idx + 1)) })
                            }
                        }
                    }
                }
            )
        }
        if (viewingIndex >= 0) {
            val rec = filtered[viewingIndex]
            val fullText = rec.answers.joinToString("\n").ifBlank { "(无内容)" }
            AlertDialog(
                onDismissRequest = { viewingIndex = -1 },
                confirmButton = {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(onClick = {
                            editingIndex = viewingIndex
                            editAnswers.clear()
                            editAnswers.addAll(filtered[viewingIndex].answers)
                            viewingIndex = -1
                        }) { Text("编辑") }
                        TextButton(onClick = {
                            val recDel = filtered[viewingIndex]
                            scope.launch {
                                ReviewStorageRepository.delete(recDel)
                                val list = ReviewStorageRepository.getAll()
                                history.clear()
                                history.addAll(list)
                                viewingIndex = -1
                            }
                        }) { Text("删除") }
                        TextButton(onClick = {
                            clipboard.setText(AnnotatedString(fullText))
                            Toast.makeText(ctx, "已复制到剪贴板", Toast.LENGTH_SHORT).show()
                        }) { Text("复制") }
                        TextButton(onClick = { viewingIndex = -1 }) { Text("关闭") }
                    }
                },
                text = { Text(fullText) }
            )
        }
        if (flashViewingIndex >= 0) {
            val e = flashList[flashViewingIndex]
            val time = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(java.util.Date(e.createdAtMillis))
            AlertDialog(
                onDismissRequest = { flashViewingIndex = -1 },
                confirmButton = {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(onClick = {
                            editingFlash = true
                            flashEditText = e.text
                        }) { Text("编辑") }
                        TextButton(onClick = {
                            scope.launch(kotlinx.coroutines.Dispatchers.IO) {
                                com.reflect.app.data.FlashQuestionRepository.delete(e)
                                flashList = com.reflect.app.data.FlashQuestionRepository.getAll()
                            }
                            flashViewingIndex = -1
                        }) { Text("删除") }
                        TextButton(onClick = {
                            clipboard.setText(AnnotatedString(e.text))
                            Toast.makeText(ctx, "已复制到剪贴板", Toast.LENGTH_SHORT).show()
                        }) { Text("复制") }
                        TextButton(onClick = { flashViewingIndex = -1 }) { Text("关闭") }
                    }
                },
                text = { Text("时间：$time\n\n" + e.text) }
            )
        }
        if (editingFlash && flashViewingIndex >= 0) {
            val e = flashList[flashViewingIndex]
            AlertDialog(
                onDismissRequest = { editingFlash = false },
                confirmButton = {
                    TextButton(onClick = {
                        scope.launch(kotlinx.coroutines.Dispatchers.IO) {
                            com.reflect.app.data.FlashQuestionRepository.update(e.copy(text = flashEditText.trim()))
                            flashList = com.reflect.app.data.FlashQuestionRepository.getAll()
                        }
                        editingFlash = false
                        flashViewingIndex = -1
                    }) { Text("保存") }
                },
                dismissButton = { TextButton(onClick = { editingFlash = false }) { Text("取消") } },
                text = { OutlinedTextField(value = flashEditText, onValueChange = { flashEditText = it }, label = { Text("问题") }) }
            )
        }
    }
}

@Preview
@Composable
fun PreviewApp() {
    AppTheme { App() }
}
