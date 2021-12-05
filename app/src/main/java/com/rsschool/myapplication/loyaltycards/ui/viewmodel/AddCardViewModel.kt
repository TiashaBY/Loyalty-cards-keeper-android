package com.rsschool.myapplication.loyaltycards.ui.viewmodel

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.zxing.BarcodeFormat
import com.rsschool.myapplication.loyaltycards.domain.model.Barcode
import com.rsschool.myapplication.loyaltycards.domain.model.LoyaltyCard
import com.rsschool.myapplication.loyaltycards.domain.usecase.AddCardUseCase
import com.rsschool.myapplication.loyaltycards.domain.utils.BarcodeGenerator
import com.rsschool.myapplication.loyaltycards.domain.utils.Constants.ADD_TASK_RESULT_OK
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddCardViewModel @Inject constructor(
    private val state: SavedStateHandle?,
    private val useCase: AddCardUseCase
) : ViewModel() {

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _number = MutableStateFlow<String>("")
    val number = _number.asStateFlow()

    private val _barcodeFormat = MutableStateFlow<BarcodeFormat?>(null)
    val barcodeFormat = _barcodeFormat.asStateFlow()

    private var _imageBitmap = MutableStateFlow<Bitmap?>(null)
    val imageBitmap = _imageBitmap.asStateFlow()

    private val _frontImageUri = MutableStateFlow<Uri?>(Uri.EMPTY)
    val frontImageUri = _frontImageUri.asStateFlow()

    private val _backImageUri = MutableStateFlow<Uri?>(Uri.EMPTY)
    val backImageUri = _backImageUri.asStateFlow()

    private val _addCardEventsFlow = MutableSharedFlow<AddCardEvent>()
    val addCardEventsFlow = _addCardEventsFlow.asSharedFlow()

    private val _cameraMode = MutableStateFlow(CameraMode.NOT_ACTIVE)
    val cameraMode = _cameraMode.asStateFlow()

    init {
        state?.get<Uri>("frontImageUri")?.let {
            _frontImageUri.value = it
        }
        state?.get<Uri>("backImageUri")?.let {
            _backImageUri.value = it
        }
        state?.get<String>("name")?.let {
            _name.value = it
        }
        state?.get<String>("number")?.let {
            _number.value = it
        }
        state?.get<BarcodeFormat>("barcodeFormat")?.let {
            _barcodeFormat.value = it
        }
    }

    fun onNameChange(newValue: String) {
        _name.value = newValue
    }

    fun onCardNumberChange(newValue: String) {
        _number.value = newValue
        _imageBitmap.value = BarcodeGenerator()
            .generateBarcode(Barcode(number.value ?: "", barcodeFormat.value))
    }

    fun onBarcodeTypeChange(newValue: BarcodeFormat) {
        _barcodeFormat.value = newValue
        _imageBitmap.value = BarcodeGenerator()
            .generateBarcode(Barcode(number.value ?: "", newValue))
    }

    fun onSaveClick() {
        val card = LoyaltyCard(
            null, false,
            name.value,
            number.value!!,
            barcodeFormat.value,
            frontImageUri.value.toString(),
            backImageUri.value.toString()
        )
        viewModelScope.launch {
            val result = useCase(card)
            if (result > 0) {
                _addCardEventsFlow.emit(AddCardEvent.NavigateBackWithResult(ADD_TASK_RESULT_OK))
            } else {
                _addCardEventsFlow.emit(AddCardEvent.ShowInvalidInputMessage("Message"))
            }
        }
    }

    fun onScanBarcodeClick() {
        viewModelScope.launch {
            _addCardEventsFlow.emit(AddCardEvent.RequestImageEvent)
        }
        _cameraMode.value = CameraMode.SCANNER
    }

    fun onAddCardClick() {
        viewModelScope.launch {
            _addCardEventsFlow.emit(AddCardEvent.RequestImageEvent)
        }
        _cameraMode.value = CameraMode.CAPTURE_IMAGE_FRONT
    }

    fun onBarcodeScanned(barcode: Barcode) {
        _number.value = barcode?.code
        _barcodeFormat.value = barcode?.format
        _cameraMode.value = CameraMode.NOT_ACTIVE
    }

    fun onCardCaptured(mode: CameraMode, uri : Uri) {
        if (mode == CameraMode.CAPTURE_IMAGE_FRONT) {
            _frontImageUri.value = uri
            _cameraMode.value = CameraMode.CAPTURE_IMAGE_BACK
        } else {
            _backImageUri.value = uri
            _cameraMode.value = CameraMode.NOT_ACTIVE
        }
    }
}

sealed class AddCardEvent {
    data class ShowInvalidInputMessage(val msg: String) : AddCardEvent()
    data class NavigateBackWithResult(val resultCode: String) : AddCardEvent()
    object RequestImageEvent : AddCardEvent()
}

sealed class Destination {
    object Camera : Destination()
    object AddCardForm : Destination()
    object Dashboard : Destination()

}

sealed class CameraResultEvent : java.io.Serializable {
    data class BarcodeScanned(val barcode: Barcode) : CameraResultEvent()
    data class ImageSaved(val type: CameraMode, val imageUri: Uri?) : CameraResultEvent()
}

sealed class CameraActionsRequest : java.io.Serializable {
    object ScanBarcodeAction : CameraActionsRequest()
    data class CaptureImageAction(val type: CameraMode) : CameraActionsRequest()
}

enum class CameraMode {
    SCANNER, CAPTURE_IMAGE_FRONT, CAPTURE_IMAGE_BACK, NOT_ACTIVE
}
