package com.reflect.app.data.local

import androidx.room.TypeConverter
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun listToString(list: List<String>): String = list.joinToString("\u0001")

    @TypeConverter
    fun stringToList(s: String): List<String> = if (s.isEmpty()) emptyList() else s.split("\u0001")

    @TypeConverter
    fun localDateToLong(date: LocalDate): Long = date.toEpochDay()

    @TypeConverter
    fun longToLocalDate(value: Long): LocalDate = LocalDate.ofEpochDay(value)
}