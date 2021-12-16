package com.rsschool.myapplication.loyaltycards.domain.usecase

import android.net.Uri
import com.rsschool.myapplication.loyaltycards.domain.CardsRepository
import com.rsschool.myapplication.loyaltycards.domain.ImageRepository
import com.rsschool.myapplication.loyaltycards.domain.model.LoyaltyCard
import com.rsschool.myapplication.loyaltycards.domain.utils.ResultContainer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DeleteCardUseCase @Inject constructor(
    private val repo: CardsRepository,
    private val imageRepo: ImageRepository
) {
    suspend operator fun invoke(card: LoyaltyCard): ResultContainer<*> {
        var isSuccess = false
        if (repo.delete(card) > 0) {
            withContext(Dispatchers.IO) {
                isSuccess = imageRepo.deleteFromRepository(Uri.parse(card.frontImage)) &&
                        imageRepo.deleteFromRepository(Uri.parse(card.backImage))
            }
        }
        return if (isSuccess) {
            ResultContainer.Success(card.cardId)
        } else {
            ResultContainer.Failure(Exception("An error occurred when deleting a card ${card.cardName}"))
        }
    }
}
