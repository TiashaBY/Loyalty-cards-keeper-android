package com.rsschool.myapplication.loyaltycards.domain.usecase

import com.rsschool.myapplication.loyaltycards.domain.CardsRepository
import com.rsschool.myapplication.loyaltycards.domain.model.LoyaltyCard
import javax.inject.Inject

class DeleteCardUseCase @Inject constructor(private val repo: CardsRepository) {
    suspend operator fun invoke(card: LoyaltyCard) = repo.delete(card)
}