package com.rsschool.myapplication.loyaltycards.domain.usecase

import android.app.Application
import android.net.Uri
import com.rsschool.myapplication.loyaltycards.domain.utils.ImageUtil
import com.rsschool.myapplication.loyaltycards.domain.utils.MyResult
import javax.inject.Inject

class DeleteCardPictureUseCase @Inject constructor(private val app: Application) {

    suspend operator fun invoke(uri: Uri): MyResult<*> {
        val imageUtil = ImageUtil(app)
        return if (imageUtil.deletePhotoFromInternalStorage(uri)){
            MyResult.Success(Unit)
    } else {
            MyResult.Failure(Exception("An error occurred when deleting an image"))
        }
    }
}
