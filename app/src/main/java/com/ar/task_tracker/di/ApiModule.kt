package com.ar.task_tracker.di

import com.ar.task_tracker.data.network.ApiClient
import com.ar.task_tracker.data.network.api.TaskApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton


// API provider
@InstallIn(SingletonComponent::class)
@Module
object ApiModule {

    @Provides
    @Singleton
    fun provideTaskApi(retrofit: Retrofit): TaskApi {
        return retrofit.create(TaskApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return ApiClient.getClient()
    }
}