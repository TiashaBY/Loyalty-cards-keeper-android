package com.rsschool.myapplication.loyaltycards.domain.usecase

import android.net.Uri
import com.rsschool.myapplication.loyaltycards.domain.ImageRepository
import com.rsschool.myapplication.loyaltycards.domain.utils.ResultContainer
import java.lang.Exception
import javax.inject.Inject

class DeleteCardImagesUseCase @Inject constructor(private val imageRepo: ImageRepository) {

    suspend operator fun invoke(uri: Uri): ResultContainer<*> {
        return try {
            imageRepo.deleteFromRepository(uri)
            ResultContainer.Success(Unit)
        } catch (e: Exception) {
            ResultContainer.Failure(e)
        }
    }
}
