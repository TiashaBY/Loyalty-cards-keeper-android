package com.rsschool.myapplication.loyaltycards.ui.viewmodel

import android.net.Uri
import androidx.camera.core.ImageProxy
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rsschool.myapplication.loyaltycards.domain.model.Barcode
import com.rsschool.myapplication.loyaltycards.domain.usecase.TakeCardPictureUseCase
import com.rsschool.myapplication.loyaltycards.domain.utils.ResultContainer
import com.rsschool.myapplication.loyaltycards.ui.CameraMode
import com.rsschool.myapplication.loyaltycards.ui.CardSide
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val CAMERA_MODE = "cameraMode"
private const val FRONT_IMAGE_URI = "frontImageUri"
private const val BACK_IMAGE_URI = "backImageUri"

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val state: SavedStateHandle?,
    private val pictureUseCase: TakeCardPictureUseCase,
) : ViewModel() {

    private val startArguments = state?.get<CameraMode>("mode")

    private val _cameraEvent = MutableStateFlow<CameraEvents>(CameraEvents.CameraFinishedCapturing)
    val cameraEvent = _cameraEvent.asStateFlow()

    var frontImageUri: Uri? = Uri.EMPTY
    var backImageUri: Uri? = Uri.EMPTY

    init {
        _cameraEvent.value =
            state?.get<CameraEvents>(CAMERA_MODE) ?: if (startArguments == CameraMode.SCANNER) {
                CameraEvents.OpenScanner
            } else {
                CameraEvents.CaptureFrontImage
            }

        state?.get<Uri>(FRONT_IMAGE_URI)?.let {
            frontImageUri = it
        }
        state?.get<Uri>(BACK_IMAGE_URI)?.let {
            backImageUri = it
        }
    }

    fun onBarcodeScanned(result: ResultContainer<*>) {
        when (result) {
            is ResultContainer.Success -> {
                val barcode = result.data as Barcode
                _cameraEvent.value = CameraEvents.BarcodeScanned(barcode)

            }
            is ResultContainer.Failure -> {
                onErrorEvent("An error on attempt to save card image")
            }
        }
    }

    fun onCardCaptured(side: CardSide, image: ImageProxy) {
        viewModelScope.launch {
            when (val res = pictureUseCase(image)) {
                is ResultContainer.Success<*> -> {
                    if (side == CardSide.FRONT) {
                        frontImageUri = res.data as Uri
                        _cameraEvent.value = CameraEvents.CaptureBackImage
                    } else {
                        backImageUri = res.data as Uri
                        _cameraEvent.value = CameraEvents.CameraFinishedCapturing
                    }
                }
                is ResultContainer.Failure -> {
                    onErrorEvent(res.exception.message.toString())
                }
            }
        }
    }

    fun onErrorEvent(msg: String) {
        _cameraEvent.value = CameraEvents.CameraError("Error during image capturing $msg")
    }
}

sealed class CameraEvents {
    object OpenScanner : CameraEvents()
    object CaptureFrontImage : CameraEvents()
    object CaptureBackImage : CameraEvents()
    object CameraFinishedCapturing : CameraEvents()
    data class BarcodeScanned(val barcode : Barcode) : CameraEvents()
    data class CameraError(val msg : String) : CameraEvents()
}
