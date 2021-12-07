package com.rsschool.myapplication.loyaltycards.domain.usecase

import android.app.Application
import androidx.camera.core.ImageProxy
import com.rsschool.myapplication.loyaltycards.domain.utils.ImageUtil
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.baseviewmodel.MyResult
import javax.inject.Inject

class TakeCardPictureUseCase @Inject constructor(private val app: Application) {

    operator fun invoke(image: ImageProxy): MyResult<*> {
        val imageUtil = ImageUtil(app)
        val bitmap = imageUtil.cropImage(image)
        return imageUtil.savePhotoToInternalStorage(bitmap)
    }
}