package com.reflect.app.model

import java.time.LocalDate

data class ReviewRecord(
    val date: LocalDate,
    val mode: ReviewMode,
    val answers: List<String>,
    val questionIds: List<String>,
    val createdAtMillis: Long
)

enum class ReviewMode { FreeDiary, ExperienceSummary, Questions }
