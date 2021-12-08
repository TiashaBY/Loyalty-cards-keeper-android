package com.rsschool.myapplication.loyaltycards.ui.viewmodel

import android.net.Uri
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

    val startArguments = state?.get<String>("mode")

    private val _cameraMode = MutableStateFlow<CameraEvents>(CameraEvents.CameraStopped)
    val cameraMode = _cameraMode.asStateFlow()

    init {
        _cameraMode.value = if (startArguments == "SCANNER") CameraEvents.OpenScanner
        else state?.get<CameraEvents>("cameraMode") ?: CameraEvents.CameraStopped
    }

    fun onBarcodeScanned(result: MyResult<*>) {
        when (result) {
            is MyResult.Success -> {
                val barcode = result.data as Barcode
                _cameraMode.value = CameraEvents.BarcodeScanned(barcode)

            }
            MyResult.Empty,
            is MyResult.Failure -> {
                onCardCaptureError()
                _cameraMode.value = CameraEvents.CameraStopped
            }
        }
    }

    //todo
    fun onCardCaptured(events: CameraEvents, image: ImageProxy) {
        viewModelScope.launch {
            val result = pictureUseCase(image)
            when (result) {
                is MyResult.Success<*> -> {
   /*                 if (events == CameraEvents.CAPTURE_IMAGE_FRONT) {
                       // sucessEvent for front
                        _cameraMode.value = CameraEvents.CAPTURE_IMAGE_BACK
                    } else {
                        // sucessEvent for front
                        _cameraMode.value = CameraEvents.NOT_ACTIVE
                    }*/
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

sealed class CameraEvents {
    object OpenScanner : CameraEvents()
    object CaptureFrontImage : CameraEvents()
    object CaptureBackImage : CameraEvents()
    object CameraStopped : CameraEvents()
    data class BarcodeScanned(val barcode : Barcode) : CameraEvents()
    data class CapturedImageSaved(val uri : Uri) : CameraEvents()
    data class CameraError(val msg : String)

    //SCANNER, CAPTURE_IMAGE_FRONT, CAPTURE_IMAGE_BACK, NOT_ACTIVE, DATA
}
