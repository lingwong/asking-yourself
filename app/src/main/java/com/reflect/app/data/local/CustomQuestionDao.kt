package com.reflect.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import androidx.room.Update

@Dao
interface CustomQuestionDao {
    @Query("SELECT * FROM custom_questions")
    suspend fun getAll(): List<CustomQuestionEntity>

    @Insert
    suspend fun insert(entity: CustomQuestionEntity): Long

    @Update
    suspend fun update(entity: CustomQuestionEntity)

    @Delete
    suspend fun delete(entity: CustomQuestionEntity)
}