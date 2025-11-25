package com.reflect.app.data

import android.content.Context
import com.reflect.app.data.local.AppDatabase
import com.reflect.app.data.local.FlashQuestionEntity

object FlashQuestionRepository {
    private var db: AppDatabase? = null

    fun init(context: Context) {
        if (db == null) db = AppDatabase.get(context)
    }

    suspend fun getAll(): List<FlashQuestionEntity> {
        val dao = requireNotNull(db).flashQuestionDao()
        return dao.getAll()
    }

    suspend fun search(q: String): List<FlashQuestionEntity> {
        val dao = requireNotNull(db).flashQuestionDao()
        return dao.search(q)
    }

    suspend fun insert(text: String, category: String, tags: List<String>): Long {
        val dao = requireNotNull(db).flashQuestionDao()
        val now = System.currentTimeMillis()
        return dao.insert(FlashQuestionEntity(0, text, category, tags, now))
    }

    suspend fun update(entity: FlashQuestionEntity) {
        val dao = requireNotNull(db).flashQuestionDao()
        dao.update(entity)
    }

    suspend fun delete(entity: FlashQuestionEntity) {
        val dao = requireNotNull(db).flashQuestionDao()
        dao.delete(entity)
    }
}