package com.reflect.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import androidx.room.Update

@Dao
interface ReviewRecordDao {
    @Query("SELECT * FROM review_records ORDER BY createdAtMillis DESC")
    suspend fun getAll(): List<ReviewRecordEntity>

    @Query("SELECT * FROM review_records WHERE dateEpochDay BETWEEN :start AND :end")
    suspend fun getBetween(start: Long, end: Long): List<ReviewRecordEntity>

    @Query("SELECT * FROM review_records WHERE dateEpochDay = :epoch")
    suspend fun getByDate(epoch: Long): List<ReviewRecordEntity>

    @Insert
    suspend fun insert(entity: ReviewRecordEntity): Long

    @Update
    suspend fun update(entity: ReviewRecordEntity)

    @Delete
    suspend fun delete(entity: ReviewRecordEntity)
}
