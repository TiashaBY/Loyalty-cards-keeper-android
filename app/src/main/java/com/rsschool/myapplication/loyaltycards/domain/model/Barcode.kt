package com.rsschool.myapplication.loyaltycards.domain.model

import android.os.Parcelable
import com.google.zxing.BarcodeFormat
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Barcode(var number: String, val format: BarcodeFormat?) : Parcelable
