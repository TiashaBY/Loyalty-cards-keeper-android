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

    private val _number : MutableStateFlow<String?> by lazy {
        if (state?.get<CameraResultEvent>("cameraResult") is CameraResultEvent.BarcodeScanned?)
            MutableStateFlow(state?.get<CameraResultEvent.BarcodeScanned?>("cameraResult")?.barcode?.code)
        else MutableStateFlow(null)
    }
    val number = _number.asStateFlow()

    private val _barcodeFormat : MutableStateFlow<BarcodeFormat?> by lazy {
        if (state?.get<CameraResultEvent>("cameraResult") is CameraResultEvent.BarcodeScanned?)
            MutableStateFlow(state?.get<CameraResultEvent.BarcodeScanned?>("cameraResult")?.barcode?.format)
        else MutableStateFlow(null)
    }
    val barcodeFormat = _barcodeFormat.asStateFlow()

    private var _imageBitmap = MutableStateFlow<Bitmap?>(null)
    val imageBitmap = _imageBitmap.asStateFlow()

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _frontImageUri : MutableStateFlow<Uri?> by lazy {
        if (state?.get<CameraResultEvent>("cameraResult") is CameraResultEvent.ImageSaved?
            && (state?.get<CameraResultEvent.ImageSaved?>("cameraResult")?.type == CardImageType.FRONT))
            MutableStateFlow(state.get<CameraResultEvent.ImageSaved?>("cameraResult")?.imageUri)
        else MutableStateFlow(Uri.EMPTY)
    }
    val frontImageUri = _frontImageUri.asStateFlow()

    private val _backImageUri : MutableStateFlow<Uri?> by lazy {
        if (state?.get<CameraResultEvent>("cameraResult") is CameraResultEvent.ImageSaved?
            && (state?.get<CameraResultEvent.ImageSaved?>("cameraResult")?.type == CardImageType.BACK)
        ) MutableStateFlow(state.get<CameraResultEvent.ImageSaved?>("cameraResult")?.imageUri)
        else MutableStateFlow(Uri.EMPTY)
    }
    val backImageUri = _backImageUri.asStateFlow()

    private val addCardEventChannel = Channel<AddCardEvent>()
    val event =
        addCardEventChannel.receiveAsFlow().stateIn(viewModelScope, SharingStarted.Eagerly, null)

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
            val result = useCase.invoke(card)
            if (result > 0) {
                addCardEventChannel.send(AddCardEvent.NavigateBackWithResult(ADD_TASK_RESULT_OK))
            } else {
                addCardEventChannel.send(AddCardEvent.ShowInvalidInputMessage("Message"))
            }
        }
    }

    fun onScanBarcodeClick() {
        viewModelScope.launch {
            addCardEventChannel.send(AddCardEvent.NavigateToCameraScanBarcode())
        }
    }

    fun addCardFront() {
        viewModelScope.launch {
            addCardEventChannel.send(AddCardEvent.NavigateToCameraTakeFrontImage())
        }
    }
    fun addCardBack() {
        viewModelScope.launch {
            addCardEventChannel.send(AddCardEvent.NavigateToCameraTakeBackImage())
        }
    }
}

sealed class AddCardEvent {
    class ShowInvalidInputMessage(val msg: String) : AddCardEvent()
    class NavigateBackWithResult(val resultCode: String) : AddCardEvent()
    class NavigateToCameraScanBarcode : AddCardEvent()
    class NavigateToCameraTakeFrontImage : AddCardEvent()
    class NavigateToCameraTakeBackImage : AddCardEvent()
}