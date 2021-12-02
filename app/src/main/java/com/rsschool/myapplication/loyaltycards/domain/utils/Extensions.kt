package com.rsschool.myapplication.loyaltycards.domain.utils

import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

// For more details, check: https://gist.github.com/marcellogalhardo/2a1ec56b7d00ba9af1ec9fd3583d53dc
fun <T> SavedStateHandle.getStateFlow(
    scope: CoroutineScope,
    key: String,
    initialValue: T
): MutableStateFlow<T> {
    val liveData = getLiveData(key, initialValue)
    val stateFlow = MutableStateFlow(initialValue)

    val observer = Observer<T> { value ->
        if (value != stateFlow.value) {
            stateFlow.value = value
        }
    }
    liveData.observeForever(observer)

    stateFlow.onCompletion {
        withContext(Dispatchers.Main.immediate) {
            liveData.removeObserver(observer)
        }
    }.onEach { value ->
        withContext(Dispatchers.Main.immediate) {
            if (liveData.value != value) {
                liveData.value = value
            }
        }
    }.launchIn(scope)

    return stateFlow
}