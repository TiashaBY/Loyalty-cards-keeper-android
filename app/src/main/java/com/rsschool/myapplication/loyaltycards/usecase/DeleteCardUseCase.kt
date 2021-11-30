package com.rsschool.myapplication.loyaltycards.usecase

import com.rsschool.myapplication.loyaltycards.datasource.repository.CardsRepository
import com.rsschool.myapplication.loyaltycards.model.LoyaltyCard
import javax.inject.Inject

class DeleteCardUseCase @Inject constructor(private val repo: CardsRepository) {
    suspend operator fun invoke(card: LoyaltyCard) = repo.delete(card)
}