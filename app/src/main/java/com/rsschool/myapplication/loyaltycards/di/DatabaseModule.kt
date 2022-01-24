package com.rsschool.myapplication.loyaltycards.di

import android.app.Application
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.rsschool.myapplication.loyaltycards.data.repository.LocalImageRepository
import com.rsschool.myapplication.loyaltycards.data.repository.RoomCardsRepository
import com.rsschool.myapplication.loyaltycards.data.room.CardsDatabase
import com.rsschool.myapplication.loyaltycards.data.room.LoyaltyCardDao
import com.rsschool.myapplication.loyaltycards.domain.CardsRepository
import com.rsschool.myapplication.loyaltycards.domain.ImageRepository
import com.rsschool.myapplication.loyaltycards.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.android.scopes.FragmentScoped
import dagger.hilt.android.scopes.ViewModelScoped
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
