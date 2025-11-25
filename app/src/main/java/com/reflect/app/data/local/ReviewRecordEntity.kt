package com.reflect.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "review_records")
data class ReviewRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateEpochDay: Long,
    val mode: String,
    val answers: List<String>,
    val questionIds: List<String>,
    val createdAtMillis: Long
)
