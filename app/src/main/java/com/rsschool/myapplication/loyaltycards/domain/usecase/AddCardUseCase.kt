package com.rsschool.myapplication.loyaltycards.domain.usecase

import com.rsschool.myapplication.loyaltycards.domain.CardsRepository
import com.rsschool.myapplication.loyaltycards.domain.model.LoyaltyCard
import com.rsschool.myapplication.loyaltycards.domain.utils.ResultContainer
import javax.inject.Inject

class AddCardUseCase @Inject constructor(private val repo: CardsRepository) {

    suspend operator fun invoke(card: LoyaltyCard): ResultContainer<*> {
        val result = repo.insert(card)
        return if (result > 0) {
            ResultContainer.Success(result)
        } else {
            ResultContainer.Failure(Exception("An error occurred when inserting a card ${card.cardName}"))
        }
    }
}
