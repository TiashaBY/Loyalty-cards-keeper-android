package com.rsschool.myapplication.loyaltycards.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.rsschool.myapplication.loyaltycards.domain.model.Barcode
import com.rsschool.myapplication.loyaltycards.domain.model.LoyaltyCard
import com.rsschool.myapplication.loyaltycards.ui.util.BarcodeGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CardDetailsViewModel @Inject constructor(private val state: SavedStateHandle): ViewModel() {
    private val card by lazy { state.get<LoyaltyCard>("card") }

    val cardNumber = card?.cardNumber ?: ""
    val bitmap = BarcodeGenerator()
        .generateBarcode(Barcode(cardNumber, card?.barcodeType))
}