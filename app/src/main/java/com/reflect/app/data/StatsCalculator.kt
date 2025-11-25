package com.reflect.app.data

import com.reflect.app.model.ReviewMode
import com.reflect.app.model.ReviewRecord
import java.time.LocalDate

data class Stats(
    val totalDays: Int,
    val consecutiveDays: Int,
    val monthlyRate: Int,
    val yearlyRate: Int,
    val modeUsage: Map<ReviewMode, Int>,
    val categoryFrequency: Map<String, Int>,
    val solvedRate: Int
)

object StatsCalculator {
    fun compute(history: List<ReviewRecord>): Stats {
        val days = history.map { it.date }.toSet()
        val totalDays = days.size
        val consecutiveDays = calcStreak(days)
        val now = LocalDate.now()
        val monthDays = now.lengthOfMonth()
        val monthSet = days.filter { it.year == now.year && it.month == now.month }.toSet()
        val monthlyRate = if (monthDays == 0) 0 else (monthSet.size * 100 / monthDays)
        val yearDaysTotal = if (isLeap(now.year)) 366 else 365
        val yearSet = days.filter { it.year == now.year }.toSet()
        val yearlyRate = if (yearDaysTotal == 0) 0 else (yearSet.size * 100 / yearDaysTotal)
        val modeUsage = history.groupingBy { it.mode }.eachCount()
        val categoryFrequency = buildCategoryFreq(history)
        val answered = history.sumOf { it.answers.count { a -> a.isNotBlank() } }
        val asked = history.sumOf { it.questionIds.size }
        val solvedRate = if (asked == 0) 0 else (answered * 100 / asked)
        return Stats(totalDays, consecutiveDays, monthlyRate, yearlyRate, modeUsage, categoryFrequency, solvedRate)
    }

    private fun calcStreak(days: Set<LocalDate>): Int {
        var streak = 0
        var cursor = LocalDate.now()
        while (cursor in days) {
            streak++
            cursor = cursor.minusDays(1)
        }
        return streak
    }

    private fun isLeap(year: Int): Boolean = java.time.Year.of(year).isLeap

    private fun buildCategoryFreq(history: List<ReviewRecord>): Map<String, Int> {
        val repo = QuestionRepository
        val freq = mutableMapOf<String, Int>()
        history.forEach { r ->
            r.questionIds.forEach { id ->
                val q = repo.getById(id) ?: return@forEach
                freq[q.category] = (freq[q.category] ?: 0) + 1
            }
        }
        return freq
    }
}