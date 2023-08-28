package com.example.diary.di

import com.example.diary.data.remote.MongoRepositoryImpl
import com.example.diary.domain.repository.MongoRepository
import com.example.diary.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.mongodb.App
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Singleton
    @Provides
    fun providesMongoApp(): App {
        return App.create(Constants.APP_ID)
    }


    @Singleton
    @Provides
    fun providesMongoRepository(mongoApp: App): MongoRepository {
        return MongoRepositoryImpl(mongoApp)
    }

}