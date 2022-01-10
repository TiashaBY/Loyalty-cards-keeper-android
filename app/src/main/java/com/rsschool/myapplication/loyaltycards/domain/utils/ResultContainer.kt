package com.rsschool.myapplication.loyaltycards.domain.utils

sealed class ResultContainer<out T> {
    class Success<T>(val data: T) : ResultContainer<T>()
    class Failure(val exception: Throwable) : ResultContainer<Nothing>()
}
