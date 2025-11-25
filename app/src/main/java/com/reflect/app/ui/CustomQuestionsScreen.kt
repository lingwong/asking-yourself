package com.reflect.app.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.reflect.app.data.CustomQuestionRepository
import com.reflect.app.model.Question
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

@Composable
fun CustomQuestionsScreen() {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var list by remember { mutableStateOf(listOf<Question>()) }
    var text by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("成长反思") }
    var tags by remember { mutableStateOf("") }
    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            scope.launch {
                val s = ctx.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() } ?: "[]"
                CustomQuestionRepository.importJson(s)
                list = CustomQuestionRepository.list()
            }
        }
    }
    LaunchedEffect(Unit) {
        CustomQuestionRepository.init(ctx)
        list = CustomQuestionRepository.list()
    }
    Column(modifier = Modifier.padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = text, onValueChange = { text = it }, label = { Text("问题") }, modifier = Modifier.weight(1f))
            OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("分类") })
        }
        OutlinedTextField(value = tags, onValueChange = { tags = it }, label = { Text("标签(逗号分隔)") }, modifier = Modifier.fillMaxWidth())
        Button(onClick = {
            scope.launch {
                CustomQuestionRepository.add(text, category, if (tags.isBlank()) emptyList() else tags.split(","))
                list = CustomQuestionRepository.list()
                text = ""
                tags = ""
            }
        }, modifier = Modifier.padding(top = 8.dp)) { Text("添加") }
        ElevatedButton(onClick = { importLauncher.launch("application/json") }, modifier = Modifier.padding(top = 8.dp)) { Text("导入外部问题库") }
        LazyColumn(modifier = Modifier.padding(top = 12.dp)) {
            items(list) { q ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(q.text)
                    Text(q.category)
                }
            }
        }
    }
}