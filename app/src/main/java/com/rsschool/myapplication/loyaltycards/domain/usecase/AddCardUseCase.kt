package com.rsschool.myapplication.loyaltycards.domain.usecase

import android.util.Log
import com.rsschool.myapplication.loyaltycards.domain.CardsRepository
import com.rsschool.myapplication.loyaltycards.domain.model.LoyaltyCard
import com.rsschool.myapplication.loyaltycards.domain.utils.ResultContainer
import javax.inject.Inject

private const val TAG = "AddCard"

class AddCardUseCase @Inject constructor(private val repo: CardsRepository) {

    suspend operator fun invoke(card: LoyaltyCard): ResultContainer<*> {
        return try {
            val result = repo.insert(card)
            ResultContainer.Success(result)
        } catch (e: Exception) {
            Log.d(TAG, "An error occurred when inserting a card ${card.cardName}")
            ResultContainer.Failure(e)
        }
    }
}
