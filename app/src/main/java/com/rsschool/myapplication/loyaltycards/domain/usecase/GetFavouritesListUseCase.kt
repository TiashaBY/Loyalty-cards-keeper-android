package com.rsschool.myapplication.loyaltycards.domain.usecase

import com.rsschool.myapplication.loyaltycards.domain.CardsRepository
import com.rsschool.myapplication.loyaltycards.domain.model.LoyaltyCard
import kotlinx.coroutines.flow.Flow

class GetFavouritesListUseCase constructor(private val repo: CardsRepository) {
    operator fun invoke(): Flow<List<LoyaltyCard>> {
        try {
            return repo.getFavouritesCarts()
        } catch (ex : Exception) {
            throw ex
        }
    }
}
