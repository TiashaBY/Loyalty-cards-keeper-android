package com.rsschool.myapplication.loyaltycards.ui.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.zxing.BarcodeFormat
import com.rsschool.myapplication.loyaltycards.model.Barcode
import com.rsschool.myapplication.loyaltycards.model.LoyaltyCard
import com.rsschool.myapplication.loyaltycards.usecase.AddCardUseCase
import com.rsschool.myapplication.loyaltycards.utils.BarcodeGenerator
import com.rsschool.myapplication.loyaltycards.utils.Constants.ADD_TASK_RESULT_OK
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddCardViewModel @Inject constructor(private val state: SavedStateHandle?,
                                           private val useCase: AddCardUseCase
) : ViewModel() {

    private var _number = MutableStateFlow(state?.get<Barcode?>("barcode")?.code)
    val number = _number.asStateFlow()

    private var _barcodeFormat = MutableStateFlow(state?.get<Barcode?>("barcode")?.format)
    val barcodeFormat = _barcodeFormat.asStateFlow()

    private var _imageBitmap = MutableStateFlow<Bitmap?>(null)
    val imageBitmap = _imageBitmap.asStateFlow()

    private val name = MutableStateFlow("")

    private val addCardEventChannel = Channel<AddCardEvent>()
    val event = addCardEventChannel.receiveAsFlow().stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun onCardNumberChange(newValue : String) {
        _number.value = newValue
        _imageBitmap.value = BarcodeGenerator()
            .generateBarcode(Barcode(number.value ?: "", barcodeFormat.value))
    }

    fun onFormatChange(newValue : BarcodeFormat?) {
        _barcodeFormat.value = newValue
        _imageBitmap.value = BarcodeGenerator()
            .generateBarcode(Barcode(number.value ?: "", newValue))
    }

    fun onNameChange(newValue : String) {
        name.value = newValue
    }

    fun onSaveClick() {
        if (name.value.isEmpty()) {
            //
        }
        if (number.value?.isEmpty() == true) {

        }
        if (barcodeFormat.value != null && imageBitmap.value == null) {

        }
        val card = LoyaltyCard(null,"user_id", false,
            name.value,
            number.value!!,
            barcodeFormat.value)
        viewModelScope.launch {
            val result = useCase.invoke(card)
            if (result > 0) {
                addCardEventChannel.send(AddCardEvent.NavigateBackWithResult(ADD_TASK_RESULT_OK))
            } else {
                addCardEventChannel.send(AddCardEvent.ShowInvalidInputMessage("Message"))
            }
        }
    }
}

sealed class AddCardEvent {
    class ShowInvalidInputMessage(val msg: String) : AddCardEvent()
    class NavigateBackWithResult(val resultCode: String) : AddCardEvent()
    class NavigateToCameraPreview(val result: Int) : AddCardEvent()
}