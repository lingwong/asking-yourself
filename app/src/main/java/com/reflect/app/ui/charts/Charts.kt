package com.reflect.app.ui.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.Alignment

@Composable
fun LineChart(labels: List<String>, values: List<Int>, lineColor: Color, title: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(title)
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            val yTicks = 4
            val maxV = (values.maxOrNull() ?: 0).coerceAtLeast(1)
            Column(modifier = Modifier.width(36.dp).height(200.dp), verticalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween) {
                (0..yTicks).forEach { i ->
                    val v = (maxV * (yTicks - i)) / yTicks
                    Text(v.toString())
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Canvas(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                    if (values.isEmpty()) return@Canvas
                    val topPad = 8f
                    val bottomPad = 8f
                    val plotW = size.width
                    val plotH = size.height - topPad - bottomPad
                    val originX = 0f
                    val originY = topPad + plotH
                    drawLine(color = Color.DarkGray, start = androidx.compose.ui.geometry.Offset(originX, topPad), end = androidx.compose.ui.geometry.Offset(originX, originY), strokeWidth = 2f)
                    drawLine(color = Color.DarkGray, start = androidx.compose.ui.geometry.Offset(originX, originY), end = androidx.compose.ui.geometry.Offset(originX + plotW, originY), strokeWidth = 2f)
                    val xSteps = (values.size.coerceAtLeast(2) - 1)
                    val stepX = plotW / xSteps
                    val path = Path()
                    values.forEachIndexed { i, v ->
                        val x = originX + i * stepX
                        val y = topPad + plotH - (v.toFloat() / maxV) * plotH
                        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                    }
                    drawPath(path = path, color = lineColor, style = Stroke(width = 4f))
                    values.forEachIndexed { i, v ->
                        val x = originX + i * stepX
                        val y = topPad + plotH - (v.toFloat() / maxV) * plotH
                        drawCircle(color = lineColor, radius = 5f, center = androidx.compose.ui.geometry.Offset(x, y))
                    }
                    val xTickCount = 6
                    val step = (values.size - 1).coerceAtLeast(1) / xTickCount.toFloat()
                    for (i in 0..xTickCount) {
                        val idx = (step * i).toInt().coerceAtMost(values.size - 1)
                        val x = originX + idx * stepX
                        drawLine(color = Color.DarkGray, start = androidx.compose.ui.geometry.Offset(x, originY), end = androidx.compose.ui.geometry.Offset(x, originY + 6f), strokeWidth = 2f)
                    }
                }
                val xTickCount = 6
                val indices = (0..xTickCount).map { i -> ((labels.size - 1) * i / xTickCount).coerceAtMost(labels.size - 1) }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween) {
                    indices.forEach { idx ->
                        Text(labels.getOrNull(idx) ?: (idx + 1).toString())
                    }
                }
            }
        }
    }
}

@Composable
fun BarChart(data: List<Pair<String, Int>>, color: Color, title: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(title)
        Canvas(modifier = Modifier.fillMaxWidth().height(200.dp)) {
            if (data.isEmpty()) return@Canvas
            val maxV = (data.maxOf { it.second }).coerceAtLeast(1)
            val barCount = data.size
            val barWidth = size.width / (barCount * 1.5f)
            val gap = barWidth * 0.5f
            var x = gap
            data.forEach { (_, v) ->
                val h = (v.toFloat() / maxV) * size.height
                drawRect(color = color, topLeft = androidx.compose.ui.geometry.Offset(x, size.height - h), size = androidx.compose.ui.geometry.Size(barWidth, h))
                x += barWidth + gap
            }
        }
        data.forEach { (label, v) -> Text("$label: $v") }
    }
}

@Composable
fun PieChart(data: List<Pair<String, Int>>, colors: List<Color>, title: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(title)
        Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
            Canvas(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                val total = data.sumOf { it.second }.coerceAtLeast(1)
                var start = 0f
                data.forEachIndexed { i, (_, v) ->
                    val sweep = 360f * (v.toFloat() / total)
                    drawArc(color = colors[i % colors.size], startAngle = start, sweepAngle = sweep, useCenter = true, size = androidx.compose.ui.geometry.Size(size.width, size.height))
                    start += sweep
                }
            }
        }
        data.forEachIndexed { i, (label, v) -> Text("${label}: ${v}") }
    }
}

val ChartPalette = listOf(Color(0xFF2D5A27), Color(0xFFD4AF37), Color(0xFF81C784), Color(0xFF4CAF50), Color(0xFF8BC34A))