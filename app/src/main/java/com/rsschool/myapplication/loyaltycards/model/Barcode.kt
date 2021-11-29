package com.rsschool.myapplication.loyaltycards.model

import android.os.Parcelable
import com.google.zxing.BarcodeFormat
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Barcode(var code: String, val format: BarcodeFormat?) : Parcelable


