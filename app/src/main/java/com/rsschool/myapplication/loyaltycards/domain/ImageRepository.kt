package com.rsschool.myapplication.loyaltycards.domain

import android.graphics.Bitmap
import android.net.Uri

interface ImageRepository {
    suspend fun saveToRepository(bitmap: Bitmap): Uri
    suspend fun deleteFromRepository(uri: Uri): Boolean
}
