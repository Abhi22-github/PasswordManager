package com.roaa.data.di

import android.content.Context
import androidx.room.Room
import com.roaa.data.local.AppDatabase
import com.roaa.data.local.dao.PasswordDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        DATABASE_NAME
    )
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun providePasswordDao(database: AppDatabase): PasswordDao =
        database.passwordDao()

    private const val DATABASE_NAME = "passwordmanager.db"
}