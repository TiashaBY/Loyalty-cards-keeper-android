package com.rsschool.myapplication.loyaltycards.model

import android.os.Parcelable
import com.google.zxing.BarcodeFormat
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Barcode(val code: String, val type: Int) : Parcelable {

    companion object {
        val barcodeFormats = HashMap<Int,Pair<BarcodeFormat?, String>>().apply {
            put(-1, null to "Unknown format")
            put(1, BarcodeFormat.CODE_128 to "Code 128 1D format")
            put(2, BarcodeFormat.CODE_39 to "Code 39 1D format")
            put(4, BarcodeFormat.CODE_93 to "Code 93 1D format")
            put(8, BarcodeFormat.CODABAR to "Codobar 1D format")
            put(16, BarcodeFormat.DATA_MATRIX to "Data Matrix 2D barcode format")
            put(32, BarcodeFormat.EAN_13 to "EAN-13 1D format")
            put(64, BarcodeFormat.EAN_8 to "EAN-8 1D format")
            put(128, BarcodeFormat.ITF to "ITF (Interleaved Two of Five) 1D format")
            put(256, BarcodeFormat.QR_CODE to "QR Code 2D barcode format")
            put(512, BarcodeFormat.UPC_A to "UPC-A 1D format")
            put(1024, BarcodeFormat.UPC_E to "UPC-E 1D format")
            put(2048, BarcodeFormat.PDF_417 to "PDF417 format")
            put(4096, BarcodeFormat.AZTEC to "Aztec 2D barcode format")
        }
    }
}

