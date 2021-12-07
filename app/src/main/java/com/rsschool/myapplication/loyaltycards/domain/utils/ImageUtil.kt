package com.rsschool.myapplication.loyaltycards.domain.utils

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.camera.core.ImageProxy
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.baseviewmodel.MyResult
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*

class ImageUtil(private val context: Context) {

    fun cropImage(image: ImageProxy): Bitmap {
        val buffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.capacity()).also { buffer.get(it) }
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

        val widthReal = bitmap.width
        val heightReal = bitmap.height

        var croppedWidth = 0F
        var croppedHeight = 0F
        val standardCardHeight = 54
        val standardCardWidth = 86
        if (heightReal > widthReal) {
            croppedWidth = widthReal * 0.9F
            croppedHeight = ((widthReal * standardCardHeight) / standardCardWidth).toFloat()
        } else {
            croppedHeight = heightReal * 0.9F
            croppedWidth = (croppedHeight * standardCardWidth) / standardCardHeight
            if (croppedWidth > widthReal) croppedWidth = widthReal.toFloat()
        }
        var topLeftX = (widthReal - croppedWidth) / 2
        val topLeftY = (heightReal - croppedHeight) / 2

        return Bitmap.createBitmap(
            bitmap,
            topLeftX.toInt(), topLeftY.toInt(), croppedWidth.toInt(), croppedHeight.toInt()
        )
    }

    fun savePhotoToInternalStorage(bitmap: Bitmap): MyResult<*> {
        val photoFile = File(context.filesDir, UUID.randomUUID().toString() + ".jpg")
        val fileStream = photoFile.outputStream()

        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val byteArrayStream = stream.toByteArray()

        try {
            byteArrayStream.let { stream.write(it, 0, it.size) }
        } catch (e: Exception) {
            return MyResult.Failure(e)
        } finally {
            fileStream.flush()
            fileStream.close()
        }
        return MyResult.Success(Uri.fromFile(photoFile))
    }
}
