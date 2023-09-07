package com.example.diary.di

import android.content.Context
import androidx.room.Room
import com.example.diary.data.local.ImageDatabase
import com.example.diary.data.local.ImageToDeleteDao
import com.example.diary.data.local.ImageToUploadDao
import com.example.diary.util.Constants
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
    fun providesImageDatabase(@ApplicationContext context: Context): ImageDatabase {
        return Room.databaseBuilder(
            context,
            ImageDatabase::class.java,
            Constants.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun providesImageToUploadDao(imageDatabase: ImageDatabase): ImageToUploadDao {
        return imageDatabase.imageToUploadDao()
    }

    @Provides
    @Singleton
    fun providesImageToDeleteDao(imageDatabase: ImageDatabase): ImageToDeleteDao {
        return imageDatabase.imageToDeleteDao()
    }
}