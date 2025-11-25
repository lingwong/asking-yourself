package com.reflect.app.data

import com.reflect.app.model.Question
import com.reflect.app.model.ReviewRecord
import java.time.LocalDate
import kotlin.random.Random

object QuestionRepository {
    private val questions = listOf(
        Question("g1", "成长反思", "今天学到了什么新知识？"),
        Question("g2", "成长反思", "今天哪个时刻让你感到最有成就感？"),
        Question("q1", "情绪管理", "今天的情绪状态如何？"),
        Question("q2", "情绪管理", "有什么事情影响了你的心情？"),
        Question("r1", "人际关系", "今天与他人的互动中有什么收获？"),
        Question("r2", "人际关系", "是否有需要改善的沟通方式？"),
        Question("t1", "目标规划", "今天离你的目标更近了吗？"),
        Question("t2", "目标规划", "明天最重要的三件事是什么？"),
        Question("p1", "问题解决", "今天遇到的最大挑战是什么？"),
        Question("p2", "问题解决", "你是如何应对的？"),
        Question("a1", "感恩反思", "今天最感谢的人或事是什么？"),
        Question("a2", "感恩反思", "有什么值得庆祝的小进步？")
    )

    fun getDailyQuestions(date: LocalDate, history: List<ReviewRecord>, refreshIndex: Int = 0, favorites: Set<String> = emptySet(), custom: List<Question> = emptyList()): List<Question> {
        val windowStart = date.minusDays(6)
        val recentIds = history
            .filter { !it.date.isBefore(windowStart) && !it.date.isAfter(date) }
            .flatMap { it.questionIds }
            .toSet()
        val base = (questions + custom).filter { it.id !in recentIds }
        val pool = mutableListOf<Question>().apply {
            addAll(base)
            base.filter { it.id in favorites }.forEach { q -> repeat(2) { add(q) } }
        }
        val rng = Random(date.toEpochDay() + refreshIndex)
        return pool.shuffled(rng).distinctBy { it.id }.take(3).ifEmpty { questions.shuffled(rng).take(3) }
    }

    fun getById(id: String): Question? = questions.find { it.id == id }

    fun allCategories(): Set<String> = questions.map { it.category }.toSet()
}