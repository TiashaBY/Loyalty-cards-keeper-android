package com.rsschool.myapplication.loyaltycards.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import com.rsschool.myapplication.loyaltycards.domain.ImageRepository
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*

private const val TAG = "ImageRepository"

class LocalImageRepository(private val context: Context) : ImageRepository {

    override suspend fun saveToRepository(bitmap: Bitmap): Uri {
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

    override suspend fun deleteFromRepository(uri: Uri): Boolean {
        return try {
            val filename = uri.toFile().name
            context.deleteFile(filename)
            Log.d(TAG, "File deleted: ${uri.path}")
            true
        } catch (ex: Exception) {
            when (ex) {
                is IllegalArgumentException -> {
                    Log.d(TAG, "File not deleted: ${uri.path}")
                    false
                }
                else -> throw ex
            }
        }
    }
}
