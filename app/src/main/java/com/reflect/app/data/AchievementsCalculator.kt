package com.reflect.app.data

import com.reflect.app.model.ReviewMode
import com.reflect.app.model.ReviewRecord

data class Achievement(val id: String, val name: String)

object AchievementsCalculator {
    fun unlocked(history: List<ReviewRecord>): List<Achievement> {
        val result = mutableListOf<Achievement>()
        if (history.isNotEmpty()) result += Achievement("first", "新手上路")
        if (history.any { it.answers.joinToString("").length >= 500 }) result += Achievement("deep", "深度思考者")
        val solvedCount = history.sumOf { r -> r.answers.count { it.isNotBlank() } }
        if (solvedCount >= 30) result += Achievement("solver", "问题解决专家")
        val totalDays = history.map { it.date }.toSet().size
        if (totalDays >= 30) result += Achievement("days", "反思达人")
        val categories = QuestionRepository.allCategories()
        val seenCats = history.flatMap { r -> r.questionIds.mapNotNull { id -> QuestionRepository.getById(id)?.category } }.toSet()
        if (seenCats.containsAll(categories)) result += Achievement("explorer", "问题探索者")
        val modes = history.map { it.mode }.toSet()
        if (modes.containsAll(setOf(ReviewMode.FreeDiary, ReviewMode.ExperienceSummary))) result += Achievement("modes", "模式探索者")
        val expCount = history.count { it.mode == ReviewMode.ExperienceSummary }
        if (expCount >= 30) result += Achievement("collector", "经验收集家")
        return result
    }
}