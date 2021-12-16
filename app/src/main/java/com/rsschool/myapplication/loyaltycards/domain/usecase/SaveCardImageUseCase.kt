package com.rsschool.myapplication.loyaltycards.domain.usecase

import androidx.camera.core.ImageProxy
import com.rsschool.myapplication.loyaltycards.domain.ImageRepository
import com.rsschool.myapplication.loyaltycards.domain.utils.CropUtil
import com.rsschool.myapplication.loyaltycards.domain.utils.ResultContainer
import javax.inject.Inject

class SaveCardImageUseCase @Inject constructor(
    private val imageRepo: ImageRepository
) {
    suspend operator fun invoke(image: ImageProxy): ResultContainer<*> {
        val bitmap = CropUtil().cropImage(image)
        return try {
            val photoFile = imageRepo.saveToRepository(bitmap)
            ResultContainer.Success(photoFile)
        } catch (e: Exception) {
            ResultContainer.Failure(e)
        }
    }
}
