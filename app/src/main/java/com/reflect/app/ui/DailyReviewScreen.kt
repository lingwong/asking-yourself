package com.reflect.app.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.CardDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.automirrored.filled.List
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import com.reflect.app.data.QuestionRepository
import com.reflect.app.data.RefreshManager
import com.reflect.app.data.FavoriteManager
import com.reflect.app.model.Question
import com.reflect.app.model.ReviewMode
import com.reflect.app.model.ReviewRecord
import java.time.LocalDate
import kotlinx.coroutines.launch
import android.speech.RecognizerIntent
import android.content.Intent
import com.reflect.app.ocr.OCRProcessor
import android.net.Uri
import android.Manifest
import android.content.pm.PackageManager
import android.speech.SpeechRecognizer
import androidx.core.content.ContextCompat
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.TextButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.graphics.Color

@Composable
fun DailyReviewScreen(
    date: LocalDate,
    mode: ReviewMode,
    history: List<ReviewRecord>,
    onSave: (ReviewRecord) -> Unit
) {
    when (mode) {
        ReviewMode.FreeDiary -> FreeDiarySection(date, onSave)
        ReviewMode.ExperienceSummary -> ExperienceSummarySection(date, onSave)
        ReviewMode.Questions -> QuestionsModeSection(date, history, onSave)
    }
}

@Composable
fun FreeDiarySection(date: LocalDate, onSave: (ReviewRecord) -> Unit) {
    var content by remember { mutableStateOf("") }
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var showVoice by remember { mutableStateOf(false) }
    val imageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                val text = OCRProcessor.recognize(ctx, uri)
                content = if (content.isEmpty()) text else content + "\n" + text
            }
        }
    }
    Column(modifier = Modifier.padding(16.dp)) {
        androidx.compose.material3.ElevatedCard(modifier = Modifier.fillMaxWidth(), shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(imageVector = Icons.Filled.Description, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text("今日日记", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                }
                Text("自由书写今天的所思所想，不受问题限制", modifier = Modifier.padding(top = 4.dp), style = MaterialTheme.typography.bodySmall)
            }
        }
        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            placeholder = { Text("记录下今天发生的事情、你的想法、感受、收获...") },
            trailingIcon = {
                androidx.compose.foundation.layout.Row(horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = {
                        showVoice = true
                    }) { Icon(imageVector = Icons.Filled.Mic, contentDescription = null) }
                    IconButton(onClick = { imageLauncher.launch("image/*") }) { Icon(imageVector = Icons.Filled.Image, contentDescription = null) }
                }
            }
        )
        if (showVoice) {
            VoiceInputSheet(onClose = { showVoice = false }, onText = { t -> content = t })
        }
        Row(modifier = Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.End) {
            ElevatedButton(onClick = {
                onSave(ReviewRecord(date, ReviewMode.FreeDiary, listOf(content), emptyList(), System.currentTimeMillis()))
                content = ""
            }) { Text("保存") }
        }
    }
}

@Composable
fun ExperienceSummarySection(date: LocalDate, onSave: (ReviewRecord) -> Unit) {
    var text by remember { mutableStateOf("") }
    var showVoice by remember { mutableStateOf(false) }
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val imageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                val ocrText = OCRProcessor.recognize(ctx, uri)
                text = if (text.isEmpty()) ocrText else text + "\n" + ocrText
            }
        }
    }
    Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
        androidx.compose.material3.ElevatedCard(shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(imageVector = Icons.Filled.FlashOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text("今日经验", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                }
                Text("花5分钟想一想，今天是否有一条经验，可以复用到以后的生活中？", modifier = Modifier.padding(top = 4.dp), style = MaterialTheme.typography.bodySmall)
            }
        }
        androidx.compose.material3.ElevatedCard(modifier = Modifier.fillMaxWidth().padding(top = 12.dp), shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(imageVector = Icons.Filled.Lightbulb, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text("经验可以是：", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }
                Text("• 生活技巧：例如烹饪前先焯水更去腥", modifier = Modifier.padding(top = 4.dp), style = MaterialTheme.typography.bodySmall)
                Text("• 工作经验：沟通时先明确意图和范围", modifier = Modifier.padding(top = 2.dp), style = MaterialTheme.typography.bodySmall)
                Text("• 观察发现：某事在早上更高效", modifier = Modifier.padding(top = 2.dp), style = MaterialTheme.typography.bodySmall)
            }
        }
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            placeholder = { Text("用一句话记录下今天学到的经验...") },
            trailingIcon = {
                androidx.compose.foundation.layout.Row(horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = { showVoice = true }) { Icon(imageVector = Icons.Filled.Mic, contentDescription = null) }
                    IconButton(onClick = { imageLauncher.launch("image/*") }) { Icon(imageVector = Icons.Filled.Image, contentDescription = null) }
                }
            }
        )
        if (showVoice) {
            VoiceInputSheet(onClose = { showVoice = false }, onText = { t -> text = t })
        }
        Row(modifier = Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.End) {
            ElevatedButton(onClick = {
                onSave(ReviewRecord(date, ReviewMode.ExperienceSummary, listOf(text), emptyList(), System.currentTimeMillis()))
                text = ""
            }) { Text("保存") }
        }
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun QuestionsModeSection(date: LocalDate, history: List<ReviewRecord>, onSave: (ReviewRecord) -> Unit) {
    Column(modifier = Modifier.padding(8.dp)) {
        androidx.compose.material3.ElevatedCard(modifier = Modifier.fillMaxWidth(), shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.List, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text("问题模式", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                }
                Text("按问题逐条记录你的回答，可刷新获取不同问题", modifier = Modifier.padding(top = 4.dp), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
    val ctx = LocalContext.current
    var refreshIndex by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()
    var favorites by remember { mutableStateOf(setOf<String>()) }
    var customQuestions by remember { mutableStateOf(listOf<Question>()) }
    var questions by remember(date, history, refreshIndex, customQuestions) { mutableStateOf(QuestionRepository.getDailyQuestions(date, history, refreshIndex, favorites, customQuestions)) }
    val answers = remember(questions) { mutableStateListOf(*Array(questions.size) { "" }) }
    var refreshCount by remember { mutableStateOf(0) }
    LaunchedEffect(date) {
        refreshCount = RefreshManager.getCount(ctx, date)
        favorites = FavoriteManager.getFavorites(ctx)
        com.reflect.app.data.CustomQuestionRepository.init(ctx)
        customQuestions = com.reflect.app.data.CustomQuestionRepository.list()
    }
    LazyColumn(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.weight(1f))
                ElevatedButton(onClick = {
                    scope.launch {
                        val newCount = RefreshManager.tryIncrement(ctx, date)
                        refreshCount = newCount
                        if (newCount <= 3) {
                            refreshIndex = newCount
                            questions = QuestionRepository.getDailyQuestions(date, history, refreshIndex, favorites, customQuestions)
                            answers.clear()
                            answers.addAll(List(questions.size) { "" })
                        }
                    }
                }, enabled = refreshCount < 3) { Text("刷新问题(${3 - refreshCount})") }
                ElevatedButton(onClick = {
                    onSave(ReviewRecord(date, ReviewMode.Questions, answers.toList(), questions.map { it.id }, System.currentTimeMillis()))
                    answers.clear()
                    answers.addAll(List(questions.size) { "" })
                }) { Text("保存") }
            }
        }
        itemsIndexed(questions) { index, q ->
            QuestionItem(
                index = index,
                question = q,
                value = answers[index],
                favorite = favorites.contains(q.id),
                onToggleFavorite = {
                    scope.launch {
                        FavoriteManager.toggle(ctx, q.id)
                        favorites = FavoriteManager.getFavorites(ctx)
                    }
                },
                onValueChange = { text -> answers[index] = text }
            )
        }
        
    }
}

@Composable
fun QuestionItem(index: Int, question: Question, value: String, favorite: Boolean, onToggleFavorite: () -> Unit, onValueChange: (String) -> Unit) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var showVoice by remember { mutableStateOf(false) }
    val imageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                val text = OCRProcessor.recognize(ctx, uri)
                onValueChange(if (value.isEmpty()) text else value + "\n" + text)
            }
        }
    }
    androidx.compose.material3.ElevatedCard(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            androidx.compose.material3.Text(text = question.text, style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = value,
                onValueChange = { onValueChange(it) },
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                placeholder = { androidx.compose.material3.Text("记录下你的想法...") },
                trailingIcon = {
                    androidx.compose.foundation.layout.Row(horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)) {
                        IconButton(onClick = onToggleFavorite) {
                            Icon(imageVector = Icons.Filled.Star, contentDescription = null, tint = if (favorite) Color(0xFFFFC107) else MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(onClick = {
                            showVoice = true
                        }) { Icon(imageVector = Icons.Filled.Mic, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) }
                        IconButton(onClick = { imageLauncher.launch("image/*") }) { Icon(imageVector = Icons.Filled.Image, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) }
                    }
                }
            )
            if (showVoice) {
                VoiceInputSheet(onClose = { showVoice = false }, onText = { t -> onValueChange(t) })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceInputSheet(onClose: () -> Unit, onText: (String) -> Unit) {
    val ctx = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var text by remember { mutableStateOf("") }
    var listening by remember { mutableStateOf(false) }
    val speechRecognizer = remember {
        SpeechRecognizer.createSpeechRecognizer(ctx)
    }
    DisposableEffect(Unit) {
        onDispose {
            speechRecognizer.destroy()
        }
    }
    val startListening = {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "zh-CN")
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        speechRecognizer.setRecognitionListener(object : android.speech.RecognitionListener {
            override fun onReadyForSpeech(params: android.os.Bundle?) {}
            override fun onBeginningOfSpeech() { listening = true }
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() { listening = false }
            override fun onError(error: Int) { listening = false }
            override fun onResults(results: android.os.Bundle) {
                val list = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val t = list?.firstOrNull()
                if (t != null) { text = t; onText(t) }
            }
            override fun onPartialResults(partialResults: android.os.Bundle) {
                val list = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val t = list?.firstOrNull()
                if (t != null) { text = t }
            }
            override fun onEvent(eventType: Int, params: android.os.Bundle?) {}
        })
        speechRecognizer.startListening(intent)
    }
    val hasPermission = ContextCompat.checkSelfPermission(ctx, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) startListening()
    }
    LaunchedEffect(Unit) {
        if (hasPermission) startListening() else permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }
    ModalBottomSheet(onDismissRequest = { onClose() }, sheetState = sheetState) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = if (listening) "正在识别..." else "语音已停止")
            OutlinedTextField(value = text, onValueChange = { v -> text = v; onText(v) }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), placeholder = { Text("语音识别结果，可编辑...") })
            Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = { startListening() }) { Text("继续识别") }
                TextButton(onClick = { onClose() }) { Text("关闭") }
            }
        }
    }
}
