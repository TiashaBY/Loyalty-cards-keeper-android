package com.rsschool.myapplication.loyaltycards.ui

object UiConst {
    const val PHOTO_RESULT = "PHOTO_RESULT"
    const val SCANNER_RESULT = "SCANNER_RESULT"
    const val RESULT_OK: String = "RESULT_OK"
}

enum class CardSide {
    FRONT, BACK
}

enum class CameraMode {
    SCANNER, PHOTO
}

val <T> T.exhaustive: T
    get() = this