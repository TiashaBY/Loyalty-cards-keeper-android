package com.rsschool.myapplication.loyaltycards.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "CardsTable")
@Parcelize
data class LoyaltyCard(
    @PrimaryKey()
    val cardId : String,
    val userId : String,
    val cardName: String,
    val cardNumber : String,
    val barcodeType: Int,
    val frontImage : String,
    val backImage : String
)
