package com.rsschool.myapplication.loyaltycards.ui

object UiConst {
    const val PHOTO_RESULT = "PHOTO_RESULT"
    const val SCANNER_RESULT = "SCANNER_RESULT"
}

enum class CardSide {
    FRONT, BACK
}

val <T> T.exhaustive: T
    get()=this