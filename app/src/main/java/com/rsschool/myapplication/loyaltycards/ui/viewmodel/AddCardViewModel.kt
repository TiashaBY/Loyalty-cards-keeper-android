package com.rsschool.myapplication.loyaltycards.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.zxing.BarcodeFormat
import com.rsschool.myapplication.loyaltycards.domain.model.Barcode
import com.rsschool.myapplication.loyaltycards.domain.model.LoyaltyCard
import com.rsschool.myapplication.loyaltycards.domain.usecase.AddCardUseCase
import com.rsschool.myapplication.loyaltycards.domain.usecase.DeleteCardImagesUseCase
import com.rsschool.myapplication.loyaltycards.domain.utils.MyResult
import com.rsschool.myapplication.loyaltycards.ui.UiConst.RESULT_OK
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AddCardViewModel @Inject constructor(
    private val state: SavedStateHandle?,
    private val addCardUseCase: AddCardUseCase,
    private val deleteImagesUseCase: DeleteCardImagesUseCase
) : ViewModel() {

    private val resultArguments = state?.get<Barcode>("result")

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _number = MutableStateFlow<String>("")
    val number = _number.asStateFlow()

    private val _barcodeFormat = MutableStateFlow<BarcodeFormat?>(null)
    val barcodeFormat = _barcodeFormat.asStateFlow()

    private val _frontImageUri = MutableStateFlow<Uri?>(Uri.EMPTY)
    val frontImageUri = _frontImageUri.asStateFlow()

    private val _backImageUri = MutableStateFlow<Uri?>(Uri.EMPTY)
    val backImageUri = _backImageUri.asStateFlow()

    private val _addCardEventsFlow = MutableSharedFlow<AddCardEvent>()
    val addCardEventsFlow = _addCardEventsFlow.asSharedFlow()

    //recover from process death
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
        state?.get<BarcodeFormat>("number")?.let {
            _barcodeFormat.value = it
        }
    }

    fun load() {
        _number.value = state?.get<String>("number") ?: resultArguments?.number ?: ""
        state?.get<BarcodeFormat>("barcodeFormat")?.let {
            _barcodeFormat.value = it } ?: resultArguments?.format
    }

    fun onNameChange(newValue: String) {
        _name.value = newValue
    }

    fun onCardNumberChange(newValue: String) {
        _number.value = newValue
    }

    fun onBarcodeTypeChange(newValue: BarcodeFormat?) {
        _barcodeFormat.value = newValue
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
            val result = addCardUseCase(card)
            when (result) {
                is MyResult.Success -> {
                    _addCardEventsFlow.emit(AddCardEvent.NavigateBackWithResult(RESULT_OK))
                }
                is MyResult.Failure -> {
                    _addCardEventsFlow.emit(AddCardEvent.ShowInvalidInputMessage("An error during saving card"))
                }
            }
        }
    }

    fun onScanBarcodeClick() {
        viewModelScope.launch {
            _addCardEventsFlow.emit(AddCardEvent.RequestBarcodeEvent)
        }
    }

    fun onAddCardImageClick() {
        viewModelScope.launch {
            _addCardEventsFlow.emit(AddCardEvent.RequestImageEvent)
        }
    }

    fun onLeave() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                frontImageUri.value?.let {
                    if (it.path != "") {
                        deleteImagesUseCase(it)
                    }
                }
                backImageUri.value?.let {
                    if (it.path != "") {
                        deleteImagesUseCase(it)
                    }
                }
                _addCardEventsFlow.emit(AddCardEvent.NavigateBackWithResult(RESULT_OK))
            }
        }
    }

    fun onBarcodeScanned(barcode: Barcode?) {
        _number.value = barcode?.number ?: ""
        _barcodeFormat.value = barcode?.format
    }

    fun onPhotoCaptured(uriList: List<String>?) {
        uriList?.let {
            _frontImageUri.value = Uri.parse(it[0])
            _backImageUri.value = Uri.parse(it[1])
        }
    }
}

sealed class AddCardEvent {
    data class ShowInvalidInputMessage(val msg: String) : AddCardEvent()
    data class NavigateBackWithResult(val resultCode: String) : AddCardEvent()
    object RequestImageEvent : AddCardEvent()
    object RequestBarcodeEvent : AddCardEvent()
}
