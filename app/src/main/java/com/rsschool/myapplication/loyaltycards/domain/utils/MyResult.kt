package com.rsschool.myapplication.loyaltycards.domain.utils

sealed class MyResult<out T> {
    data class Success<T>(val data: T) : MyResult<T>()
    data class Failure(val exception: Throwable) : MyResult<Nothing>()
}
