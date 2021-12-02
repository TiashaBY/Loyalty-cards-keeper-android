package com.rsschool.myapplication.loyaltycards.ui.viewmodel.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rsschool.myapplication.loyaltycards.domain.model.LoyaltyCard
import com.rsschool.myapplication.loyaltycards.domain.usecase.LoyaltyCardUseCases
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.DBResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class BaseCardsViewModel @Inject constructor(private val useCase: LoyaltyCardUseCases): ViewModel() {

    protected val _uiState = MutableStateFlow<DBResult>(DBResult.Empty())
    val uiState = _uiState.asStateFlow()

    protected val _isListEmpty = MutableStateFlow(false)
    val islistEmpty = _isListEmpty.asStateFlow()

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
