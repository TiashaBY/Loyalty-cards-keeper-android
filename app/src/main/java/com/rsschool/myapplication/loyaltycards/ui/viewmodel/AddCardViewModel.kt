package com.rsschool.myapplication.loyaltycards.ui.viewmodel

import android.graphics.Bitmap
import android.widget.Toast
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.make
import com.google.zxing.BarcodeFormat
import com.rsschool.myapplication.loyaltycards.model.Barcode
import com.rsschool.myapplication.loyaltycards.model.LoyaltyCard
import com.rsschool.myapplication.loyaltycards.usecase.AddCardUseCase
import com.rsschool.myapplication.loyaltycards.usecase.SearchForQueryUseCase
import com.rsschool.myapplication.loyaltycards.utils.BarcodeGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
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
            useCase.invoke(card)
        }
    }
}

sealed class AddCardEvent {
    data class ShowInvalidInputMessage(val msg: String) : AddCardEvent()
    data class NavigateBackWithResult(val result: Int)
}