package com.roaa.data.local

import androidx.room.*
import com.roaa.data.local.converter.ServiceTypeConverter
import com.roaa.data.local.dao.PasswordDao
import com.roaa.data.local.entity.PasswordEntity


@Database(
    entities = [PasswordEntity::class],
    version = 2,
    exportSchema = true
)

@TypeConverters(ServiceTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun passwordDao(): PasswordDao

}