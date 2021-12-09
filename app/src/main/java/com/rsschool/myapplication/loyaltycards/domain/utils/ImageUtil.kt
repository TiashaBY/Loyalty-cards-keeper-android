package com.rsschool.myapplication.loyaltycards.domain.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.camera.core.ImageProxy
import androidx.core.net.toFile
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*

private const val TAG = "FileUtil"

class ImageUtil(private val context: Context) {

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
            bitmap,
            topLeftX.toInt(), topLeftY.toInt(), croppedWidth.toInt(), croppedHeight.toInt()
        )
    }

    suspend fun savePhotoToInternalStorage(bitmap: Bitmap): Uri {
        val photoFile = File(context.filesDir, UUID.randomUUID().toString() + ".jpg")
        val fileStream = context.openFileOutput(photoFile.name, Context.MODE_PRIVATE)

        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val byteArrayStream = stream.toByteArray()

        return try {
            byteArrayStream.let { fileStream.write(it, 0, it.size) }
            val uri = Uri.fromFile(photoFile)
            Log.d(TAG, "File saved: ${uri.path}")
            uri
        } catch (e: Exception) {
            throw e
        } finally {
            fileStream.flush()
            fileStream.close()
        }
    }

    suspend fun deletePhotoFromInternalStorage(uri: Uri): Boolean {
        val filename = uri.toFile().name
        return try {
            context.deleteFile(filename)
            Log.d(TAG, "File deleted: ${uri.path}")
            true
        } catch (e: Exception) {
            Log.d(TAG, "File not deleted: ${uri.path}")
            e.printStackTrace()
            false
        }
    }
}
