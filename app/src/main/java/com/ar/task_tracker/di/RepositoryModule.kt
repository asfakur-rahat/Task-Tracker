package com.ar.task_tracker.di

import com.ar.task_tracker.data.ListRepositoryImpl
import com.ar.task_tracker.domain.repository.ListRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent



// Binds for repository
@InstallIn(SingletonComponent::class)
@Module
abstract class RepositoryModule {
    @Binds
    abstract fun provideListRepository(impl: ListRepositoryImpl): ListRepository
}