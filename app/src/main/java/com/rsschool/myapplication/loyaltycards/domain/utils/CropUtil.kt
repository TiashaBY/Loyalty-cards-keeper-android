package com.rsschool.myapplication.loyaltycards.domain.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.camera.core.ImageProxy

class CropUtil {

    suspend fun cropImage(image: ImageProxy): Bitmap {
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
        val topLeftX = (widthReal - croppedWidth) / 2
        val topLeftY = (heightReal - croppedHeight) / 2

        return Bitmap.createBitmap(
            bitmap, topLeftX.toInt(), topLeftY.toInt(), croppedWidth.toInt(), croppedHeight.toInt()
        )
    }
}
