package com.rsschool.myapplication.loyaltycards.ui.viewmodel.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rsschool.myapplication.loyaltycards.domain.model.LoyaltyCard
import com.rsschool.myapplication.loyaltycards.domain.usecase.LoyaltyCardUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class BaseCardsViewModel @Inject constructor(private val useCase: LoyaltyCardUseCases): ViewModel() {

    fun onItemDetailsClick(card: LoyaltyCard) {
        // useCase.getCards.invoke(card)
    }

    fun onFavIconClick(card: LoyaltyCard, checked: Boolean) {
        viewModelScope.launch {
            useCase.updateFavorites(card, checked)
        }
    }

    fun onDeleteIconClick(card: LoyaltyCard) {
        viewModelScope.launch {
            useCase.deleteCard(card)
        }
    }
}
