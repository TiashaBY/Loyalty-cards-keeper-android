package com.rsschool.myapplication.loyaltycards.di

import android.content.Context
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
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped

@InstallIn(ViewModelComponent::class)
@Module
class AppModule {
    @Provides
    @ViewModelScoped
    fun provideLoyaltyCardsDao(db: CardsDatabase) = db.getLoyaltyCardDao()

    @Provides
    @ViewModelScoped
    fun provideCardsRepo(dao: LoyaltyCardDao): CardsRepository = RoomCardsRepository(dao)

    @Provides
    @ViewModelScoped
    fun provideImagesRepo(@ApplicationContext cntx: Context): ImageRepository =
        LocalImageRepository(cntx)

    @Provides
    @ViewModelScoped
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
    @ViewModelScoped
    fun provideTakePicUseCaseUseCases(imageRepo: ImageRepository): SaveCardImageUseCase {
        return SaveCardImageUseCase(imageRepo)
    }
}
