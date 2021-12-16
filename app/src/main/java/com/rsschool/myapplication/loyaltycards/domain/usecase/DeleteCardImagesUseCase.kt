package com.rsschool.myapplication.loyaltycards.domain.usecase

import android.net.Uri
import com.rsschool.myapplication.loyaltycards.domain.ImageRepository
import com.rsschool.myapplication.loyaltycards.domain.utils.ResultContainer
import javax.inject.Inject

class DeleteCardImagesUseCase @Inject constructor(private val imageRepo: ImageRepository) {

    suspend operator fun invoke(uri: Uri): ResultContainer<*> {
        return if (imageRepo.deleteFromRepository(uri)) {
            ResultContainer.Success(Unit)
        } else {
            ResultContainer.Failure(Exception("An error occurred when deleting an image"))
        }
    }
}
