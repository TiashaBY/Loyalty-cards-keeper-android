package com.rsschool.myapplication.loyaltycards.domain.usecase

import com.rsschool.myapplication.loyaltycards.domain.CardsRepository

class GetFavouritesListUseCase constructor(private val repo: CardsRepository) {
    operator fun invoke() = repo.getFavouritesCarts()
}