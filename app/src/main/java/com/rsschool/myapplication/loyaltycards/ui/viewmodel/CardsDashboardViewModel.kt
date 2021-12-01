package com.rsschool.myapplication.loyaltycards.ui.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.rsschool.myapplication.loyaltycards.model.LoyaltyCard
import com.rsschool.myapplication.loyaltycards.ui.viewmodel.base.BaseCardsViewModel
import com.rsschool.myapplication.loyaltycards.usecase.GetUserAuthStateUseCase
import com.rsschool.myapplication.loyaltycards.usecase.LogoutUseCase
import com.rsschool.myapplication.loyaltycards.usecase.LoyaltyCardUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class CardsDashboardViewModel @Inject constructor(private val cardUseCase: LoyaltyCardUseCases,
                                                  private val logoutUseCase : LogoutUseCase,
                                                  private val userAuthStateUseCase: GetUserAuthStateUseCase,
                                                  state: SavedStateHandle): BaseCardsViewModel(cardUseCase) {

    private val _searchQuery = MutableStateFlow(state.get<String>("searchQuery") ?: "")
    val searchQueryValue = _searchQuery.value

    private val cardsFlow = _searchQuery.flatMapLatest { cardUseCase.getCards.invoke(it) }
    val cards: StateFlow<List<LoyaltyCard>> = cardsFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val _authState = MutableStateFlow(userAuthStateUseCase.invoke().value)
    val authState = _authState.asStateFlow()

    fun onSearchQueryChange(query : String) {
        _searchQuery.value = query
        Log.d("dashboardEvent", "search=$query")
    }

    fun onLogoutClick() {
        logoutUseCase.invoke()
    }
}
