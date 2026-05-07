package com.roaa.data.di

import com.roaa.data.repository.BrandRepositoryImpl
import com.roaa.data.repository.PasswordRepositoryImpl
import com.roaa.domain.repositoryInterfaces.BrandRepository
import com.roaa.domain.repositoryInterfaces.PasswordRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPasswordRepository(
        impl: PasswordRepositoryImpl
    ): PasswordRepository

    @Binds
    @Singleton
    abstract fun bindBrandRepository(
        impl: BrandRepositoryImpl
    ): BrandRepository
}