package com.reflect.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.reflect.app.data.FlashQuestionRepository
import com.reflect.app.data.local.FlashQuestionEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashQuestionsScreen() {
    val ctx = LocalContext.current
    LaunchedEffect(Unit) { FlashQuestionRepository.init(ctx) }
    val scope = rememberCoroutineScope()
    var inputText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { }

            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("为什么呢？", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(value = inputText, onValueChange = { inputText = it }, label = { Text("记录问题") }, modifier = Modifier.fillMaxWidth())
        
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        ElevatedButton(onClick = {
            if (inputText.isBlank()) {
                android.widget.Toast.makeText(ctx, "内容未输入", android.widget.Toast.LENGTH_SHORT).show()
                return@ElevatedButton
            }
            scope.launch(Dispatchers.IO) {
                FlashQuestionRepository.insert(inputText.trim(), "", emptyList())
            }
            inputText = ""
            android.widget.Toast.makeText(ctx, "保存成功", android.widget.Toast.LENGTH_SHORT).show()
        }) { Text("保存") }
        }

    }
}
