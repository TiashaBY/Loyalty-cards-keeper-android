package com.rsschool.myapplication.loyaltycards.ui.viewmodel

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.camera.core.ImageProxy
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.zxing.BarcodeFormat
import com.rsschool.myapplication.loyaltycards.domain.model.Barcode
import com.rsschool.myapplication.loyaltycards.domain.model.LoyaltyCard
import com.rsschool.myapplication.loyaltycards.domain.usecase.AddCardUseCase
import com.rsschool.myapplication.loyaltycards.domain.usecase.DeleteCardPictureUseCase
import com.rsschool.myapplication.loyaltycards.domain.usecase.TakeCardPictureUseCase
import com.rsschool.myapplication.loyaltycards.domain.utils.BarcodeGenerator
import com.rsschool.myapplication.loyaltycards.domain.utils.Constants.RESULT_OK
import com.rsschool.myapplication.loyaltycards.domain.utils.MyResult
import com.rsschool.myapplication.loyaltycards.ui.AddCardFragmentArgs
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddCardViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val cardUseCase: AddCardUseCase,
    private val deleteUseCase: DeleteCardPictureUseCase
) : ViewModel() {

    val args : AddCardFragmentArgs = AddCardFragmentArgs.fromSavedStateHandle(state)

    private val resultArguments = state?.get<Barcode>("result")

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _number = MutableStateFlow<String>("")
    val number = _number.asStateFlow()

    private val _barcodeFormat = MutableStateFlow<BarcodeFormat?>(null)
    val barcodeFormat = _barcodeFormat.asStateFlow()

    private var _imageBitmap = MutableStateFlow<Bitmap?>(null)
    val imageBitmap = _imageBitmap.asStateFlow()

    private val _frontImageUri = MutableStateFlow<Uri>(Uri.EMPTY)
    val frontImageUri = _frontImageUri.asStateFlow()

    private val _backImageUri = MutableStateFlow<Uri>(Uri.EMPTY)
    val backImageUri = _backImageUri.asStateFlow()

    private val _addCardEventsFlow = MutableSharedFlow<AddCardEvent>()
    val addCardEventsFlow = _addCardEventsFlow.asSharedFlow()

    override fun onCleared() {
        super.onCleared()
        Log.d("ViewM", name.value)
    }
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
    }

    fun load() {
/*        state?.get<Uri>("frontImageUri")?.let {
            _frontImageUri.value = it
        }
        state?.get<Uri>("backImageUri")?.let {
            _backImageUri.value = it
        }
        state?.get<String>("name")?.let {
            _name.value = it
        }*/
        _number.value = state?.get<String>("number") ?: resultArguments?.number ?: ""
        state?.get<BarcodeFormat>("barcodeFormat")?.let {
            _barcodeFormat.value = it } ?: resultArguments?.format
    }

    fun onNameChange(newValue: String) {
        _name.value = newValue
    }

    fun onCardNumberChange(newValue: String) {
        _number.value = newValue
        _imageBitmap.value = BarcodeGenerator()
            .generateBarcode(Barcode(number.value, barcodeFormat.value))
    }

    fun onBarcodeTypeChange(newValue: BarcodeFormat) {
        _barcodeFormat.value = newValue
        _imageBitmap.value = BarcodeGenerator()
            .generateBarcode(Barcode(number.value, newValue))
    }

    fun onSaveClick() {
        val card = LoyaltyCard(
            null, false,
            name.value,
            number.value,
            barcodeFormat.value,
            frontImageUri.value.toString(),
            backImageUri.value.toString()
        )
        viewModelScope.launch {
            val result = cardUseCase(card)
            if (result > 0) {
                _addCardEventsFlow.emit(AddCardEvent.NavigateBackWithResult(RESULT_OK))
            } else {
                _addCardEventsFlow.emit(AddCardEvent.ShowInvalidInputMessage("An error during saving card"))
            }
        }
    }

    fun onScanBarcodeClick() {
        viewModelScope.launch {
            _addCardEventsFlow.emit(AddCardEvent.RequestBarcodeEvent("SCANNER"))
        }
    }

    fun onAddCardClick() {
        viewModelScope.launch {
            _addCardEventsFlow.emit(AddCardEvent.RequestImageEvent("CAPTURE_IMAGE_FRONT"))
        }
    }

    //remove
/*    fun onBarcodeScanned(result: MyResult<*>) {
        when (result) {
            is MyResult.Success -> {
                val barcode = result.data as Barcode
                _number.value = barcode.code
                _barcodeFormat.value = barcode.format
            }
            MyResult.Empty,
            is MyResult.Failure -> {
                onCardCaptureError()
            }
        }
        _cameraMode.value = CameraMode.NOT_ACTIVE
    }*/

    //remove
/*    fun onCardCaptured(events: CameraEvents, image: ImageProxy) {
        viewModelScope.launch {
            val result = pictureUseCase(image)
            when (result) {
                is MyResult.Success -> {
                    if (events == CameraEvents.CAPTURE_IMAGE_FRONT) {
                        _frontImageUri.value = result.data as Uri
                       // _cameraMode.value = CameraMode.CAPTURE_IMAGE_BACK
                    } else {
                        _backImageUri.value = result.data as Uri
                       // _cameraMode.value = CameraMode.NOT_ACTIVE
                    }
                }
                is MyResult.Failure -> {
                    _addCardEventsFlow.emit(AddCardEvent.ShowInvalidInputMessage("An error card image saving occurred, try again"))
                }
            }
        }
    }*/

    fun onLeave() {
        viewModelScope.launch {
            if (frontImageUri.value != Uri.EMPTY) {
                deleteUseCase(frontImageUri.value)
            }
            if (backImageUri.value != Uri.EMPTY) {
                deleteUseCase(backImageUri.value)
            }
            _addCardEventsFlow.emit(AddCardEvent.NavigateBackWithResult(RESULT_OK))
        }
    }
}

sealed class AddCardEvent {
    data class ShowInvalidInputMessage(val msg: String) : AddCardEvent()
    data class NavigateBackWithResult(val resultCode: String) : AddCardEvent()
    data class RequestImageEvent(val resultCode: String) : AddCardEvent()
    data class RequestBarcodeEvent(val resultCode: String) : AddCardEvent()
}
