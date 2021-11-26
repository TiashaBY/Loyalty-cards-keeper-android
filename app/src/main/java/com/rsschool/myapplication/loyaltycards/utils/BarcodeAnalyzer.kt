package com.rsschool.myapplication.loyaltycards.utils

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.core.os.bundleOf
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.rsschool.myapplication.loyaltycards.BarcodeListener

class BarcodeAnalyzer(private val barcodeListener: BarcodeListener) : ImageAnalysis.Analyzer {
    // Get an instance of BarcodeScanner
    private val scanner = BarcodeScanning.getClient()

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            // Pass image to the scanner and have it do its thing
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    if (barcodes.isNotEmpty()) {
                        val barcode = barcodes[0]
                        Log.d("barcode found", barcode.displayValue ?: "")
                        barcodeListener(bundleOf("CODE" to barcode.displayValue, "TYPE" to barcode.format))
                    }
                }
                .addOnFailureListener {
                    // You should really do something about Exceptions
                }
                .addOnCompleteListener {
                    // It's important to close the imageProxy
                    imageProxy.close()
                }
        }
    }
}