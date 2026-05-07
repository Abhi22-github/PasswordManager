package com.roaa.data.di

import com.roaa.data.repository.LogoUrlBuilder
import com.roaa.domain.repositoryInterfaces.BrandLogoUrlBuilder
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
abstract class LogoModule {
    @Binds
    abstract fun bindBrandLogoUrlBuilder(
        impl: LogoUrlBuilder
    ): BrandLogoUrlBuilder
}