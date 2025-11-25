package com.reflect.app.share

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.net.Uri
import androidx.core.content.FileProvider
import com.reflect.app.data.QuestionRepository
import com.reflect.app.model.ReviewMode
import com.reflect.app.model.ReviewRecord
import java.io.File
import java.io.FileOutputStream
import java.time.format.DateTimeFormatter

object ShareExporter {
    fun exportCard(context: Context, record: ReviewRecord): Uri {
        val bmp = renderBitmap(context, record)
        val file = File(context.cacheDir, "record_${record.date}.png")
        FileOutputStream(file).use { out -> bmp.compress(Bitmap.CompressFormat.PNG, 100, out) }
        return FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)
    }

    fun shareImage(context: Context, uri: Uri) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/png"
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(Intent.createChooser(intent, "分享复盘卡片"))
    }

    private fun renderBitmap(context: Context, record: ReviewRecord): Bitmap {
        val width = 1080
        val height = 1600
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)
        val bg = Paint().apply { color = 0xFFF8F6F0.toInt() }
        val primary = Paint().apply { color = 0xFF2D5A27.toInt() }
        val secondary = Paint().apply { color = 0xFFD4AF37.toInt() }
        c.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bg)
        val titlePaint = Paint().apply { color = primary.color; textSize = 64f; isAntiAlias = true }
        val textPaint = Paint().apply { color = 0xFF333333.toInt(); textSize = 40f; isAntiAlias = true }
        val datePaint = Paint().apply { color = secondary.color; textSize = 36f; isAntiAlias = true }
        val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        c.drawText("吾日三省吾心", 64f, 120f, titlePaint)
        c.drawText(record.date.format(fmt), 64f, 180f, datePaint)
        c.drawText(if (record.mode == ReviewMode.FreeDiary) "自由日记" else "经验总结", 64f, 240f, datePaint)
        val card = RectF(48f, 280f, width - 48f, height - 100f)
        val cardPaint = Paint().apply { color = 0xFFFFFFFF.toInt() }
        c.drawRoundRect(card, 24f, 24f, cardPaint)
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
        return bmp
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