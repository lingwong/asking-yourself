package com.reflect.app.ocr

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import com.google.mlkit.vision.text.TextRecognition

object OCRProcessor {
    suspend fun recognize(context: Context, uri: Uri): String {
        val client = TextRecognition.getClient(ChineseTextRecognizerOptions.Builder().build())
        val input = context.contentResolver.openInputStream(uri)?.use { stream ->
            val bmp = BitmapFactory.decodeStream(stream)
            InputImage.fromBitmap(bmp, 0)
        } ?: return ""
        val res = client.process(input).await()
        return res.text
    }
}

private suspend fun <T> com.google.android.gms.tasks.Task<T>.await(): T = kotlinx.coroutines.suspendCancellableCoroutine { cont ->
    addOnSuccessListener { cont.resume(it, null) }
    addOnFailureListener { e -> cont.resumeWith(Result.failure(e)) }
}