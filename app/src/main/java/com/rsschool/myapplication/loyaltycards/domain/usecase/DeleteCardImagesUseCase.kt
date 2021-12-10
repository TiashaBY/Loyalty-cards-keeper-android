package com.rsschool.myapplication.loyaltycards.domain.usecase

import android.app.Application
import android.net.Uri
import com.rsschool.myapplication.loyaltycards.domain.utils.ImageUtil
import com.rsschool.myapplication.loyaltycards.domain.utils.ResultContainer
import javax.inject.Inject

class DeleteCardImagesUseCase @Inject constructor(private val app: Application) {

    suspend operator fun invoke(uri: Uri): ResultContainer<*> {
        val imageUtil = ImageUtil(app)
        return if (imageUtil.deletePhotoFromInternalStorage(uri)) {
            ResultContainer.Success(Unit)
        } else {
            ResultContainer.Failure(Exception("An error occurred when deleting an image"))
        }
    }
}
