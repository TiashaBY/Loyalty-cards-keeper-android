package com.rsschool.myapplication.loyaltycards.usecase

import com.rsschool.myapplication.loyaltycards.datasource.repository.CardsRepository

class GetFavouritesListUseCase constructor(private val repo: CardsRepository) {
    operator fun invoke() = repo.getFavouritesCarts()
}