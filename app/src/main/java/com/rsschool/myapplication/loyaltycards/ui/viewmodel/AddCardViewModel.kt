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
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddCardViewModel @Inject constructor(
    private val state: SavedStateHandle?,
    private val useCase: AddCardUseCase
) : ViewModel() {

    private val _number = MutableStateFlow<String>("")
    val number = _number.asStateFlow()

    private val _barcodeFormat = MutableStateFlow<BarcodeFormat?>(null)
    val barcodeFormat = _barcodeFormat.asStateFlow()

    private val _frontImageUri = MutableStateFlow<Uri?>(Uri.EMPTY)
    val frontImageUri = _frontImageUri.asStateFlow()

    private val _backImageUri = MutableStateFlow<Uri?>(Uri.EMPTY)
    val backImageUri = _backImageUri.asStateFlow()

    private val _addCardEventsFlow = MutableSharedFlow<AddCardEvent?>()
    val addCardEventsFlow = _addCardEventsFlow.asSharedFlow()

    private var _imageBitmap = MutableStateFlow<Bitmap?>(null)
    val imageBitmap = _imageBitmap.asStateFlow()

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    fun handleSavedState() {
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

        when(state?.get<CameraResultEvent>("cameraResult")) {
            is CameraResultEvent.BarcodeScanned? -> {
                _number.value = state?.get<CameraResultEvent.BarcodeScanned?>("cameraResult")?.barcode?.code ?: ""
                _barcodeFormat.value = state?.get<CameraResultEvent.BarcodeScanned?>("cameraResult")?.barcode?.format
            }
            is CameraResultEvent.ImageSaved? -> {
                if (state?.get<CameraResultEvent.ImageSaved?>("cameraResult")?.type == CardImageType.FRONT) {
                    _frontImageUri.value =
                        state.get<CameraResultEvent.ImageSaved?>("cameraResult")?.imageUri
                } else {
                    _backImageUri.value =
                        state?.get<CameraResultEvent.ImageSaved?>("cameraResult")?.imageUri
                }
            }
        }
    }

    fun onCardNumberChange(newValue: String) {
        _number.value = newValue
        _imageBitmap.value = BarcodeGenerator()
            .generateBarcode(Barcode(number.value ?: "", barcodeFormat.value))
    }

    fun onFormatChange(newValue: BarcodeFormat?) {
        _barcodeFormat.value = newValue
        _imageBitmap.value = BarcodeGenerator()
            .generateBarcode(Barcode(number.value ?: "", newValue))
    }

    fun onNameChange(newValue: String) {
        _name.value = newValue
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
            _addCardEventsFlow.emit(AddCardEvent.NavigateToCameraScanBarcode)
        }
    }

    fun addCardFront() {
        viewModelScope.launch {
            _addCardEventsFlow.emit(AddCardEvent.NavigateToCameraTakeFrontImage)
        }
    }

    fun addCardBack() {
        viewModelScope.launch {
            _addCardEventsFlow.emit(AddCardEvent.NavigateToCameraTakeBackImage)
        }
    }
}

sealed class AddCardEvent {
    data class ShowInvalidInputMessage(val msg: String) : AddCardEvent()
    data class NavigateBackWithResult(val resultCode: String) : AddCardEvent()
    object NavigateToCameraScanBarcode : AddCardEvent()
    object NavigateToCameraTakeFrontImage : AddCardEvent()
    object NavigateToCameraTakeBackImage : AddCardEvent()
}