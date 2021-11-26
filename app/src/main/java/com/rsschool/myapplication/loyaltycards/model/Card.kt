package com.rsschool.myapplication.loyaltycards.model

data class Card(
    val userId : String,
    val cardName: String,
    val cardNumber : String,
    val barcodeType: String,
    val frontImage : String,
    val backImage : String
)
