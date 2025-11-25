package com.reflect.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "flash_questions")
data class FlashQuestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val text: String,
    val category: String,
    val tags: List<String>,
    val createdAtMillis: Long
)