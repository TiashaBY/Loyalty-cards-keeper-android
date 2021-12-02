package com.rsschool.myapplication.loyaltycards.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.rsschool.myapplication.loyaltycards.domain.model.LoyaltyCard
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.base.BaseCardsViewModel
import com.rsschool.myapplication.loyaltycards.domain.usecase.LoyaltyCardUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class FavouriteCardsViewModel @Inject constructor(private val useCase: LoyaltyCardUseCases): BaseCardsViewModel(useCase) {

    private val cardsFlow = useCase.getFavoriteCards.invoke()
    val cards: StateFlow<List<LoyaltyCard>> =
        cardsFlow.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
}
