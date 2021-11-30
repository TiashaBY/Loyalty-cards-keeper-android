package com.rsschool.myapplication.loyaltycards.usecase

data class LoyaltyCardUseCases(
    val getCards: SearchForQueryUseCase,
    val deleteCard: DeleteCardUseCase,
    val addCard: AddCardUseCase,
    val getFavoriteCards: GetFavouritesListUseCase,
    val updateFavorites: UpdateFavoritesUseCase
)
