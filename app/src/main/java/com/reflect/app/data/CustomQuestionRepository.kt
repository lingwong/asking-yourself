package com.reflect.app.data

import android.content.Context
import com.reflect.app.data.local.AppDatabase
import com.reflect.app.data.local.CustomQuestionEntity
import com.reflect.app.model.Question
import org.json.JSONArray

object CustomQuestionRepository {
    private var db: AppDatabase? = null

    fun init(context: Context) {
        if (db == null) db = AppDatabase.get(context)
    }

    suspend fun list(): List<Question> {
        val dao = requireNotNull(db).customQuestionDao()
        return dao.getAll().map { Question(it.id.toString(), it.category, it.text) }
    }

    suspend fun add(text: String, category: String, tags: List<String>) {
        val dao = requireNotNull(db).customQuestionDao()
        dao.insert(CustomQuestionEntity(0, text, category, tags))
    }

    suspend fun update(id: Long, text: String, category: String, tags: List<String>) {
        val dao = requireNotNull(db).customQuestionDao()
        dao.update(CustomQuestionEntity(id, text, category, tags))
    }

    suspend fun delete(id: Long, text: String, category: String, tags: List<String>) {
        val dao = requireNotNull(db).customQuestionDao()
        dao.delete(CustomQuestionEntity(id, text, category, tags))
    }

    suspend fun importJson(json: String) {
        val arr = JSONArray(json)
        val dao = requireNotNull(db).customQuestionDao()
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            val text = obj.optString("text")
            val category = obj.optString("category")
            val tags = obj.optJSONArray("tags")
            val list = mutableListOf<String>()
            if (tags != null) for (j in 0 until tags.length()) list.add(tags.getString(j))
            dao.insert(CustomQuestionEntity(0, text, category, list))
        }
    }
}