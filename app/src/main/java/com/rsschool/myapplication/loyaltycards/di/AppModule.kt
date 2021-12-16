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
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AppModule {
    @Provides
    @Singleton
    fun provideDatabase(app: Application) =
        Room.databaseBuilder(app, CardsDatabase::class.java, "cards_database")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideLoyaltyCardsDao(db: CardsDatabase) = db.getLoyaltyCardDao()

    @Provides
    @Singleton
    fun provideCardsRepo(dao: LoyaltyCardDao): CardsRepository = RoomCardsRepository(dao)

    @Provides
    @Singleton
    fun provideImagesRepo(app: Application): ImageRepository =
        LocalImageRepository(app)

    @Provides
    @Singleton
    fun provideLoyaltyCardsUseCases(
        repo: CardsRepository,
        imageRepo: ImageRepository
    ): LoyaltyCardUseCases {
        return LoyaltyCardUseCases(
            getCards = SearchForQueryUseCase(repo),
            deleteCard = DeleteCardUseCase(repo, imageRepo),
            addCard = AddCardUseCase(repo),
            getFavoriteCards = GetFavouritesListUseCase(repo),
            updateFavorites = UpdateFavoritesUseCase(repo)
        )
    }

    @Provides
    @Singleton
    fun provideTakePicUseCaseUseCases(imageRepo: ImageRepository): SaveCardImageUseCase {
        return SaveCardImageUseCase(imageRepo)
    }

    @Provides
    @Singleton
    fun provideSharedPrefs(app: Application): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(app)
}
