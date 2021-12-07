package com.rsschool.myapplication.loyaltycards.domain.usecase

import android.app.Application
import android.net.Uri
import com.rsschool.myapplication.loyaltycards.domain.CardsRepository
import com.rsschool.myapplication.loyaltycards.domain.model.LoyaltyCard
import com.rsschool.myapplication.loyaltycards.domain.utils.ImageUtil
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.baseviewmodel.MyResult
import javax.inject.Inject

class DeleteCardUseCase @Inject constructor(private val repo: CardsRepository, private val app: Application) {
    suspend operator fun invoke(card: LoyaltyCard): MyResult<*> {
        val imageUtil = ImageUtil(app)
        var isSuccess = false
        if (repo.delete(card) > 0) {
            isSuccess = imageUtil.deletePhotoFromInternalStorage(Uri.parse(card.backImage)) &&
                    imageUtil.deletePhotoFromInternalStorage(Uri.parse(card.backImage))
        }
        return if (isSuccess) {
            MyResult.Success(card.cardId)
        } else {
            MyResult.Failure(Exception("An error occurred when deleting card ${card.cardName}"))
        }
    }
}
