package com.rsschool.myapplication.loyaltycards.ui.util

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.google.zxing.BarcodeFormat
import com.rsschool.myapplication.loyaltycards.domain.model.Barcode
import com.rsschool.myapplication.loyaltycards.domain.utils.ResultContainer
import com.rsschool.myapplication.loyaltycards.ui.addcard.BarcodeListener

class BarcodeAnalyzer(private val barcodeListener: BarcodeListener) : ImageAnalysis.Analyzer {
    private val scanner = BarcodeScanning.getClient()

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    if (barcodes.isNotEmpty() && barcodes.size == 1) {
                        val barcode = barcodes[0]
                        Log.d("barcode found", barcode.displayValue ?: "")
                        barcodeListener(
                            ResultContainer.Success(
                                Barcode(
                                    barcode.displayValue,
                                    barcode.format.toZxingBarcode()
                                )
                            )
                        )
                    }
                    return@addOnSuccessListener
                }
                .addOnFailureListener {
                    barcodeListener(ResultContainer.Failure(it))
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }
}

private fun Int.toZxingBarcode(): BarcodeFormat? {
    return when (this) {
        1 -> BarcodeFormat.CODE_128
        2 -> BarcodeFormat.CODE_39
        4 -> BarcodeFormat.CODE_93
        8 -> BarcodeFormat.CODABAR
        16 -> BarcodeFormat.DATA_MATRIX
        32 -> BarcodeFormat.EAN_13
        64 -> BarcodeFormat.EAN_8
        128 -> BarcodeFormat.ITF
        256 -> BarcodeFormat.QR_CODE
        512 -> BarcodeFormat.UPC_A
        1024 -> BarcodeFormat.UPC_E
        2048 -> BarcodeFormat.PDF_417
        4096 -> BarcodeFormat.AZTEC
        else -> null
    }
}
