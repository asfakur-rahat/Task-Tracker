package com.ar.task_tracker.di

import com.ar.task_tracker.data.firebase.FirebaseServiceImpl
import com.ar.task_tracker.data.firebase.firebase_service.FirebaseService
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CloudModule {

    @Binds
    @Singleton
    abstract fun bindFirebaseService(impl: FirebaseServiceImpl): FirebaseService

    companion object{
        @Provides
        @Singleton
        fun provideFirebaseFireStore(): FirebaseFirestore {
            return FirebaseFirestore.getInstance()
        }
        @Provides
        @Singleton
        fun provideFirebaseStorage(): FirebaseStorage {
            return FirebaseStorage.getInstance()
        }
    }
}