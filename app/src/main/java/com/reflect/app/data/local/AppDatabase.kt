package com.reflect.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [ReviewRecordEntity::class, CustomQuestionEntity::class, FlashQuestionEntity::class], version = 4, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun reviewRecordDao(): ReviewRecordDao
    abstract fun customQuestionDao(): CustomQuestionDao
    abstract fun flashQuestionDao(): FlashQuestionDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun get(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                val MIGRATION_3_4 = androidx.room.migration.Migration(3, 4) { db ->
                    db.execSQL("ALTER TABLE review_records ADD COLUMN createdAtMillis INTEGER NOT NULL DEFAULT 0")
                    db.execSQL("UPDATE review_records SET createdAtMillis = dateEpochDay * 86400000")
                }
                instance ?: Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "review.db").addMigrations(MIGRATION_3_4).fallbackToDestructiveMigration().build().also { instance = it }
            }
        }
    }
}
