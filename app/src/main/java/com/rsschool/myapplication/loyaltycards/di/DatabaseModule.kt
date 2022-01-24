package com.rsschool.myapplication.loyaltycards.di

import android.app.Application
import androidx.room.Room
import com.rsschool.myapplication.loyaltycards.data.room.CardsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(app: Application) =
        Room.databaseBuilder(app, CardsDatabase::class.java, "cards_database")
            .fallbackToDestructiveMigration()
            .build()
}
