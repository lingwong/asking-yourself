package com.reflect.app.share

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import com.reflect.app.data.QuestionRepository
import com.reflect.app.model.ReviewMode
import com.reflect.app.model.ReviewRecord
import java.io.File
import java.io.FileOutputStream
import java.time.format.DateTimeFormatter

object PdfExporter {
    fun export(context: Context, record: ReviewRecord): Uri {
        val doc = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(1080, 1600, 1).create()
        val page = doc.startPage(pageInfo)
        val c = page.canvas
        val primary = Paint().apply { color = 0xFF2D5A27.toInt(); textSize = 64f; isAntiAlias = true }
        val textPaint = Paint().apply { color = 0xFF333333.toInt(); textSize = 40f; isAntiAlias = true }
        val secondary = Paint().apply { color = 0xFFD4AF37.toInt(); textSize = 36f; isAntiAlias = true }
        val cardPaint = Paint().apply { color = 0xFFFFFFFF.toInt() }
        c.drawColor(0xFFF8F6F0.toInt())
        val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        c.drawText("吾日三省吾心", 64f, 120f, primary)
        c.drawText(record.date.format(fmt), 64f, 180f, secondary)
        c.drawText(if (record.mode == ReviewMode.FreeDiary) "自由日记" else "经验总结", 64f, 240f, secondary)
        c.drawRect(48f, 280f, 1032f, 1500f, cardPaint)
        var y = 340f
        if (record.mode == ReviewMode.ExperienceSummary) {
            record.questionIds.forEachIndexed { idx, id ->
                val q = QuestionRepository.getById(id)?.text ?: ""
                c.drawText("${idx + 1}. $q", 64f, y, textPaint)
                y += 56f
                val ans = record.answers.getOrNull(idx) ?: ""
                for (line in wrap(ans, 900, textPaint)) {
                    c.drawText(line, 64f, y, textPaint)
                    y += 48f
                }
                y += 24f
            }
        } else {
            val ans = record.answers.joinToString("\n")
            for (line in wrap(ans, 900, textPaint)) {
                c.drawText(line, 64f, y, textPaint)
                y += 48f
            }
        }
        doc.finishPage(page)
        val file = File(context.cacheDir, "record_${record.date}.pdf")
        FileOutputStream(file).use { doc.writeTo(it) }
        doc.close()
        return FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)
    }

    fun share(context: Context, uri: Uri) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "application/pdf"
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(Intent.createChooser(intent, "分享复盘PDF"))
    }

    private fun wrap(text: String, maxWidth: Int, paint: Paint): List<String> {
        val lines = mutableListOf<String>()
        var current = StringBuilder()
        for (ch in text) {
            current.append(ch)
            if (paint.measureText(current.toString()) > maxWidth) {
                val s = current.toString()
                lines += s.dropLast(1)
                current = StringBuilder(ch.toString())
            }
        }
        if (current.isNotEmpty()) lines += current.toString()
        return lines
    }
}