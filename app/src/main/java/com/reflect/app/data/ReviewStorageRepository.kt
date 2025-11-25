package com.reflect.app.data

import android.content.Context
import com.reflect.app.data.local.AppDatabase
import com.reflect.app.data.local.ReviewRecordEntity
import com.reflect.app.model.ReviewMode
import com.reflect.app.model.ReviewRecord
import java.time.LocalDate

object ReviewStorageRepository {
    private var db: AppDatabase? = null

    fun init(context: Context) {
        if (db == null) db = AppDatabase.get(context)
    }

    suspend fun getAll(): List<ReviewRecord> {
        val dao = requireNotNull(db).reviewRecordDao()
        return dao.getAll().map { it.toModel() }
    }

    suspend fun insert(record: ReviewRecord) {
        val dao = requireNotNull(db).reviewRecordDao()
        dao.insert(record.toEntity())
    }

    suspend fun getBetween(start: LocalDate, end: LocalDate): List<ReviewRecord> {
        val dao = requireNotNull(db).reviewRecordDao()
        return dao.getBetween(start.toEpochDay(), end.toEpochDay()).map { it.toModel() }
    }

    suspend fun delete(record: ReviewRecord) {
        val dao = requireNotNull(db).reviewRecordDao()
        val list = dao.getByDate(record.date.toEpochDay())
        val target = list.firstOrNull { it.mode == record.mode.name && it.answers == record.answers && it.questionIds == record.questionIds } ?: return
        dao.delete(target)
    }

    suspend fun replace(old: ReviewRecord, new: ReviewRecord) {
        delete(old)
        insert(new)
    }
}

private fun ReviewRecordEntity.toModel(): ReviewRecord =
    ReviewRecord(LocalDate.ofEpochDay(dateEpochDay), ReviewMode.valueOf(mode), answers, questionIds, createdAtMillis)

private fun ReviewRecord.toEntity(): ReviewRecordEntity =
    ReviewRecordEntity(0, date.toEpochDay(), mode.name, answers, questionIds, createdAtMillis)
