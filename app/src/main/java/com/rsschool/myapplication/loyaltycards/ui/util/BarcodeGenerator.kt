package com.rsschool.myapplication.loyaltycards.ui.util

import android.graphics.Bitmap
import android.util.Log
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.rsschool.myapplication.loyaltycards.domain.model.Barcode


const val WIDTH = 1028
const val HEIGHT = 512
const val WHITE = -0x1
const val BLACK = -0x1000000

class BarcodeGenerator {

    fun generateBarcode(barcode: Barcode): Bitmap? {
        val writer = MultiFormatWriter()
        val bitMatrix: BitMatrix
        try {
            bitMatrix = try {
                writer.encode(barcode.number, barcode.format, WIDTH, HEIGHT, null)
            } catch (e: Exception) {
                // Cast a wider net here and catch any exception, as there are some
                // cases where an encoder may fail if the data is invalid for the
                // barcode type. If this happens, we want to fail gracefully.
                throw WriterException(e)
            }
            val bitMatrixWidth = bitMatrix.width
            val bitMatrixHeight = bitMatrix.height
            val pixels = IntArray(bitMatrixWidth * bitMatrixHeight)
            for (y in 0 until bitMatrixHeight) {
                val offset = y * bitMatrixWidth
                for (x in 0 until bitMatrixWidth) {
                    val color = if (bitMatrix[x, y]) BLACK else WHITE
                    pixels[offset + x] = color
                }
            }
            val bitmap = Bitmap.createBitmap(
                bitMatrixWidth, bitMatrixHeight,
                Bitmap.Config.ARGB_8888
            )
            bitmap.setPixels(pixels, 0, bitMatrixWidth, 0, 0, bitMatrixWidth, bitMatrixHeight)
            return bitmap
        } catch (e: WriterException) {
            Log.d("Barcode generator", e.message.toString())
            throw e
        } catch (e: OutOfMemoryError) {
            Log.d("Barcode generator", e.message.toString())
            throw e
        }
    }
}
