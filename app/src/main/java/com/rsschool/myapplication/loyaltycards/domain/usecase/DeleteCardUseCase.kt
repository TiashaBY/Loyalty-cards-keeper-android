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
        try {
            repo.delete(card)
        } catch (dbException: java.lang.Exception) {
            return ResultContainer.Failure(dbException)
        }

        withContext(Dispatchers.IO) {
            try {
                imageRepo.deleteFromRepository(Uri.parse(card.frontImage))
                imageRepo.deleteFromRepository(Uri.parse(card.backImage))
            } catch (fileDeleteException: Exception) {
                return@withContext ResultContainer.Failure(fileDeleteException)
            }
        }
        return ResultContainer.Success(card.cardId)
    }
}
