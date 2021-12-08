package com.rsschool.myapplication.loyaltycards.ui.viewmodel

import androidx.camera.core.ImageProxy
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rsschool.myapplication.loyaltycards.domain.model.Barcode
import com.rsschool.myapplication.loyaltycards.domain.usecase.TakeCardPictureUseCase
import com.rsschool.myapplication.loyaltycards.domain.utils.MyResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val state: SavedStateHandle?,
    private val pictureUseCase: TakeCardPictureUseCase,
) : ViewModel() {

    val startArguments = state?.get<CameraMode?>("mode")

    private val _cameraMode = MutableStateFlow(CameraMode.NOT_ACTIVE)
    val cameraMode = _cameraMode.asStateFlow()

    init {
        _cameraMode.value = state?.get<CameraMode?>("cameraMode") ?: startArguments ?: CameraMode.NOT_ACTIVE
    }

    fun onBarcodeScanned(result: MyResult<*>) {
        when (result) {
            is MyResult.Success -> {
                val barcode = result.data as Barcode
                _cameraMode.value = CameraMode.DATA

            }
            MyResult.Empty,
            is MyResult.Failure -> {
                onCardCaptureError()
                //error event
            }
        }
        _cameraMode.value = CameraMode.NOT_ACTIVE
    }

    //todo
    fun onCardCaptured(mode: CameraMode, image: ImageProxy) {
        viewModelScope.launch {
            val result = pictureUseCase(image)
            when (result) {
                is MyResult.Success<*> -> {
                    if (mode == CameraMode.CAPTURE_IMAGE_FRONT) {
                       // sucessEvent for front
                        _cameraMode.value = CameraMode.CAPTURE_IMAGE_BACK
                    } else {
                        // sucessEvent for front
                        _cameraMode.value = CameraMode.NOT_ACTIVE
                    }
                }
                is MyResult.Failure -> {
                   //error event on save
                }
            }
        }
    }

    //TOdo
    fun onCardCaptureError() {
        viewModelScope.launch {
            //error event on capture
        }
    }
}

enum class CameraMode {
    SCANNER, CAPTURE_IMAGE_FRONT, CAPTURE_IMAGE_BACK, NOT_ACTIVE, DATA
}
