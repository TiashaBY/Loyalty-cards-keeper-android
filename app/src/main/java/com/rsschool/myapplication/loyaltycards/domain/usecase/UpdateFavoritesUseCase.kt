package com.rsschool.myapplication.loyaltycards.domain.usecase

import android.util.Log
import com.rsschool.myapplication.loyaltycards.domain.CardsRepository
import com.rsschool.myapplication.loyaltycards.domain.model.LoyaltyCard
import com.rsschool.myapplication.loyaltycards.domain.utils.ResultContainer
import javax.inject.Inject

private const val TAG = "AddCard"

class UpdateFavoritesUseCase @Inject constructor(private val repo: CardsRepository) {
    suspend operator fun invoke(card: LoyaltyCard, isChecked: Boolean): ResultContainer<*> {
        val updatedCard = card.copy(isFavourite = isChecked)
        return try {
            val result = repo.update(updatedCard)
            ResultContainer.Success(result)
        } catch (e: Exception) {
            Log.d(TAG, "An error occurred when updating a card ${card.cardName}")
            ResultContainer.Failure(e)
        }
    }
}
