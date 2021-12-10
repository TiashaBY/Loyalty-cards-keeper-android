package com.rsschool.myapplication.loyaltycards.domain.utils

sealed class ResultContainer<out T> {
    data class Success<T>(val data: T) : ResultContainer<T>()
    data class Failure(val exception: Throwable) : ResultContainer<Nothing>()
}
