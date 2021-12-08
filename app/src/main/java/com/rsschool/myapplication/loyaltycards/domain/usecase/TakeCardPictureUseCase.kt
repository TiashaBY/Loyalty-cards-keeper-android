package com.rsschool.myapplication.loyaltycards.domain.usecase

import android.app.Application

import androidx.camera.core.ImageProxy
import com.rsschool.myapplication.loyaltycards.domain.utils.ImageUtil
import com.rsschool.myapplication.loyaltycards.domain.utils.MyResult
import javax.inject.Inject

class TakeCardPictureUseCase @Inject constructor(private val app: Application) {

    suspend operator fun invoke(image: ImageProxy): MyResult<*> {
        val imageUtil = ImageUtil(app)
        val bitmap = imageUtil.cropImage(image)
        return try {
            val photoFile = imageUtil.savePhotoToInternalStorage(bitmap)
            MyResult.Success(photoFile)
        } catch (e: Exception) {
            MyResult.Failure(e)
        }
    }
}
