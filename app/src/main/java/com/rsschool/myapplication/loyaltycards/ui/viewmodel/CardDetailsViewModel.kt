package com.rsschool.myapplication.loyaltycards.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.rsschool.myapplication.loyaltycards.domain.model.LoyaltyCard
import com.rsschool.myapplication.loyaltycards.ui.util.CardSide
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CardDetailsViewModel @Inject constructor(private val state: SavedStateHandle) : ViewModel() {
    val card by lazy { state.get<LoyaltyCard>("card") }

    var cardSide: CardSide = state.get<CardSide>("cardSide") ?: CardSide.FRONT

    fun onCardSideChange() {
        if (cardSide == CardSide.FRONT) {
            cardSide = CardSide.BACK
        } else {
            cardSide = CardSide.FRONT
        }
    }
}
