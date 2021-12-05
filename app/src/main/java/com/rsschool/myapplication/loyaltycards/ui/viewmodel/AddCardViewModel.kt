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

    private val _addCardEventsFlow = MutableSharedFlow<AddCardEvent?>()
    val addCardEventsFlow = _addCardEventsFlow.asSharedFlow()

    fun handleArgs() {
        //restore editable field
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

    fun onCardNumberChange(newValue: String) {
        _number.value = newValue
        _imageBitmap.value = BarcodeGenerator()
            .generateBarcode(Barcode(number.value ?: "", barcodeFormat.value))
    }

    fun onNameChange(newValue: String) {
        _name.value = newValue
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
            _addCardEventsFlow.emit(AddCardEvent.RequestImageEvent(CameraMode.SCANNER))
        }
    }

    fun addCardFront() {
        viewModelScope.launch {
            _addCardEventsFlow.emit(AddCardEvent.RequestImageEvent(CameraMode.CAPTURE_BACK))
        }
    }

    fun addCardBack() {
        viewModelScope.launch {
            _addCardEventsFlow.emit(AddCardEvent.RequestImageEvent(CameraMode.CAPTURE_BACK))
        }
    }
}

sealed class AddCardEvent {
    data class ShowInvalidInputMessage(val msg: String) : AddCardEvent()
    data class NavigateBackWithResult(val resultCode: String) : AddCardEvent()
    data class RequestImageEvent(val mode:CameraMode) : AddCardEvent()
    object CameraResultSuccess : AddCardEvent()
    data class CameraResultError(val message: String) : AddCardEvent()
}

/*sealed class CameraResultEvent : Serializable {
    data class BarcodeScanned(val barcode: Barcode) : CameraResultEvent()
    data class ImageSaved(val type: CameraMode, val imageUri: Uri?) : CameraResultEvent()
}

sealed class CameraActionsRequest : Serializable {
    object ScanBarcodeAction : CameraActionsRequest()
    data class CaptureImageAction(val type: CameraMode) : CameraActionsRequest()
}*/

enum class CameraMode {
    SCANNER, FRONT, CAPTURE_BACK
}
