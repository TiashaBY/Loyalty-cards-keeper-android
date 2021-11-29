package com.rsschool.myapplication.loyaltycards.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.zxing.BarcodeFormat
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "CardsTable")
@Parcelize
data class LoyaltyCard(
    @PrimaryKey
    val cardId: Long? = null,
    val userId: String,
    val isFavourite: Boolean,
    val cardName: String,
    val cardNumber: String,
    val barcodeType: BarcodeFormat?,
    val frontImage: String = "",
    val backImage: String = ""
) : Parcelable
