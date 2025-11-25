package com.reflect.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import androidx.room.Update

@Dao
interface FlashQuestionDao {
    @Query("SELECT * FROM flash_questions ORDER BY createdAtMillis DESC")
    suspend fun getAll(): List<FlashQuestionEntity>

    @Query("SELECT * FROM flash_questions WHERE text LIKE '%' || :q || '%' ORDER BY createdAtMillis DESC")
    suspend fun search(q: String): List<FlashQuestionEntity>

    @Insert
    suspend fun insert(entity: FlashQuestionEntity): Long

    @Update
    suspend fun update(entity: FlashQuestionEntity)

    @Delete
    suspend fun delete(entity: FlashQuestionEntity)
}