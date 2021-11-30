package com.rsschool.myapplication.loyaltycards.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rsschool.myapplication.loyaltycards.model.LoyaltyCard
import com.rsschool.myapplication.loyaltycards.model.SearchEvent
import com.rsschool.myapplication.loyaltycards.usecase.LoyaltyCardUseCases
import com.rsschool.myapplication.loyaltycards.usecase.SearchForQueryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CardsDashboardViewModel @Inject constructor(private val useCase: LoyaltyCardUseCases): ViewModel() {

    private val searchQuery = MutableStateFlow("")
    private val cardsFlow = searchQuery.flatMapLatest { useCase.getCards.invoke(it) }

    val cards: StateFlow<List<LoyaltyCard>> =
        cardsFlow.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun onEvent(event: SearchEvent.SearchQueryInput) {
        searchQuery.value = event.input
        Log.d("searchEvent", event.input)
    }

    fun onItemClick(card: LoyaltyCard) {
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