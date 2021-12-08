package com.rsschool.myapplication.loyaltycards.domain.utils

import android.graphics.Bitmap
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.rsschool.myapplication.loyaltycards.domain.model.Barcode
import java.lang.Exception

class BarcodeGenerator {

    fun generateBarcode(barcode: Barcode): Bitmap? {
        var writer = MultiFormatWriter()
        var bitMatrix: BitMatrix
        try {
            bitMatrix = try {
                writer.encode(barcode.number, barcode.format, 200, 150, null)
            } catch (e: Exception) {
                // Cast a wider net here and catch any exception, as there are some
                // cases where an encoder may fail if the data is invalid for the
                // barcode type. If this happens, we want to fail gracefully.
                throw WriterException(e)
            }
            val WHITE = -0x1
            val BLACK = -0x1000000
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
            var bitmap = Bitmap.createBitmap(
                bitMatrixWidth, bitMatrixHeight,
                Bitmap.Config.ARGB_8888
            )
            bitmap.setPixels(pixels, 0, bitMatrixWidth, 0, 0, bitMatrixWidth, bitMatrixHeight)

            // Determine if the image needs to be scaled.
            // This is necessary because the datamatrix barcode generator
            // ignores the requested size and returns the smallest image necessary
            // to represent the barcode. If we let the ImageView scale the image
            // it will use bi-linear filtering, which results in a blurry barcode.
            // To avoid this, if scaling is needed do so without filtering.
            val heightScale: Int = 512 / bitMatrixHeight
            val widthScale: Int = 1500 / bitMatrixHeight
            val scalingFactor = Math.min(heightScale, widthScale)
            if (scalingFactor > 1) {
                bitmap = Bitmap.createScaledBitmap(
                    bitmap,
                    bitMatrixWidth * scalingFactor,
                    bitMatrixHeight * scalingFactor,
                    false
                )
            }
            return bitmap
        } catch (e: WriterException) {
            //
        } catch (e: java.lang.OutOfMemoryError) {
            //

        }

        return null
    }
}